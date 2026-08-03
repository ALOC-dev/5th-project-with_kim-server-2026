package com.with_kim.aloc_study.util;

import com.with_kim.aloc_study.repository.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class HouseViewCountUpdater {

    private final HouseRepository houseRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increase(Long houseId) {
        houseRepository.increaseViewCount(houseId);
    }
}