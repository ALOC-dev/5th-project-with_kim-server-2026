package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.dto.response.NeighborhoodPriceStatisticsResponse;
import com.with_kim.aloc_study.entity.Building;
import com.with_kim.aloc_study.entity.House;
import com.with_kim.aloc_study.exception.ResourceNotFoundException;
import com.with_kim.aloc_study.repository.HouseRepository;
import com.with_kim.aloc_study.repository.HouseStatisticsRepository;
import com.with_kim.aloc_study.repository.projection.NeighborhoodPriceStatisticsProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HouseStatisticsService {

    private final HouseStatisticsRepository houseStatisticsRepository;
    private final HouseRepository houseRepository;

    public NeighborhoodPriceStatisticsResponse
    getPriceStatisticsByNeighborhood(
            String sggCd,
            String emdCd
    ) {
        NeighborhoodPriceStatisticsProjection projection =
        houseStatisticsRepository
                .findPriceStatisticsByNeighborhood(sggCd, emdCd)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "해당 법정동의 매물 통계를 찾을 수 없습니다."
                        )
                );
        return NeighborhoodPriceStatisticsResponse.from(projection);
    }

    public NeighborhoodPriceStatisticsResponse
    getPriceStatisticsByHouseId(Long houseId) {

        House house = houseRepository.findByIdWithBuilding(houseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "매물을 찾을 수 없습니다. id=" + houseId
                        )
                );

        Building building = house.getBuilding();

        if (building.getSggCd() == null || building.getEmdCd() == null) {
            throw new ResourceNotFoundException(
                    "매물의 법정동 정보를 찾을 수 없습니다. id=" + houseId
            );
        }

        return getPriceStatisticsByNeighborhood(
                building.getSggCd(),
                building.getEmdCd()
        );
    }
}