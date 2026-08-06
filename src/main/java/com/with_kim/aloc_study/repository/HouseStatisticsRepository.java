package com.with_kim.aloc_study.repository;

import com.with_kim.aloc_study.entity.House;
import com.with_kim.aloc_study.repository.projection.NeighborhoodPriceStatisticsProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HouseStatisticsRepository extends Repository<House, Long>{

    //동별, 계약유형별 매매가, 보증금, 월세, 관리비 통계
    @Query(value = """
        SELECT
            b.sgg_cd AS districtCode,
            b.sgg_name AS districtName,
            b.emd_cd AS neighborhoodCode,
            b.emd_name AS neighborhoodName,

            COUNT(*) FILTER (
                WHERE h.contract_type = 'SALE'
            ) AS saleListingCount,

            AVG(NULLIF(h.price, 0)) FILTER (
                WHERE h.contract_type = 'SALE'
            ) AS averageSalePrice,

            AVG(h.management_fee) FILTER (
                WHERE h.contract_type = 'SALE'
            ) AS averageSaleManagementFee,

            COUNT(*) FILTER (
                WHERE h.contract_type = 'JEONSE'
            ) AS jeonseListingCount,

            AVG(NULLIF(h.deposit, 0)) FILTER (
                WHERE h.contract_type = 'JEONSE'
            ) AS averageJeonseDeposit,

            AVG(h.management_fee) FILTER (
                WHERE h.contract_type = 'JEONSE'
            ) AS averageJeonseManagementFee,

            COUNT(*) FILTER (
                WHERE h.contract_type = 'MONTHLY'
            ) AS monthlyRentListingCount,

            AVG(h.deposit) FILTER (
                WHERE h.contract_type = 'MONTHLY'
            ) AS averageMonthlyDeposit,

            AVG(NULLIF(h.monthly_rent, 0)) FILTER (
                WHERE h.contract_type = 'MONTHLY'
            ) AS averageMonthlyRent,

            AVG(h.management_fee) FILTER (
                WHERE h.contract_type = 'MONTHLY'
            ) AS averageMonthlyManagementFee

        FROM houses h
        JOIN buildings b ON h.building_id = b.id

        WHERE b.sgg_cd = :sggCd
          AND b.emd_cd = :emdCd
          AND h.contract_type IN ('SALE', 'JEONSE', 'MONTHLY')

        GROUP BY
            b.sgg_cd,
            b.sgg_name,
            b.emd_cd,
            b.emd_name
        """, nativeQuery = true)
    Optional<NeighborhoodPriceStatisticsProjection>
    findPriceStatisticsByNeighborhood(
            @Param("sggCd") String sggCd,
            @Param("emdCd") String emdCd
    );
}
