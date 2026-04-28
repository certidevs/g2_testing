package com.ecommerce.repository;

import com.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Long> {
    //  Conultas personalizadas

    //BUSCAR findAll

    // Buscar productos por nombre
    List<Product> findByTitle(String name);
    // Buscar productos por categoría
    //List<Product> findByCategoryId(UUID categoryId);

    //FILTRAR

    //Filtrar por rango de precio
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    //Filtrar por marca
    List<Product> findByBrandId(UUID brandId);

    // ORDENAR

    //Ordenar por precio de forma ascendente (Menor a Mayor)
    List<Product> findByAvailableTrueOrderByPriceAsc();
    //Ordenar por precio de forma Descendente (Mayor a Menor)
    List<Product> findByAvailableTrueOrderByPriceDesc();







}