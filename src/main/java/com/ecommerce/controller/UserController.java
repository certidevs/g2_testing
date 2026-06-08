package com.ecommerce.controller;

import com.ecommerce.dto.UserRequestDto;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("register")
    public String register(Model model)
    {
        model.addAttribute("user", new UserRequestDto());
        return "/auth/register";
    }

    @PostMapping("register")
    public String register(@Valid @ModelAttribute("user") UserRequestDto form, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes)
    {
        if (form.getPassword() != null
                && form.getPasswordConfirm() != null
                && !form.getPassword().equals(form.getPasswordConfirm())) {

            bindingResult.rejectValue(
                    "passwordConfirm",
                    "passwordConfirm.error",
                    "Las contraseñas no coinciden"
            );
        }

        if (bindingResult.hasErrors()) {
            return "/auth/register";
        }

        try {
            userService.register(form);
            redirectAttributes.addFlashAttribute("message", "Cuenta creada correctamente, inicia sesión");
            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "/auth/register";
        }
    }

    @GetMapping("login")
    public String login(Model model){
        return "/auth/login";
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
