package com.with_kim.aloc_study.repository.projection;

import java.math.BigDecimal;

public interface NeighborhoodPriceStatisticsProjection {
    String getDistrictCode();

    String getDistrictName();

    String getNeighborhoodCode();

    String getNeighborhoodName(); //동명

    Long getSaleListingCount(); //현재 동의 매매 매물 수

    BigDecimal getAverageSalePrice(); //매매가 평균

    BigDecimal getAverageSaleManagementFee();

    Long getJeonseListingCount(); //현재 동의 전세 매물 수

    BigDecimal getAverageJeonseDeposit(); //전세 보증금 평균

    BigDecimal getAverageJeonseManagementFee();

    Long getMonthlyRentListingCount(); //현재 동의 월세 매물 수

    BigDecimal getAverageMonthlyDeposit(); //월세 보증금 평균

    BigDecimal getAverageMonthlyRent(); //월세 평균

    BigDecimal getAverageMonthlyManagementFee();
}