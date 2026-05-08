package com.ecommerce.controller;

import com.ecommerce.model.Address;
import com.ecommerce.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AddressController {

    private final AddressRepository addressRepository;

    @GetMapping("/addresses")
    public String listAddresses(Model model) {
        List<Address> addresses = addressRepository.findAll();
        model.addAttribute("addresses", addresses);
        return "addresses/addresses-list";
    }

    @GetMapping("/addresses/{id}")
    public String addressDetail(@PathVariable UUID id, Model model) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("address", address);
        return "addresses/address-detail";
    }
}
