package com.ecommerce.controller.api;

import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.PurchaseRepository;
import com.ecommerce.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@AllArgsConstructor
public class ProductRestController {
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final PurchaseRepository purchaseRepository;

    @GetMapping("products")
    public List<Product> findAll(){
        return productRepository.findAll();
    }
}
