package com.with_kim.aloc_study.repository;

import com.with_kim.aloc_study.entity.House;
import com.with_kim.aloc_study.repository.projection.HouseSchoolDistanceProjection;
import com.with_kim.aloc_study.repository.projection.HouseWithDistanceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HouseRepository extends JpaRepository<House, Long> {
    //모든 매물 찾기
    @Query("SELECT h FROM House h JOIN FETCH h.building")
    List<House> findAllWithBuilding();

    //ID로 매물 찾기
    @Query("SELECT h FROM House h JOIN FETCH h.building WHERE h.id = :id")
    Optional<House> findByIdWithBuilding(@Param("id") Long id);

    //중심, 반지름 지정해서 중심으로부터의 거리 순으로 정렬(주로 사용)
    @Query(value = """
        SELECT h.* FROM houses h
        JOIN buildings b ON h.building_id = b.id
        WHERE ST_DWithin(
            b.location::geography,
            ST_SetSRID(ST_MakePoint(:centerLng, :centerLat), 4326)::geography,
            :radiusMeters
        )
        """,
            nativeQuery = true)
    List<House> findAllWithinRadius(@Param("centerLng") Double centerLng,
                                    @Param("centerLat") Double centerLat,
                                    @Param("radiusMeters") Double radiusMeters);

    // 사각형 범위 검색
    @Query(value = """
            SELECT h.* FROM houses h
            JOIN buildings b ON h.building_id = b.id
            WHERE ST_Contains(
                ST_MakeEnvelope(:swLng, :swLat, :neLng, :neLat, 4326),
                b.location::geometry
            )
            """,
            nativeQuery = true)
    List<House> findAllInBoundingBox(@Param("swLat") Double swLat,
                                     @Param("swLng") Double swLng,
                                     @Param("neLat") Double neLat,
                                     @Param("neLng") Double neLng);

    // 특정 매물과 학교 건물들의 거리
    @Query(value = """
        SELECT h.id AS houseId,
               sb.id AS schoolBuildingId,
               sb.building_name AS schoolBuildingName,
               ST_Distance(b.location::geography, sb.location::geography) AS distanceMeters
        FROM houses h
        JOIN buildings b ON h.building_id = b.id, school_buildings sb
        WHERE h.id = :houseId
        ORDER BY distanceMeters ASC
        """,
            nativeQuery = true)
    List<HouseSchoolDistanceProjection> findAllSchoolDistancesByHouseId(@Param("houseId") Long houseId);

    // 특정 학교 건물에서 지정 거리 이내의 매물
    @Query(value = """
        SELECT h.id AS houseId,
               b.id AS buildingId,
               b.address AS address,
               h.price AS price,
               h.area AS area,
               ST_Distance(sb.location::geography, b.location::geography) AS distanceMeters
        FROM houses h
        JOIN buildings b ON h.building_id = b.id, school_buildings sb
        WHERE sb.id = :schoolBuildingId
        AND ST_DWithin(sb.location::geography, b.location::geography, :radiusMeters)
        ORDER BY distanceMeters ASC
        """,
            nativeQuery = true)
    List<HouseWithDistanceProjection> findHousesNearSchool(@Param("schoolBuildingId") Long schoolBuildingId,
                                                           @Param("radiusMeters") Double radiusMeters);
}
