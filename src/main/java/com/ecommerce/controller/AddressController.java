package com.ecommerce.controller;

import com.ecommerce.dto.AddressRequestDto;
import com.ecommerce.dto.AddressResponseDto;
import com.ecommerce.model.Address;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.AddressType;
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

        List<AddressResponseDto> userAddresses;
        if (user.getRole() == Role.ROLE_ADMIN) {
            userAddresses = addressService.findAll();
        } else {
            userAddresses = addressService.findByUser(user);
        }

        model.addAttribute("addresses", userAddresses);
        model.addAttribute("addressTypes", AddressType.values());

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
    public String showCreateAddressForm(Model model, @AuthenticationPrincipal User user)
    {
        if (user == null) {
            return "redirect:/login";
        }

        AddressRequestDto addressDto = new AddressRequestDto();
        addressDto.setUsersId(user.getId());

        model.addAttribute("address", addressDto);
        model.addAttribute("addressTypes", AddressType.values());

        return "addresses/address-form";
    }

    // Procesa la creación de una nueva dirección
    @PostMapping("/addresses")
    public String saveAddress(@Valid @ModelAttribute("address") AddressRequestDto addressDto, BindingResult result, @AuthenticationPrincipal User user, Model model)
    {
        if (user == null) {
            return "redirect:/login";
        }

        addressDto.setUsersId(user.getId());

        if (result.hasErrors()) {
            model.addAttribute("addressTypes", AddressType.values());
            return "addresses/address-form";
        }

        try {
            addressService.addAddress(addressDto, user);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "No se pudo registrar la dirección. Inténtelo de nuevo.");
            model.addAttribute("addressTypes", AddressType.values());
            return "addresses/address-form";
        }

        return "redirect:/addresses";
    }

    // Edita una dirección
    @GetMapping("addresses/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model, @AuthenticationPrincipal User user)
    {
        if (user == null) {
            return "redirect:/login";
        }

        AddressResponseDto address = addressService.findById(id);

        AddressRequestDto addressDto = AddressRequestDto.builder()
                .street(address.getStreet())
                .number(address.getNumber())
                .complement(address.getComplement())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .zipCode(address.getZipCode())
                .addressType(address.getAddressType())
                .usersId(user.getId())
                .build();

        model.addAttribute("addressId", id);
        model.addAttribute("address", addressDto);
        model.addAttribute("addressTypes", AddressType.values());

        return "addresses/edit";
    }

    // Procesa la actualización de la dirección
    @PostMapping("/addresses/{id}")
    public String update(
            @PathVariable UUID id,
            @Valid @ModelAttribute("address") AddressRequestDto addressDto,
            BindingResult result, Model model, RedirectAttributes redirectAttributes, @AuthenticationPrincipal User user)
    {
        if (user == null) {
            return "redirect:/login";
        }

        addressDto.setUsersId(user.getId());

        if (result.hasErrors()) {
            model.addAttribute("addressTypes", AddressType.values());
            model.addAttribute("addressId", id);
            return "addresses/edit";
        }

        try {
            addressService.updateAddress(id, addressDto, user);
            redirectAttributes.addFlashAttribute("message", "Dirección actualizada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la dirección: " + e.getMessage());
        }

        return "redirect:/addresses";
    }

    // Elimina una dirección
    @PostMapping("/addresses/{id}/delete")
    public String deleteAddress(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal User user
    ) {
        if (user == null) {
            return "redirect:/login";
        }

        try {
            addressService.delete(id, user);
            redirectAttributes.addFlashAttribute("message", "La dirección se ha eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la dirección: " + e.getMessage());
        }

        return "redirect:/addresses";
    }
}