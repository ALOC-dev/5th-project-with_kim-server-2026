package com.with_kim.aloc_study.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

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


}