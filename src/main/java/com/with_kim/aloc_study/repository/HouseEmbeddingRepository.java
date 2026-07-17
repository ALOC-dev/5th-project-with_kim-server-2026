package com.with_kim.aloc_study.repository;

import com.with_kim.aloc_study.entity.House;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HouseEmbeddingRepository {
    private final EntityManager em;

    //매물 1개 임베딩 저장
    public void updateEmbedding(Long houseId,String vectorLiteral){
        em.createNativeQuery("""
                UPDATE houses
                SET feature_embedding = CAST(:v AS vector)
                WHERE id=:id
                """)
                .setParameter("v",vectorLiteral)
                .setParameter("id",houseId)
                .executeUpdate();
    }

    //쿼리 백터와 코사인 거리가 가까운 매물 topk개
    @SuppressWarnings("unchecked")
    public List<House> searchByVector(String vectorLiteral, int topK){
        return em.createNativeQuery("""
                SELECT h.*
                FROM houses h
                WHERE h.feature_embedding IS NOT NULL
                ORDER BY h.feature_embedding <=> CAST(:v AS vector)
                LIMIT :k
                """, House.class)
                .setParameter("v",vectorLiteral)
                .setParameter("k", topK)
                .getResultList();
    }
}
