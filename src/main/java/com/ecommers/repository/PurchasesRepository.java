package com.ecommers.repository;

import com.ecommers.models.Product;
import com.ecommers.models.Purchases;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PurchasesRepository extends JpaRepository<Purchases, Long> {

    List<Purchases> findByTotalPriceLessThanEqualOrderByTotalPrice(Double totalPriceIsLessThan);
    List<Purchases> findByProductId(UUID productId);
    List<Purchases> findByFinishedDateBetween(LocalDateTime finishedDateAfter, LocalDateTime finishedDateBefore);

}
