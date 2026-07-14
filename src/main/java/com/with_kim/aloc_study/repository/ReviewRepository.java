package com.with_kim.aloc_study.repository;

import com.with_kim.aloc_study.entity.Review;
import com.with_kim.aloc_study.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByHouse_Id(long houseId);

    List<Review> findByUser_Id(long userId);

    boolean existsByUser_IdAndHouse_Id(long userId, long houseId);

    Optional<Review> findByIdAndUser_Id(long reviewId, long userId);

    List<Review> findByHouse_IdOrderByCreatedAtDesc(Long houseId);

}
