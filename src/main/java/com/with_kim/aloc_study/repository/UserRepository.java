package com.with_kim.aloc_study.repository;

import com.with_kim.aloc_study.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {



}
