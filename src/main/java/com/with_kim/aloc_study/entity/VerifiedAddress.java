package com.with_kim.aloc_study.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "verified_addresses",
        indexes = {
                @Index(name = "idx_verified_addresses_user_id", columnList = "user_id"),
                @Index(name = "idx_verified_addresses_house_id", columnList = "house_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerifiedAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id")
    private House house;

    @Column(name = "raw_address", nullable = false, length = 500)
    private String rawAddress;

    @Column(name = "road_address", length = 500)
    private String roadAddress;

    @Column(name = "jibun_address", length = 500)
    private String jibunAddress;

    @Column(name = "is_current", nullable = false)
    private boolean current;

    @Column(name = "address_order", nullable = false)
    private int addressOrder;

    @Column(name = "residence_years", nullable = false, length = 500)
    private String residenceYears;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_status", nullable = false, length = 20)
    private MatchStatus matchStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static VerifiedAddress create(
            Users user,
            String rawAddress,
            String roadAddress,
            String jibunAddress,
            boolean current,
            int addressOrder,
            String residenceYears
    ) {
        VerifiedAddress verifiedAddress = new VerifiedAddress();
        verifiedAddress.user = user;
        verifiedAddress.rawAddress = rawAddress;
        verifiedAddress.roadAddress = roadAddress;
        verifiedAddress.jibunAddress = jibunAddress;
        verifiedAddress.current = current;
        verifiedAddress.addressOrder = addressOrder;
        verifiedAddress.residenceYears = residenceYears;
        verifiedAddress.matchStatus = MatchStatus.PENDING;
        verifiedAddress.createdAt = LocalDateTime.now();
        user.addVerifiedAddress(verifiedAddress);
        return verifiedAddress;
    }

    public void match(House house) {
        if (house == null) {
            throw new IllegalArgumentException("매칭할 집이 필요합니다.");
        }

        detachFromHouse();
        this.house = house;
        this.matchStatus = MatchStatus.MATCHED;
        house.addVerifiedAddress(this);
    }

    public void markNotFound() {
        detachFromHouse();
        this.matchStatus = MatchStatus.NOT_FOUND;
    }

    public void markAmbiguous() {
        detachFromHouse();
        this.matchStatus = MatchStatus.AMBIGUOUS;
    }

    private void detachFromHouse() {
        if (this.house != null) {
            this.house.removeVerifiedAddress(this);
            this.house = null;
        }
    }

    public enum MatchStatus {
        PENDING,
        MATCHED,
        NOT_FOUND,
        AMBIGUOUS
    }
}
