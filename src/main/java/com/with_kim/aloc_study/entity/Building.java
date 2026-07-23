package com.with_kim.aloc_study.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buildings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String address;

    @Column(name = "m_address")
    private String mAddress;

    private Double latitude;  // 위도
    private Double longitude; // 경도

    private Integer receiptYear;
    private String sggCd;      // 자치구 코드
    private String sggName;    // 자치구 이름
    private String emdCd;      // 법정동 코드
    private String emdName;    // 법정동 이름
    private Integer mainLotNumber; // 본번
    private Integer subNumber;     // 부번
    private LocalDate contractDate; // 계약일

    private Long price;
    private Double buildingArea;
    private Double landArea;
    private Integer floor;
    private Integer constructionYear;

    private String buildingUsage;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    @OneToMany(mappedBy = "building")
    private List<House> houses = new ArrayList<>();

    public static Building of(String address,
                              String mAddress,
                              Double latitude,
                              Double longitude,
                              Integer receiptYear,
                              String sggCd,
                              String sggName,
                              String emdCd,
                              String emdName,
                              Integer mainLotNumber,
                              Integer subNumber,
                              LocalDate contractDate,
                              Long price,
                              Double buildingArea,
                              Double landArea,
                              Integer floor,
                              Integer constructionYear,
                              String buildingUsage) {

        Building building = new Building();
        building.address = address;
        building.mAddress = mAddress;
        building.latitude = latitude;
        building.longitude = longitude;
        building.receiptYear = receiptYear;
        building.sggCd = sggCd;
        building.sggName = sggName;
        building.emdCd = emdCd;
        building.emdName = emdName;
        building.mainLotNumber = mainLotNumber;
        building.subNumber = subNumber;
        building.contractDate = contractDate;
        building.price = price;
        building.buildingArea = buildingArea;
        building.landArea = landArea;
        building.floor = floor;
        building.constructionYear = constructionYear;
        building.buildingUsage = buildingUsage;

        return building;
    }

    @PrePersist
    @PreUpdate
    private void syncLocation() {
        if (latitude != null && longitude != null) {
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
            this.location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        }
    } //위도, 경도를 통해 자동으로 location 필드 채움
}