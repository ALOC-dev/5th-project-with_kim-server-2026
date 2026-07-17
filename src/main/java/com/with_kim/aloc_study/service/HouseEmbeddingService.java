package com.with_kim.aloc_study.service;

import com.with_kim.aloc_study.entity.House;
import com.with_kim.aloc_study.repository.HouseEmbeddingRepository;
import com.with_kim.aloc_study.repository.HouseRepository;
import com.with_kim.aloc_study.repository.NearbyInfoRepository;
import com.with_kim.aloc_study.util.HouseFeatureTextBuilder;
import com.with_kim.aloc_study.util.OpenAiEmbeddingClient;
import com.with_kim.aloc_study.util.VectorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseEmbeddingService {
    //호출 횟수 줄이기 용도
    private static final int BATCH_SIZE=100;

    private final HouseRepository houseRepository;
    private final NearbyInfoRepository nearbyInfoRepository;
    private final HouseFeatureTextBuilder houseFeatureTextBuilder;
    private final OpenAiEmbeddingClient embeddingClient;
    private final HouseEmbeddingRepository embeddingRepository;

    //전체 매물의 feature_embedding을 생성
    @Transactional
    public int rebuildAll(){
        List<House> houses=houseRepository.findAllWithBuilding();
        int updated=0;

        for(int i=0;i<houses.size();i+=BATCH_SIZE){
            List<House> part=houses.subList(i,Math.min(i+BATCH_SIZE,houses.size()));

            //특징 텍스트 생성
            List<String> texts=part.stream()
                    .map(h->houseFeatureTextBuilder.build(h,nearbyInfoRepository.findFor(h.getBuilding())))
                    .toList();

            //배치 임베딩
            List<List<Double>> vectors=embeddingClient.embedAll(texts);

            //저장
            for (int j = 0; j < part.size(); j++) {
                embeddingRepository.updateEmbedding(
                        part.get(j).getId(),
                        VectorUtils.toVectorLiteral(vectors.get(j))
                );
                updated++;
            }
        }
        return updated;
    }
}
