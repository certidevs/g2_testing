package com.ecommerce.controller;

import com.ecommerce.model.User;
import com.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public String listUsers(@RequestParam(required = false) String adminEmail, Model model) {
        String resolvedAdminEmail = userService.resolveAdminEmail(adminEmail);
        userService.validateAdminAccess(resolvedAdminEmail);

        model.addAttribute("adminEmail", resolvedAdminEmail);
        model.addAttribute("users", userService.findAll());
        return "users/users-admin-list";
    }

    @GetMapping("/{id}/edit")
    public String editUser(@PathVariable UUID id,
                           @RequestParam(required = false) String adminEmail,
                           Model model) {
        String resolvedAdminEmail = userService.resolveAdminEmail(adminEmail);
        userService.validateAdminAccess(resolvedAdminEmail);

        model.addAttribute("adminEmail", resolvedAdminEmail);
        model.addAttribute("user", userService.findById(id));
        return "users/user-admin-edit";
    }

    @PostMapping("/{id}/edit")
    public String updateUser(@PathVariable UUID id,
                             @RequestParam(required = false) String adminEmail,
                             User updatedUser,
                             RedirectAttributes redirectAttributes) {
        String resolvedAdminEmail = userService.resolveAdminEmail(adminEmail);

        try {
            userService.validateAdminAccess(resolvedAdminEmail);
            userService.updateUserByAdmin(id, updatedUser);
            redirectAttributes.addFlashAttribute("success", "Usuario actualizado correctamente");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/users/" + id + "/edit?adminEmail=" + resolvedAdminEmail;
        }

        return "redirect:/admin/users?adminEmail=" + resolvedAdminEmail;
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable UUID id,
                               @RequestParam(required = false) String adminEmail,
                               RedirectAttributes redirectAttributes) {
        String resolvedAdminEmail = userService.resolveAdminEmail(adminEmail);

        try {
            userService.validateAdminAccess(resolvedAdminEmail);
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("success", "Estado del usuario actualizado");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/admin/users?adminEmail=" + resolvedAdminEmail;
    }

    @PostMapping("/{id}/delete")
    public String softDelete(@PathVariable UUID id,
                             @RequestParam(required = false) String adminEmail,
                             RedirectAttributes redirectAttributes) {
        String resolvedAdminEmail = userService.resolveAdminEmail(adminEmail);

        try {
            userService.validateAdminAccess(resolvedAdminEmail);
            userService.softDeleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Usuario desactivado correctamente");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/admin/users?adminEmail=" + resolvedAdminEmail;
    }
}

