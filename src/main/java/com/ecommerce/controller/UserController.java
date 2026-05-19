package com.ecommerce.controller;

import com.ecommerce.dto.UserRequestDto;
import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("register")
    public String register(Model model)
    {
        model.addAttribute("user", new UserRequestDto());
        return "/users/register";
    }

    @PostMapping("register")
    public String register(@ModelAttribute UserRequestDto form, RedirectAttributes redirectAttributes)
    {
        try {
            userService.register(form);
            redirectAttributes.addFlashAttribute("message", "Cuenta creada correctamente, inicia sesion");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }


//    @GetMapping("/users")
//    public String listUsers(Model model) {
//        List<User> users = userRepository.findAll();
//        model.addAttribute("users", users);
//        return "users/users-list";
//    }
//
//    @GetMapping("/users/{id}")
//    public String detailUser(@PathVariable UUID id, Model model) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));
//        model.addAttribute("user", user);
//        return "users/user-detail";
//}
}
