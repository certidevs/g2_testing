package com.ecommerce.controller;

import com.ecommerce.repository.PurchaseRepository;
import com.ecommerce.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@AllArgsConstructor
public class PurchaseController {

    private final PurchaseRepository purchaseRepository;

    @GetMapping("purchases")
    public String purchases(Model model) {
        model.addAttribute("purchases", purchaseRepository.findAll());
        return "purchases/purchase-detail";
    }

    @GetMapping("orders/{id}")
    public String purchase(Model model, @PathVariable Long id) {
        model.addAttribute("order", purchaseRepository.findById(id).orElseThrow());
        return "purchases/purchase-detail";
    }
}