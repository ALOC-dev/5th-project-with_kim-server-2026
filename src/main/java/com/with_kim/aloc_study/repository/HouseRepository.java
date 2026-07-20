package com.with_kim.aloc_study.repository;

import com.with_kim.aloc_study.entity.House;
import com.with_kim.aloc_study.repository.projection.HouseSchoolDistanceProjection;
import com.with_kim.aloc_study.repository.projection.HouseWithDistanceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface HouseRepository extends JpaRepository<House, Long> {
    //모든 매물 찾기
    @Query(value = "SELECT h FROM House h JOIN FETCH h.building",
            countQuery = "SELECT COUNT(h) FROM House h")
    Page<House> findAllWithBuilding(Pageable pageable);

    //ID로 매물 찾기
    @Query("SELECT h FROM House h JOIN FETCH h.building WHERE h.id = :id")
    Optional<House> findByIdWithBuilding(@Param("id") Long id);

    //원형 범위 검색
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

    //사각형 범위 검색
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

    // 특정 학교 건물에서 지정 거리 이내의 매물 id 검색
    @Query(value = """
    SELECT h.id FROM houses h
    JOIN buildings b ON h.building_id = b.id, school_buildings sb
    WHERE sb.id = :schoolBuildingId
    AND ST_DWithin(sb.location::geography, b.location::geography, :radiusMeters)
    """,
            nativeQuery = true)
    Set<Long> findHouseIdsNearSchool(@Param("schoolBuildingId") Long schoolBuildingId,
                                     @Param("radiusMeters") Double radiusMeters);

    //Metadata 조건 검색
    @Query(value = """
    SELECT h.id FROM houses h
    WHERE h.id IN (:houseIds)
    AND (:minMart IS NULL OR (h.metadata::jsonb->>'martCount')::int >= :minMart)
    AND (:minConvenienceStore IS NULL OR (h.metadata::jsonb->>'convenienceStoreCount')::int >= :minConvenienceStore)
    AND (:minParking IS NULL OR (h.metadata::jsonb->>'parkingCount')::int >= :minParking)
    AND (:minSubway IS NULL OR (h.metadata::jsonb->>'subwayCount')::int >= :minSubway)
    AND (:minBank IS NULL OR (h.metadata::jsonb->>'bankCount')::int >= :minBank)
    AND (:minPO IS NULL OR (h.metadata::jsonb->>'POCount')::int >= :minPO)
    AND (:minRestaurant IS NULL OR (h.metadata::jsonb->>'restaurantCount')::int >= :minRestaurant)
    AND (:minCafe IS NULL OR (h.metadata::jsonb->>'cafeCount')::int >= :minCafe)
    AND (:minHospital IS NULL OR (h.metadata::jsonb->>'hospitalCount')::int >= :minHospital)
    AND (:minPharmacy IS NULL OR (h.metadata::jsonb->>'pharmacyCount')::int >= :minPharmacy)
    """,
            nativeQuery = true)
    List<Long> findByIdsWithMetadataCondition(
            @Param("houseIds") List<Long> houseIds,
            @Param("minMart") Integer minMart,
            @Param("minConvenienceStore") Integer minConvenienceStore,
            @Param("minParking") Integer minParking,
            @Param("minSubway") Integer minSubway,
            @Param("minBank") Integer minBank,
            @Param("minPO") Integer minPO,
            @Param("minRestaurant") Integer minRestaurant,
            @Param("minCafe") Integer minCafe,
            @Param("minHospital") Integer minHospital,
            @Param("minPharmacy") Integer minPharmacy);

    //조회
    @Query(value = """
    SELECT h.id FROM houses h
    WHERE h.id IN (:houseIds)
    ORDER BY
        CASE WHEN :sort = 'PRICE_ASC'  THEN h.price END ASC,
        CASE WHEN :sort = 'PRICE_DESC' THEN h.price END DESC,
        CASE WHEN :sort = 'AREA_ASC'   THEN h.area  END ASC,
        CASE WHEN :sort = 'AREA_DESC'  THEN h.area  END DESC,
        CASE WHEN :sort = 'FLOOR_ASC'  THEN h.floor END ASC,
        CASE WHEN :sort = 'FLOOR_DESC' THEN h.floor END DESC,
        h.id ASC
    LIMIT :limit OFFSET :offset
    """,
            nativeQuery = true)
    List<Long> findSortedIds(@Param("houseIds") List<Long> houseIds,
                             @Param("sort") String sort,
                             @Param("limit") int limit,
                             @Param("offset") long offset);

    //정렬
    @Query("SELECT h FROM House h JOIN FETCH h.building WHERE h.id IN :ids")
    List<House> findAllByIdInWithBuilding(@Param("ids") List<Long> ids);




    @Modifying
    @Query("UPDATE House h SET h.metadata = :metadata, h.metadataUpdatedAt = :updatedAt WHERE h.id = :houseId")
    void updateMetadata(@Param("houseId") Long houseId,
                        @Param("metadata") String metadata,
                        @Param("updatedAt") LocalDateTime updatedAt);
}









