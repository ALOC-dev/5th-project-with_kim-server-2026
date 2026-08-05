package com.with_kim.aloc_study.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Entity
@Table(name = "houses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class House{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private Building building;

    @OneToMany(mappedBy = "house")
    private List<Submission> submissions = new ArrayList<>();

    @OneToMany(mappedBy = "house")
    private List<VerifiedAddress> verifiedAddresses = new ArrayList<>();

    private Long price; //매매가
    private Long deposit; //보증금
    private Long monthlyRent; //월세
    private Long managementFee; //관리비

    private Double area;
    private Integer roomNumber; //방 수
    private Integer toilet; //욕실 수


    @Enumerated(EnumType.STRING)
    private ContractType contractType;   // 매매/전세/월세

    private Integer bldg; //동
    private Integer unit; //호수
    private Long number; //매물 번호
    private String description; //매물 설명

    @Enumerated(EnumType.STRING)
    private Direction direction;
    private Integer floor;

    @Column(name = "source_key", unique = true)
    private String sourceKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String metadata;

    @Column(name = "metadata_updated_at")
    private LocalDateTime metadataUpdatedAt;

    private String image1Url;
    private String image2Url;
    private String image3Url;

    // 가장 최근에 완료된 등기부 분석의 조회용 요약값이다.
    // 보증금·임대 유형에 따라 달라질 수 있으므로 원본 이력은 Submission/AnalysisResult에 보존한다.
    @Column(name = "analysis_status", length = 30)
    private String analysisStatus;

    @Column(name = "analysis_lease_type", length = 10)
    private String analysisLeaseType;

    @Column(name = "risk_level", length = 20)
    private String riskLevel;

    @Column(name = "risk_score")
    private Double riskScore;

    @Column(name = "mortgage_total")
    private Long mortgageTotal;

    @Column(name = "jeonse_rate")
    private Double jeonseRate;

    @Column(name = "lh_eligible")
    private Boolean lhEligible;

    @Column(name = "hug_eligible")
    private Boolean hugEligible;

    @Column(name = "estimated_recoverable_deposit")
    private Long estimatedRecoverableDeposit;

    @Column(name = "deposit_recovery_rate")
    private Double depositRecoveryRate;

    @Column(name = "analysis_updated_at")
    private LocalDateTime analysisUpdatedAt;

    @Column(nullable = false) //조회수
    @ColumnDefault("0")
    private Long viewCount = 0L;

    public List<String> getImageUrls() {
        return Stream.of(image1Url, image2Url, image3Url)
                .filter(Objects::nonNull)
                .toList();
    }

    public boolean needsMetadataUpdate(){
        if(metadata == null || metadataUpdatedAt == null){ //NPE 가능성 수정(HouseCreateRequest에서 metadata 필드 빼도 됨)
            return true;
        }

        return metadataUpdatedAt.isBefore(LocalDateTime.now().minusDays(30));
    }

    public enum ContractType {
        SALE,
        JEONSE,
        MONTHLY
    }

    public enum Direction{
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

    public Double getLatitude() {
        return building.getLatitude();
    }

    public Double getLongitude() {
        return building.getLongitude();
    }

    public void updateDescription(String description){
        this.description=description;
    }

    public void updateMetadata(String metadataJson) {
        this.metadata = metadataJson;
        this.metadataUpdatedAt = LocalDateTime.now();
    }

    public static House fromSeoulRtms(
            Building building,
            Long price,
            Double area,
            Integer floor,
            String sourceKey,
            String description,
            String metadata
    ) {
        House house = new House();
        house.building = building;
        house.price = price;
        house.area = area;
        house.roomNumber = 1;
        house.toilet = 1;
        house.managementFee = 0L;
        house.floor = floor;
        house.bldg = 0;
        house.unit = 0;
        house.number = sourceKey == null ? null : Integer.toUnsignedLong(sourceKey.hashCode());
        house.contractType = ContractType.SALE;
        house.direction = Direction.SOUTH;
        house.sourceKey = sourceKey;
        house.description = description;
        house.metadata = metadata;
        house.metadataUpdatedAt = LocalDateTime.now();

        return house;
    }

    public void updateAnalysisSummary(
            String analysisStatus,
            String analysisLeaseType,
            String riskLevel,
            Double riskScore,
            Long mortgageTotal,
            Double jeonseRate,
            Boolean lhEligible,
            Boolean hugEligible,
            Long estimatedRecoverableDeposit,
            Double depositRecoveryRate,
            LocalDateTime analysisUpdatedAt
    ) {
        this.analysisStatus = analysisStatus;
        this.analysisLeaseType = analysisLeaseType;
        this.riskLevel = riskLevel;
        this.riskScore = riskScore;
        this.mortgageTotal = mortgageTotal;
        this.jeonseRate = jeonseRate;
        this.lhEligible = lhEligible;
        this.hugEligible = hugEligible;
        this.estimatedRecoverableDeposit = estimatedRecoverableDeposit;
        this.depositRecoveryRate = depositRecoveryRate;
        this.analysisUpdatedAt = analysisUpdatedAt == null ? LocalDateTime.now() : analysisUpdatedAt;
    }

    void addVerifiedAddress(VerifiedAddress verifiedAddress) {
        if (!verifiedAddresses.contains(verifiedAddress)) {
            verifiedAddresses.add(verifiedAddress);
        }
    }

    void removeVerifiedAddress(VerifiedAddress verifiedAddress) {
        verifiedAddresses.remove(verifiedAddress);
    }

    public static House create(
            Building building,
            Long price,
            Long deposit,
            Long monthlyRent,
            Double area,
            Integer roomNumber,
            Integer toilet,
            Long managementFee,
            ContractType contractType,
            Integer floor,
            Direction direction,
            String description,
            String metadata,
            List<String> imageUrls
    ) {
        House house = new House();
        house.building = building;
        house.price = price;
        house.deposit = deposit;
        house.monthlyRent = monthlyRent;
        house.area = area;
        house.roomNumber = roomNumber;
        house.toilet = toilet;
        house.managementFee = managementFee;
        house.contractType = contractType;
        house.floor = floor;
        house.direction = direction;
        house.description = description;
        house.metadata = metadata;
        if (imageUrls != null) {
            if (!imageUrls.isEmpty()) {
                house.image1Url = imageUrls.get(0);
            }
            if (imageUrls.size() > 1) {
                house.image2Url = imageUrls.get(1);
            }
            if (imageUrls.size() > 2) {
                house.image3Url = imageUrls.get(2);
            }
        }
        return house;
    }
}
