package com.ecommerce.controller;

import com.ecommerce.dto.AddressRequestDto;
import com.ecommerce.dto.AddressResponseDto;
import com.ecommerce.model.User;
import com.ecommerce.service.AddressService;
import com.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;


@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService usersService;
    private final AddressService addressService;

    @GetMapping("/profile")
    public String viewProfile(@RequestParam(required = false) String email, Model model) {
        User user = resolveUser(email);
        model.addAttribute("user", user);
        return "user/profile";
    }

    @GetMapping("/edit")
    public String editProfile(@RequestParam(required = false) String email, Model model) {
        User user = resolveUser(email);
        model.addAttribute("user", user);
        return "user/profile-form";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam String email,
                               User updatedUser,
                               RedirectAttributes redirectAttributes) {
        try {
            User currentUser = usersService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            usersService.updateProfile(currentUser.getId(), updatedUser);
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }
        
        return "redirect:/user/profile?email=" + email;
    }

    // Endpoints para gestión de direcciones
    @GetMapping("/addresses/new")
    public String newAddress(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "user/address-form";
    }

    @PostMapping("/addresses/create")
    public String createAddress(@RequestParam String email,
                               AddressRequestDto addressDto,
                               RedirectAttributes redirectAttributes) {
        try {
            User user = usersService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            addressDto.setUsersId(user.getId());
            addressService.create(addressDto);
            redirectAttributes.addFlashAttribute("success", "Dirección creada correctamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear dirección: " + e.getMessage());
        }
        
        return "redirect:/user/profile?email=" + email;
    }

    @GetMapping("/addresses/{id}/edit")
    public String editAddress(@PathVariable UUID id,
                             @RequestParam(required = false) String email,
                             Model model) {
        AddressResponseDto address = addressService.findById(id);
        model.addAttribute("address", address);
        model.addAttribute("email", email);
        return "user/address-form";
    }

    @PostMapping("/addresses/{id}/update")
    public String updateAddress(@PathVariable UUID id,
                               @RequestParam String email,
                               AddressRequestDto addressDto,
                               RedirectAttributes redirectAttributes) {
        try {
            User user = usersService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            addressDto.setUsersId(user.getId());
            addressService.update(id, addressDto);
            redirectAttributes.addFlashAttribute("success", "Dirección actualizada correctamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar dirección: " + e.getMessage());
        }
        
        return "redirect:/user/profile?email=" + email;
    }

    @PostMapping("/addresses/{id}/delete")
    public String deleteAddress(@PathVariable UUID id,
                               @RequestParam String email,
                               RedirectAttributes redirectAttributes) {
        try {
            addressService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Dirección eliminada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar dirección: " + e.getMessage());
        }
        
        return "redirect:/user/profile?email=" + email;
    }

    private User resolveUser(String email) {
        if (email != null && !email.isBlank()) {
            return usersService.findProfileByEmail(email);
        }
        return usersService.findAnyProfileUser();
    }
}
