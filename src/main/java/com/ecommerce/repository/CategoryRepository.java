package com.ecommerce.repository;

import com.ecommerce.model.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID>
{
    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    List<Category> findByParentIsNull();

    List<Category> findByParentId(UUID parentId);

    boolean existsByParentIdAndSlug(UUID parentId, String slug);

    boolean existsByParentIsNullAndSlug(String slug);

    // Trae categorías raíz y sus hijos en una sola consulta
    @Query("select distinct c from Category c left join fetch c.children where c.parent is null")
    List<Category> findAllRootWithChildren();

    // Alternativa usando EntityGraph para controlar la carga de relaciones
    @EntityGraph(attributePaths = {"children"})
    @Query("select c from Category c where c.parent is null")
    List<Category> findAllRootWithChildrenEntityGraph();
}
