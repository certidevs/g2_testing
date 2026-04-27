package com.ecommers.repository;

import com.ecommers.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    //  Conultas personalizadas

    //BUSCAR

    // Buscar productos por nombre
    List<Product> findByName(String name);
    // Buscar productos por categoría
    List<Product> findByCategory(String category);

    //FILTRAR

    //Filtrar por rango de precio
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    //Filtrar por marca
    List<Product> findByBrand(String brand);

    // ORDENAR

    //Ordenar por precio de forma ascendente (Menor a Mayor)
    List<Product> findAllByOrderByPriceAsc();
    //Ordenar por precio de forma Descendente (Mayor a Menor)
    List<Product> findAllByOrderByPriceDesc();


}