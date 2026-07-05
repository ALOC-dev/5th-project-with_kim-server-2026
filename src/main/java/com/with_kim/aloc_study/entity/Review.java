package com.with_kim.aloc_study.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private int rating;

    private String text;

    private String image_url1;

    private String image_url2;

    private String image_url3;

    public Review(Users user, House house, int rating, String text,
                  String imageUrl1, String imageUrl2, String imageUrl3) {
        this.user = user;
        this.house = house;
        this.rating = rating;
        this.text = text;
        this.image_url1 = imageUrl1;
        this.image_url2 = imageUrl2;
        this.image_url3 = imageUrl3;
    }

    public void update(int rating, String text, String image_url1, String image_url2, String image_url3) {
        this.rating = rating;
        this.text = text;
        this.image_url1 = image_url1;
        this.image_url2 = image_url2;
        this.image_url3 = image_url3;
    }
}
