package com.ecommers.repository;

import com.ecommers.models.Purchases;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PurchasesRepository extends JpaRepository<Purchases, Long> {

    List<Purchases> findByTotalPriceLessThanEqualOrderByTotalPrice(Double totalPriceIsLessThan);
    List<Purchases> findByProduct_Id(Long productId);
    List<Purchases> findByFinishedDateBetween(LocalDateTime finishedDateAfter, LocalDateTime finishedDateBefore);
}
