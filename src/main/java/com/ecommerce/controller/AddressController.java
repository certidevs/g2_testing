package com.ecommerce.controller;

import com.ecommerce.dto.AddressResponseDto;
import com.ecommerce.service.AddressService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/addresses")
    public String listAddresses(Model model) {
        List<AddressResponseDto> addresses = addressService.findAll();
        model.addAttribute("addresses", addresses);
        return "addresses/addresses-list";
    }

    @GetMapping("/addresses/{id}")
    public String detailAddress(@PathVariable UUID id, Model model) {
        AddressResponseDto address = addressService.findById(id);
        model.addAttribute("address", address);
        return "addresses/address-detail";
    }
}

