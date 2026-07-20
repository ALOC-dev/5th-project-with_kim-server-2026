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

        //캠퍼스 건물 이름
        String campusName = null;
        //캠퍼스 건물 거리
        Integer campusMin = null;
        if(!campusBuilding.isEmpty()){
            campusName=(String) campusBuilding.get( 0)[0];
            campusMin=(int)Math. ceil(((Number)campusBuilding.get(0)[1]).doubleValue()/METERS_PER_MINUTE);
        }
        return new NearbyInfo(campusName, campusMin, cctv.intValue());
    }
}
