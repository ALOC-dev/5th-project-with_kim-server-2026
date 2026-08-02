package com.with_kim.aloc_study.repository;

import com.with_kim.aloc_study.dto.NearbyInfo;
import com.with_kim.aloc_study.entity.Building;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NearbyInfoRepository {
    private final EntityManager em;
    private static final int CAMPUS_RADIUS = 1200;   // m 학교까지의 거리 제한
    private static final int CCTV_RADIUS = 200;      // m
    private static final int METERS_PER_MINUTE = 70; // 도보로 변환
    private static final int SUBWAY_RADIUS = 800;   // m, 도보 역세권기준
    private static final int POLICE_RADIUS = 500;   // m

    @SuppressWarnings("unchecked")
    public NearbyInfo findFor(Building b) {
        if (b.getLocation() == null) {
            return NearbyInfo.empty();
        }

        // 가장 가까운 학교 건물 1개
        List<Object[]> campusBuilding = em.createNativeQuery("""
                SELECT s.building_name,ST_Distance(CAST(s.location AS geography), CAST(:loc AS geography)) AS dist
                FROM school_buildings s
                WHERE ST_DWithin(CAST(s.location AS geography), CAST(:loc AS geography), :r)
                ORDER BY dist
                LIMIT 1
                """)
                .setParameter("loc", b.getLocation())
                .setParameter("r", CAMPUS_RADIUS)
                .getResultList();

        //가장 가까운 지하철역 1개
        List<Object[]> subway=em.createNativeQuery("""
                SELECT i.name, ST_Distance(CAST(i.location AS geography), CAST(:loc AS geography)) AS dist
                FROM infrastructures i
                WHERE i.category = 'SUBWAY'
                AND ST_DWithin(CAST(i.location AS geography), CAST(:loc AS geography), :r)
                ORDER BY dist
                LIMIT 1
                """)
                .setParameter("loc", b.getLocation())
                .setParameter("r",SUBWAY_RADIUS)
                .getResultList();

        // 반경 내 CCTV 개수
        Number cctv = (Number) em.createNativeQuery("""
                SELECT COUNT(*) 
                FROM infrastructures i
                WHERE i.category = 'CCTV'
                AND ST_DWithin(CAST(i.location AS geography), CAST(:loc AS geography), :r)
                """)
                .setParameter("loc", b.getLocation())
                .setParameter("r", CCTV_RADIUS)
                .getSingleResult();

        // 반경 내 경찰서 개수
        Number police = (Number) em.createNativeQuery("""
                SELECT COUNT(*)
                FROM infrastructures i
                WHERE i.category = 'POLICE'
                AND ST_DWithin(CAST(i.location AS geography), CAST(:loc AS geography), :r)
                """)
                .setParameter("loc", b.getLocation())
                .setParameter("r", POLICE_RADIUS)
                .getSingleResult();

        //캠퍼스 건물 이름
        String campusName = null;
        //캠퍼스 건물 거리
        Integer campusMin = null;
        Integer campusMeters = null;

        if(!campusBuilding.isEmpty()){
            campusName=(String) campusBuilding.get(0)[0];
            campusMeters=(int)Math. ceil(((Number)campusBuilding.get(0)[1]).doubleValue());
            campusMin=(int)Math. ceil(((Number)campusBuilding.get(0)[1]).doubleValue()/METERS_PER_MINUTE);
        }

        //지하철역 이름
        String subwayName=null;
        //지하철 거리
        Integer subwayMeters=null;
        if(!subway.isEmpty()){
            subwayName=(String) subway.get(0)[0];
            subwayMeters=(int)Math.ceil(((Number) subway.get(0)[1]).doubleValue());
        }

        return new NearbyInfo(campusName, campusMin, campusMeters,cctv.intValue(),subwayName,subwayMeters,police.intValue());
    }
}
