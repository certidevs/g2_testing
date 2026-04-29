package com.ecommerce.repository;

import com.ecommerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID>
{
    // Trae categorías raíz y sus hijos en una sola consulta
    @Query("select c from Category c left join fetch c.children where c.parent is null")
    List<Category> findAllRootWithChildren();
}
