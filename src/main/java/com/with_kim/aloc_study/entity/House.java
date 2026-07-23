package com.with_kim.aloc_study.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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

    private Long price; //매매가
    private Long deposit; //보증금
    private Long monthlyRent; //월세
    private Long managementFee; //관리비

    private Double area;
    private Integer roomNumber; //방 수
    private Integer toilet; //욕실 수


    @Enumerated(EnumType.STRING)
    private ContractType contractType;   // 매매/전세/월세

    private Integer bldg; //동
    private Integer unit; //호수
    private Long number; //매물 번호
    private String description; //매물 설명

    @Enumerated(EnumType.STRING)
    private Direction direction;
    private Integer floor;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "metadata_updated_at")
    private LocalDateTime metadataUpdatedAt;

    private String image1Url;
    private String image2Url;
    private String image3Url;

    public List<String> getImageUrls() {
        return Stream.of(image1Url, image2Url, image3Url)
                .filter(Objects::nonNull)
                .toList();
    }

    public boolean needsMetadataUpdate(){
        if(metadata == null){
            return true;
        }

        return metadataUpdatedAt.isBefore(LocalDateTime.now().minusDays(30));
    }

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

    public void updateMetadata(String metadataJson) {
        this.metadata = metadataJson;
        this.metadataUpdatedAt = LocalDateTime.now();
    }
}