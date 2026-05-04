package com.ecommerce.controller.api;

import com.ecommerce.dto.BrandRequestDto;
import com.ecommerce.dto.BrandResponseDto;
import com.ecommerce.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandApiController
{
    private final BrandService brandService;

    @GetMapping
    public List<BrandResponseDto> findAll()
    {
        return brandService.findAll();
    }

    @GetMapping("/{id}")
    public BrandResponseDto findById(@PathVariable UUID id)
    {
        return brandService.findById(id);
    }

    @PostMapping
    public BrandResponseDto create(@RequestBody BrandRequestDto dto)
    {
        return brandService.create(dto);
    }

    @PutMapping("/{id}")
    public BrandResponseDto update(@PathVariable UUID id, @RequestBody BrandRequestDto dto)
    {
        return brandService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id)
    {
        brandService.delete(id);
    }
}
