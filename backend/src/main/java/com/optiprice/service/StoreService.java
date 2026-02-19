package com.optiprice.service;

import com.optiprice.model.Store;
import com.optiprice.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    @Cacheable(value = "stores", key = "#name")
    public Store getOrCreateStore(String name, String logoUrl, String websiteUrl) {
        System.out.println("Database Hit: Fetching/Creating Store -> " + name);

        return storeRepository.findByName(name)
                .orElseGet(() -> {
                    Store newStore = new Store();
                    newStore.setName(name);
                    newStore.setLogoUrl(logoUrl);
                    newStore.setWebsiteUrl(websiteUrl);
                    return storeRepository.save(newStore);
                });
    }
}