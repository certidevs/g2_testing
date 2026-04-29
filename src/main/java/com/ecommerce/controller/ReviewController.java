package com.ecommerce.controller;

import com.ecommerce.model.Reviews;
import com.ecommerce.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;

    // @GetMapping("reviews")
    @GetMapping("reviews")
    public List<Reviews> findAll() {
        return reviewRepository.findAll();
    }

    // @GetMapping("reviews/{id}")
    @GetMapping("reviews/{id}")
    public Reviews findById(@PathVariable UUID id) {
        return reviewRepository.findById(id).orElse(null);
    }
    // @GetMapping("reviews/product/{id}")
    @GetMapping("reviews/product/{id}")
    public List<Reviews> findByProductId(@PathVariable UUID id) {
        return reviewRepository.findByProductId(id);
    }

    // @GetMapping("reviews/rating/{rating}")
    @GetMapping("reviews/rating/{rating}")
    public List<Reviews> findByRating(@PathVariable Integer rating) {
        return reviewRepository.findByRating(rating);
    }
}
