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
@Table(name = "school_buildings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SchoolBuilding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String buildingName;
    private Integer buildingNumber;
    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    public static SchoolBuilding of(Long id,
                                    String buildingName,
                                    Integer buildingNumber,
                                    Double latitude,
                                    Double longitude){
        SchoolBuilding schoolBuilding = new SchoolBuilding();
        schoolBuilding.id = id;
        schoolBuilding.buildingName = buildingName;
        schoolBuilding.buildingNumber = buildingNumber;
        schoolBuilding.latitude = latitude;
        schoolBuilding.longitude = longitude;

        return schoolBuilding;
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