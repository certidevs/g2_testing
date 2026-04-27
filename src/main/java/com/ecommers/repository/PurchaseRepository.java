package com.ecommers.repository;

import com.ecommers.models.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    List<Purchase> findByTotalPriceLessThanEqualOrderByTotalPrice(Double totalPriceIsLessThan);
    List<Purchase> findByProductId(UUID productId);
    List<Purchase> findByFinishedDateBetween(LocalDateTime finishedDateAfter, LocalDateTime finishedDateBefore);

}
