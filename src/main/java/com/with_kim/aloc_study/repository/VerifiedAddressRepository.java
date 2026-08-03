package com.with_kim.aloc_study.repository;

import com.with_kim.aloc_study.entity.VerifiedAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerifiedAddressRepository extends JpaRepository<VerifiedAddress, Long> {

    List<VerifiedAddress> findAllByUserIdOrderByAddressOrderAsc(Long userId);

    void deleteAllByUserId(Long userId);

    void deleteByHouse_Id(Long houseId);
}
