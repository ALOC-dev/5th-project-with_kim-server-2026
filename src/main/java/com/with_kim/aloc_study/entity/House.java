package com.with_kim.aloc_study.entity;

import com.with_kim.aloc_study.entity.Building;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "houses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class House{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private Building building;

    private Long price;
    private Double area;
    private Integer roomNumber; //방 수
    private Integer toilet; //욕실 수
    private Long managementFee; //관리비

    @Enumerated(EnumType.STRING)
    private ContractType contractType;   // 매매/전세/월세

    private Integer bldg; //동
    private Integer unit; //호수
    private Long number; //매물 번호
    private String description; //매물 설명

    @Enumerated(EnumType.STRING)
    private Direction direction;
    private Integer floor;


    public enum ContractType {
        SALE,
        JEONSE,
        MONTHLY
    }

    public enum Direction{
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

    public Double getLatitude() {
        return building.getLatitude();
    }

    public Double getLongitude() {
        return building.getLongitude();
    }
}