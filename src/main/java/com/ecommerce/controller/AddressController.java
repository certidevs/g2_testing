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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AddressController {

    private final AddressRepository addressRepository;
    private final AddressService addressService;

    // Accede a una lista de direcciones de envío
    @GetMapping("/addresses")
    public String listAddresses(Model model, @AuthenticationPrincipal User user) {
        if (user == null) {
            return "redirect:/login";
        }

        List<AddressResponseDto> userAddresses = addressService.findByUser(user);
        model.addAttribute("addresses", userAddresses);

        return "addresses/address-list";
    }

    // Acceder a detalles de una dirección
    @GetMapping("/addresses/{id}")
    public String addressDetail(@PathVariable UUID id, Model model) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("address", address);
        return "addresses/address-detail";
    }

    // Crea una nueva dirección
    @GetMapping("/addresses/new")
    public String showCreateAddressForm(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("address", new AddressRequestDto());
        return "addresses/address-form";
    }

    // Procesa la creación de una nueva dirección
    @PostMapping("/addresses")
    public String saveAddress(@Valid @ModelAttribute("address") AddressRequestDto addressDto, BindingResult result, @AuthenticationPrincipal User user, Model model) {

        if (result.hasErrors()) {
            return "addresses/address-form";
        }
        try {
            addressService.addAddress(addressDto, user);
        } catch (Exception e) {

            model.addAttribute("errorMessage", "No se pudo registrar la dirección. Inténtelo de nuevo.");
            return "addresses/address-form";
        }

        return "redirect:/addresses";
    }

    // Edita una dirección
    @GetMapping("addresses/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        AddressResponseDto address = addressService.findById(id);
        model.addAttribute("address", address);
        return "addresses/edit";
    }

    // Procesa la actualización de la dirección
    @PostMapping("/addresses/{id}")
    public String update(@PathVariable UUID id, @ModelAttribute AddressRequestDto addressDto, RedirectAttributes redirectAttributes) {
        try {
            addressService.updateAddress(id, addressDto);
            redirectAttributes.addFlashAttribute("message", "Dirección actualizada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la dirección: " + e.getMessage());
        }
        return "redirect:/addresses";
    }

    // Elimina una dirección
    @GetMapping("addresses/delete/{id}")
    public String deleteAddress(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        addressService.delete(id);
        redirectAttributes.addFlashAttribute("message", "La dirección se ha eliminado correctamente");
        return "redirect:/addresses";
    }
}