package com.with_kim.aloc_study.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Users {

    public enum Role {
        USER,
        AGENT,
        ADMIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String loginId;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String username;

    private String department;

    private Long preferredSchoolBuildingId;

    private Long preferredDeposit;

    private Long budget;

    private Boolean prefersMonthlyRent;

    private Boolean prefersJeonse;

    private Boolean notificationEnabled;

    @OneToMany(mappedBy = "user")
    private List<Submission> submissions = new ArrayList<>();

}
