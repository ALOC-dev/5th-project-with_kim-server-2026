package com.with_kim.aloc_study.repository;

import com.with_kim.aloc_study.entity.House;
import com.with_kim.aloc_study.entity.Users;
import com.with_kim.aloc_study.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {

    List<WishList> findByUser_Id(Long userId);

    boolean existsByUser_IdAndHouse_Id(Long userId, Long houseId);

    void deleteByUser_IdAndHouse_Id(Long userId, Long houseId);
}