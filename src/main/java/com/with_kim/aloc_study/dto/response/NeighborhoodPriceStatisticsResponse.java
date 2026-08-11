package com.with_kim.aloc_study.dto.response;

import com.with_kim.aloc_study.repository.projection.NeighborhoodPriceStatisticsProjection;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record NeighborhoodPriceStatisticsResponse(
        String districtCode,
        String districtName,
        String neighborhoodCode,
        String neighborhoodName,

        Long saleListingCount,
        BigDecimal averageSalePrice,
        BigDecimal averageSaleManagementFee,

        Long jeonseListingCount,
        BigDecimal averageJeonseDeposit,
        BigDecimal averageJeonseManagementFee,

        Long monthlyRentListingCount,
        BigDecimal averageMonthlyDeposit,
        BigDecimal averageMonthlyRent,
        BigDecimal averageMonthlyManagementFee
){
        public static NeighborhoodPriceStatisticsResponse from(
                NeighborhoodPriceStatisticsProjection projection
        ){
            return new NeighborhoodPriceStatisticsResponse(
                    projection.getDistrictCode(),
                    projection.getDistrictName(),
                    projection.getNeighborhoodCode(),
                    projection.getNeighborhoodName(),

                    projection.getSaleListingCount(),
                    round(projection.getAverageSalePrice()),
                    round(projection.getAverageSaleManagementFee()),

                    projection.getJeonseListingCount(),
                    round(projection.getAverageJeonseDeposit()),
                    round(projection.getAverageJeonseManagementFee()),

                    projection.getMonthlyRentListingCount(),
                    round(projection.getAverageMonthlyDeposit()),
                    round(projection.getAverageMonthlyRent()),
                    round(projection.getAverageMonthlyManagementFee())
            );
        }

        private static BigDecimal round(BigDecimal value){
            if (value == null) {
                return null;
            }

            return value.setScale(0, RoundingMode.HALF_UP);
        }

}
