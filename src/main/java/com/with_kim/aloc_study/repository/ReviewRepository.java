package com.with_kim.aloc_study.repository;

import com.with_kim.aloc_study.entity.Review;
import com.with_kim.aloc_study.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"user", "house"})
    Page<Review> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "house"})
    List<Review> findByHouse_Id(long houseId);

    boolean existsByUser_IdAndHouse_Id(long userId, long houseId);

    Optional<Review> findByIdAndUser_Id(long reviewId, long userId);

}
