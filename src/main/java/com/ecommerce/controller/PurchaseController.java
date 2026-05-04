package com.ecommerce.controller;

import com.ecommerce.repository.PurchaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class PurchaseController {

    private final PurchaseRepository purchaseRepository;

    @GetMapping("purchases")
    public String listPurchases(Model model) {
        model.addAttribute("purchases", purchaseRepository.findAll());
        return "purchases/purchases-list";
    }

    @GetMapping("purchases/{id}")
    public String detailPurchase(Model model, @PathVariable UUID id) {
        model.addAttribute("purchase", purchaseRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        return "purchases/purchase-detail";
    }
}