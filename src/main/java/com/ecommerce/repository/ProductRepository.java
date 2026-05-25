package com.ecommerce.repository;

import com.ecommerce.model.Product;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    //  Conultas personalizadas

    //BUSCAR findAll

    // Buscar productos por nombre
    List<Product> findByTitle(String name);
    // Buscar productos por categoría
    //List<Product> findByCategoryId(UUID categoryId);

    // Buscar productos por disponibilidad con paginacion -
    List<Product> findByTitleContainsIgnoreCase(String nombre);


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




    List<Product> findByTitleContainingIgnoreCaseOrShortDescriptionContainingIgnoreCase(String query, String query1);

//    List<Product> findBySubcategoryId(UUID id);

    List<Product> findBySubcategoryIdIn(List<UUID> ids);
    List<Product> findByAvailableTrue();


    // TODO crear query  @query filtre por subcategory.slug OR subcategory.parent.slug
    List<Product> findBySubcategorySlug(String slug);

}