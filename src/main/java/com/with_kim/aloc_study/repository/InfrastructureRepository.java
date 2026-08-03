package com.with_kim.aloc_study.repository;

import com.with_kim.aloc_study.entity.Infrastructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InfrastructureRepository extends JpaRepository<Infrastructure, Long> {

    // 특정 카테고리 인프라 목록 조회
    @Query("SELECT i FROM Infrastructure i WHERE i.category = :category")
    List<Infrastructure> findByCategory(@Param("category") Infrastructure.InfrastructureCategory category);

    // 인프라 명칭 기준 조회
    @Query("SELECT i FROM Infrastructure i WHERE i.name= :name")
    List<Infrastructure> findByNameContaining(@Param("name") String name);

    // 특정 매물 주변 인프라 목록 조회
    @Query(value = """
            SELECT i.* FROM infrastructures i
            JOIN houses h ON h.id = :houseId
            JOIN buildings b ON h.building_id = b.id
            WHERE ST_DWithin(
                i.location::geography,
                b.location::geography,
                :radiusMeters
            )
            ORDER BY ST_Distance(i.location::geography, b.location::geography) ASC
            """,
            nativeQuery = true)
    List<Infrastructure> findNearbyByHouseId(@Param("houseId") Long houseId,
                                             @Param("radiusMeters") Double radiusMeters);

    //좌표, 반경 지정하고 특정 카테고리의 인프라 개수 반환
    @Query(value = """
    SELECT COUNT(*) FROM infrastructures i
    WHERE i.category = :category
    AND ST_DWithin(
        i.location::geography,
        ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
        :radiusMeters
    )
    """,
            nativeQuery = true)
    int countByCategoryWithinRadius(@Param("category") String category,
                                    @Param("lat") Double lat,
                                    @Param("lng") Double lng,
                                    @Param("radiusMeters") Double radiusMeters);
}

