package com.with_kim.aloc_study.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class Users {

    public enum HousingType {
        MONTHLY_RENT, // 월세
        JEONSE,       // 전세
        ANY           // 상관없음
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_building_id")
    private SchoolBuilding mainBuilding;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_building_id")
    private SchoolBuilding subBuilding;

    private Integer maxDeposit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HousingType housingType;

    public static Users create(String loginId, String password, String username) {
        Users user = new Users();
        user.loginId = loginId;
        user.password = password;
        user.username = username;
        user.housingType = HousingType.ANY;
        return user;
    }

    public static Users create(String loginId,
                               String password,
                               String username,
                               SchoolBuilding mainBuilding,
                               SchoolBuilding subBuilding,
                               Integer maxDeposit,
                               HousingType housingType) {
        Users user = new Users();
        user.loginId = loginId;
        user.password = password;
        user.username = username;
        user.mainBuilding = mainBuilding;
        user.subBuilding = subBuilding;
        user.maxDeposit = maxDeposit;
        user.housingType = housingType;
        return user;
    }
}
