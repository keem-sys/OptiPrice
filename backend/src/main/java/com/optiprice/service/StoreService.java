package com.optiprice.service;

import com.optiprice.model.Store;
import com.optiprice.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public Store getOrCreateStore(String name, String logoUrl, String websiteUrl) {
        return storeRepository.findByName(name)
                .orElseGet(() -> storeRepository.save(new Store(name, logoUrl, websiteUrl)));
    }
}