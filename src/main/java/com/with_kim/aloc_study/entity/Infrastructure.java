package com.with_kim.aloc_study.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "infrastructures")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Infrastructure{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private InfrastructureCategory category;
    private String name;
    private String address;

    @Column(name = "m_address")
    private String mAddress; //도로명 주소
    private String ctprvnCd; //시도 코드
    private String sggCd;
    private String emdCd;
    private Double latitude;
    private Double longitude;
    private String external_id; //고유 ID


    enum InfrastructureCategory{
        CCTV,
        CAMPUS
    } //카카오맵으로 표시 불가능한 것만 지정

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;
}