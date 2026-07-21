package com.with_kim.aloc_study.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

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


    public enum InfrastructureCategory{
        CCTV,
        CAMPUS
    } //카카오맵으로 표시 불가능한 것만 지정

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    public static Infrastructure of(Long id,
                                    InfrastructureCategory category,
                                    String name,
                                    String address,
                                    String mAddress,
                                    String ctprvnCd,
                                    String sggCd,
                                    String emdCd,
                                    Double latitude,
                                    Double longitude,
                                    String external_id){
        Infrastructure infrastructure = new Infrastructure();
        infrastructure.id = id;
        infrastructure.category = category;
        infrastructure.name = name;
        infrastructure.address = address;
        infrastructure.mAddress = mAddress;
        infrastructure.ctprvnCd = ctprvnCd;
        infrastructure.sggCd = sggCd;
        infrastructure.emdCd = emdCd;
        infrastructure.latitude = latitude;
        infrastructure.longitude = longitude;
        infrastructure.external_id = external_id;

        return infrastructure;
    }

    @PrePersist
    @PreUpdate
    private void syncLocation() {
        if (latitude != null && longitude != null) {
            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
            this.location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        }
    }
}