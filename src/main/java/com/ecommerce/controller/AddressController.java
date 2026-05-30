package com.ecommerce.controller;

import com.ecommerce.dto.AddressRequestDto;
import com.ecommerce.dto.AddressResponseDto;
import com.ecommerce.model.Address;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.Role;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AddressController {

    private final AddressRepository addressRepository;
    private final AddressService addressService;

    @GetMapping("/addresses")
    public String listAddresses(Model model, @AuthenticationPrincipal User user) {
        if (user.getRole().equals(Role.ROLE_ADMIN)) {
            model.addAttribute("addresses", addressRepository.findAll());
        } else {
            model.addAttribute("addresses", addressRepository.findByUser(user));
        }
        return "addresses/address-list";
    }

    @GetMapping("/addresses/{id}")
    public String addressDetail(@PathVariable UUID id, Model model) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("address", address);
        return "addresses/address-detail";
    }

    // Muestra el formulario para agregar una nueva dirección de envío
    @GetMapping("addresses/new")
    public String showCreateAddressForm(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("address", new Address());
        model.addAttribute("addresses", addressRepository.findByUser(user));
        return "addresses/address-form";
    }

    // Procesa el formulario para agregar una nueva dirección
    @PostMapping("/addresses")
    public String addAddress(@Valid @ModelAttribute AddressRequestDto form, @AuthenticationPrincipal User user) {
        addressService.addAddress(form, user);
        return "redirect:/addresses";
    }

    // Edita una dirección de envío existente
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {

        AddressResponseDto address = addressService.findById(id);
        model.addAttribute("address", address);

        return "addresses/edit";
    }

    // Procesa el formulario para editar una dirección
    @PostMapping("/{id}")
    public String update(@PathVariable UUID addressId, @ModelAttribute AddressRequestDto address) {
        addressService.updateAddress(addressId, address);

        return "redirect:/addresses" + addressId;
    }

    @GetMapping("addresses/delete/{id}")
    public String deleteAddress(@PathVariable UUID addressId, RedirectAttributes redirectAttributes){
        addressService.delete(addressId);
        redirectAttributes.addFlashAttribute("message", "La dirección se ha eliminado correctamente");
        return "redirect:/addresses/" + addressId;
    }
}