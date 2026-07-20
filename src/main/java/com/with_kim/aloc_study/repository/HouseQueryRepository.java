package com.with_kim.aloc_study.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.with_kim.aloc_study.dto.HouseSearchCondition;
import com.with_kim.aloc_study.entity.House;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

import static com.with_kim.aloc_study.entity.QBuilding.building;
import static com.with_kim.aloc_study.entity.QHouse.house;

@Repository
@RequiredArgsConstructor
public class HouseQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final HouseRepository houseRepository;
    private static final double DEFAULT_SCHOOL_DISTANCE_METERS = 750;

    public Page<House> searchHouses(HouseSearchCondition condition, Pageable pageable) {



        Set<Long> houseIds = null;

        // 1. 기본 조건 필터링
        if (hasBasicCondition(condition)) {
            houseIds = new HashSet<>(queryFactory
                    .select(house.id)
                    .from(house)
                    .join(house.building, building)
                    .where(
                            contractTypeEq(condition.contractType()),
                            priceBetween(condition.minPrice(), condition.maxPrice()),
                            areaBetween(condition.minArea(), condition.maxArea()),
                            minRoomNumberGoe(condition.minRoomNumber()),
                            maxManagementFeeLoe(condition.maxManagementFee()),
                            floorBetween(condition.minFloor(), condition.maxFloor())
                    )
                    .fetch());

            if (houseIds.isEmpty()) return Page.empty(pageable);
        }

        // 2. 위치 필터링
        if (hasBoundingBox(condition) || hasCircle(condition)) {
            Set<Long> locationIds = hasBoundingBox(condition)
                    ? houseRepository.findAllInBoundingBox(
                            condition.swLat(), condition.swLng(),
                            condition.neLat(), condition.neLng())
                    .stream().map(House::getId).collect(Collectors.toSet())
                    : houseRepository.findAllWithinRadius(
                            condition.centerLng(), condition.centerLat(),
                            condition.radiusMeters())
                    .stream().map(House::getId).collect(Collectors.toSet());

            if (houseIds == null) {
                houseIds = locationIds;
            } else {
                houseIds.retainAll(locationIds);
            }

            if (houseIds.isEmpty()) return Page.empty(pageable);
        }

        if (houseIds == null) {
            houseIds = new HashSet<>(queryFactory.select(house.id).from(house).fetch());
        }

        //3. 학교 건물 거리 필터링
        if (hasSchoolBuildingCondition(condition)) {
            Set<Long> filtered = houseRepository.findHouseIdsNearSchool(
                    condition.schoolBuildingId(),
                    condition.maxDistanceFromSchool()   // radiusMeters() → maxDistanceFromSchool()
            );

            if (houseIds == null) {
                houseIds = filtered;
            } else {
                houseIds.retainAll(filtered);
            }

            if (houseIds.isEmpty()) return Page.empty(pageable);
        }

        //metadata 필터링
        if (hasMetadataCondition(condition)) {
            List<Long> filtered = houseRepository.findByIdsWithMetadataCondition(
                    new ArrayList<>(houseIds),
                    condition.minMart(),
                    condition.minConvenienceStore(),
                    condition.minParking(),
                    condition.minSubway(),
                    condition.minBank(),
                    condition.minPO(),
                    condition.minRestaurant(),
                    condition.minCafe(),
                    condition.minHospital(),
                    condition.minPharmacy()
            );
            houseIds = new HashSet<>(filtered);

            if (houseIds.isEmpty()) return Page.empty(pageable);
        }

        List<Long> idList = new ArrayList<>(houseIds);
        long total = idList.size();

        List<Long> sortedIds = houseRepository.findSortedIds(idList, condition.sort(), pageable.getPageSize(), pageable.getOffset());
        List<House> houses = houseRepository.findAllByIdInWithBuilding(sortedIds);

        Map<Long, House> houseMap = houses.stream().collect(Collectors.toMap(House::getId, h -> h));
        List<House> orderedContent = sortedIds.stream().map(houseMap::get).toList();
        // 조회, 정렬 분리(n+1 문제)

        return new PageImpl<>(orderedContent, pageable, total);
    }
    private boolean hasBasicCondition(HouseSearchCondition condition){
        return condition.contractType() != null
            || condition.minPrice() != null || condition.maxPrice() != null
            || condition.minArea() != null || condition.maxArea() != null
            || condition.minRoomNumber() != null
            || condition.maxManagementFee() != null
            || condition.minFloor() != null || condition.maxFloor() != null;
    }

    private boolean hasMetadataCondition(HouseSearchCondition condition) {
        return condition.minMart() != null
                || condition.minConvenienceStore() != null
                || condition.minParking() != null
                || condition.minSubway() != null
                || condition.minBank() != null
                || condition.minPO() != null
                || condition.minRestaurant() != null
                || condition.minCafe() != null
                || condition.minHospital() != null
                || condition.minPharmacy() != null;
    }

    private boolean hasSchoolBuildingCondition(HouseSearchCondition condition) {
        return condition.schoolBuildingId() != null && condition.maxDistanceFromSchool() != null;
    }

    //사각형 범위
    private boolean hasBoundingBox(HouseSearchCondition condition) {
        return condition.swLat() != null && condition.swLng() != null
                && condition.neLat() != null && condition.neLng() != null;
    }

    //원형 범위
    private boolean hasCircle(HouseSearchCondition condition) {
        return condition.centerLat() != null && condition.centerLng() != null
                && condition.radiusMeters() != null;
    }

    // 계약 유형
    private BooleanExpression contractTypeEq(String contractType) {
        return contractType != null
                ? house.contractType.eq(House.ContractType.valueOf(contractType))
                : null;
    }

    // 가격 범위
    private BooleanExpression priceBetween(Long minPrice, Long maxPrice) {
        if (minPrice != null && maxPrice != null) return house.price.between(minPrice, maxPrice);
        if (minPrice != null) return house.price.goe(minPrice);
        if (maxPrice != null) return house.price.loe(maxPrice);
        return null;
    }

    // 면적 범위
    private BooleanExpression areaBetween(Double minArea, Double maxArea) {
        if (minArea != null && maxArea != null) return house.area.between(minArea, maxArea);
        if (minArea != null) return house.area.goe(minArea);
        if (maxArea != null) return house.area.loe(maxArea);
        return null;
    }

    // 방 수
    private BooleanExpression minRoomNumberGoe(Integer minRoomNumber) {
        return minRoomNumber != null ? house.roomNumber.goe(minRoomNumber) : null;
    }

    // 관리비
    private BooleanExpression maxManagementFeeLoe(Long maxManagementFee) {
        return maxManagementFee != null ? house.managementFee.loe(maxManagementFee) : null;
    }

    // 층수 범위
    private BooleanExpression floorBetween(Integer minFloor, Integer maxFloor) {
        if (minFloor != null && maxFloor != null) return house.floor.between(minFloor, maxFloor);
        if (minFloor != null) return house.floor.goe(minFloor);
        if (maxFloor != null) return house.floor.loe(maxFloor);
        return null;
    }
}