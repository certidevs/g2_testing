package com.ecommerce.controller.api;

import com.ecommerce.dto.CategoryRequestDto;
import com.ecommerce.dto.CategoryResponseDto;
import com.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryApiController
{
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponseDto> findAll()
    {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public CategoryResponseDto findById(@PathVariable UUID id)
    {
        return categoryService.findById(id);
    }

    @PostMapping
    public CategoryResponseDto create(@RequestBody CategoryRequestDto dto)
    {
        return categoryService.create(dto);
    }

    @PutMapping("/{id}")
    public CategoryResponseDto update(@PathVariable UUID id, @RequestBody CategoryRequestDto dto)
    {
        return categoryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id)
    {
        categoryService.delete(id);
    }

    @GetMapping("/tree")
    public List<CategoryResponseDto> findRootCategories()
    {
        return categoryService.findRootCategories();
    }

}
