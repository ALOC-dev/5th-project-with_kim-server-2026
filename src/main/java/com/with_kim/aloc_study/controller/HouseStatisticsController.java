package com.with_kim.aloc_study.controller;

import com.with_kim.aloc_study.dto.response.NeighborhoodPriceStatisticsResponse;
import com.with_kim.aloc_study.service.HouseStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "House Statistics",
        description = "매물 통계 조회 API"
)
@RestController
@RequestMapping("/api/house-statistics")
@RequiredArgsConstructor
public class HouseStatisticsController {

    private final HouseStatisticsService houseStatisticsService;

    @Operation(
            summary = "법정동의 거래 유형별 평균 금액 데이터 조회",
            description = """
                    선택한 법정동의 매매 평균 매매가, 전세 평균 보증금,
                    월세 평균 보증금과 평균 월 임대료, 관리비를 조회합니다.
                    """
    )
    @GetMapping("/neighborhood-prices")
    public NeighborhoodPriceStatisticsResponse
    getPriceStatisticsByNeighborhood(
            @Parameter(description = "통계를 확인할 법정동의 시군구 코드") @RequestParam String sggCd,
            @Parameter(description = "통계를 확인할 법정동의 읍면동 코드") @RequestParam String emdCd
    ) {

        return houseStatisticsService.getPriceStatisticsByNeighborhood(sggCd, emdCd);
    }

    @Operation(
            summary = "선택한 매물과 같은 법정동의 평균 금액 데이터 조회",
            description = """
                    매물이 속한 법정동의 매매 평균 매매가, 전세 평균 보증금,
                    월세 평균 보증금과 평균 월 임대료, 관리비를 조회합니다.
                    """
    )
    @GetMapping("/neighborhood-prices/by-house/{houseId}")
    public NeighborhoodPriceStatisticsResponse
    getPriceStatisticsByHouseId(
            @Parameter(description = "주변 매물 통계를 확인할 매물 Id") @PathVariable Long houseId
    ) {

        return houseStatisticsService.getPriceStatisticsByHouseId(houseId);
    }


}