package com.ecommerce.controller.advice;

import com.ecommerce.dto.CategoryResponseDto;
import com.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class NavbarControllerAdvice
{
    private final CategoryService categoryService;

    @ModelAttribute("rootCategories")
    public List<CategoryResponseDto> rootCategories() {
        return categoryService.findActiveRootCategoriesForNavbar();
    }
}
