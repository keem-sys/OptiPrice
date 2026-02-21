package com.optiprice.listener;

import com.optiprice.event.ProductScrapedEvent;
import com.optiprice.repository.StoreItemRepository;
import com.optiprice.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AiProcessingListener {

    private final StoreItemRepository storeItemRepository;
    private final MatchingService matchingService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductScraped(ProductScrapedEvent event) {
        storeItemRepository.findById(event.storeItemId()).ifPresent(item -> {

            if (item.getMasterProduct() == null) {
                System.out.println("Async AI: Processing item " + item.getId() + "...");
                matchingService.findOrCreateMasterProduct(item);
            }

        });
    }
}