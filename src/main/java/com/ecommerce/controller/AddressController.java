package com.ecommerce.controller;

import com.ecommerce.model.Address;
import com.ecommerce.repository.AddressRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class AddressController {

    private final AddressRepository addressRepository;

    @GetMapping("/addresses")
    public String listAddresses(Model model) {
        model.addAttribute("addresses", addressRepository.findAll());
        return "addresses/addresses-list";
    }

    @GetMapping("/addresses/{id}")
    public String detailAddress(@PathVariable UUID id, Model model) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("address", address);
        return "addresses/address-detail";
    }
}

