package com.ecommerce.controller;

import com.ecommerce.repository.PurchaseLineRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PurchaseLineController {

    private final PurchaseLineRepository purchaseLineRepository;

    public PurchaseLineController(PurchaseLineRepository purchaseLineRepository) {
        this.purchaseLineRepository = purchaseLineRepository;
    }

    @GetMapping("purchase_line")
    public String purchases(Model model) {
        model.addAttribute("purchase_line", purchaseLineRepository.findAll());
        return "purchase_line/purchase_line-detail";
    }

}
