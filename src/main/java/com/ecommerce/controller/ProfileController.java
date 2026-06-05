package com.ecommerce.controller;

import com.ecommerce.dto.AddressRequestDto;
import com.ecommerce.dto.AddressResponseDto;
import com.ecommerce.model.User;
import com.ecommerce.service.AddressService;
import com.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;


@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService usersService;
    private final AddressService addressService;

    @GetMapping({"/profile", "/user/profile", "/users/profile"})
    public String viewProfile(@RequestParam(required = false) String email,
                              @AuthenticationPrincipal User authenticatedUser,
                              Model model) {
        User user = resolveUser(email, authenticatedUser);
        model.addAttribute("user", user);
        return "users/profile";
    }

    @GetMapping({"/profile/edit", "/user/edit", "/users/profile/edit"})
    public String editProfile(@RequestParam(required = false) String email,
                              @AuthenticationPrincipal User authenticatedUser,
                              Model model) {
        User user = resolveUser(email, authenticatedUser);
        model.addAttribute("user", user);
        return "users/profile-form";
    }

    @PostMapping({"/profile/update", "/user/update", "/users/profile/update"})
    public String updateProfile(@RequestParam String email,
                               User updatedUser,
                               @AuthenticationPrincipal User authenticatedUser,
                               RedirectAttributes redirectAttributes) {
        try {
            User currentUser = resolveUser(email, authenticatedUser);

            usersService.updateProfile(currentUser.getId(), updatedUser);
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    // Endpoints para gestión de direcciones
    @GetMapping({"/user/addresses/new", "/profile/addresses/new"})
    public String newAddress(@RequestParam(required = false) String email,
                             @AuthenticationPrincipal User authenticatedUser,
                             Model model) {
        User user = resolveUser(email, authenticatedUser);
        model.addAttribute("email", user.getEmail());
        return "users/address-form";
    }

    @PostMapping({"/user/addresses/create", "/profile/addresses/create"})
    public String createAddress(@RequestParam String email,
                                AddressRequestDto addressDto,
                                @AuthenticationPrincipal User authenticatedUser,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = resolveUser(email, authenticatedUser);

            addressDto.setUsersId(user.getId());
            addressService.addAddress(addressDto, user);
            redirectAttributes.addFlashAttribute("success", "Dirección creada correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear dirección: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    @GetMapping({"/user/addresses/{id}/edit", "/profile/addresses/{id}/edit"})
    public String editAddress(@PathVariable UUID id,
                             @RequestParam(required = false) String email,
                             @AuthenticationPrincipal User authenticatedUser,
                             Model model) {
        User user = resolveUser(email, authenticatedUser);
        AddressResponseDto address = addressService.findById(id);
        model.addAttribute("address", address);
        model.addAttribute("email", user.getEmail());
        return "users/address-form";
    }

    @PostMapping({"/user/addresses/{id}/update", "/profile/addresses/{id}/update"})
    public String updateAddress(@PathVariable UUID id,
                               @RequestParam String email,
                               AddressRequestDto addressDto,
                               @AuthenticationPrincipal User authenticatedUser,
                               RedirectAttributes redirectAttributes) {
        try {
            User user = resolveUser(email, authenticatedUser);

            addressDto.setUsersId(user.getId());
            addressService.updateAddress(id, addressDto, user);
            redirectAttributes.addFlashAttribute("success", "Dirección actualizada correctamente");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar dirección: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    @PostMapping({"/user/profile/addresses/{id}/delete", "/profile/addresses/{id}/delete"})
    public String deleteAddressFromProfile(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes
    ) {
        if (user == null) {
            return "redirect:/login";
        }

        try {
            addressService.delete(id, user);
            redirectAttributes.addFlashAttribute("success", "Dirección eliminada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar dirección: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    private User resolveUser(String email, User authenticatedUser) {
        if (authenticatedUser != null && !usersService.isAdmin(authenticatedUser)) {
            return usersService.findByUsername(authenticatedUser.getUsername())
                    .orElseGet(() -> usersService.findProfileByEmail(authenticatedUser.getEmail()));
        }
        if (email != null && !email.isBlank()) {
            return usersService.findProfileByEmail(email);
        }
        if (authenticatedUser != null) {
            return usersService.findByUsername(authenticatedUser.getUsername())
                    .orElseGet(() -> usersService.findProfileByEmail(authenticatedUser.getEmail()));
        }
        return usersService.findAnyProfileUser();
    }
}
