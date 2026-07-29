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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String loginId;

    private String password;

    private String username;

    @OneToMany(mappedBy = "user")
    private List<Submission> submissions = new ArrayList<>();

}
