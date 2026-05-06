package com.ecommerce.repository;

import com.ecommerce.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {
    // Método original, que puede causar LazyInitializationException si el Product no se carga explícitamente
    // List<Favorite> findByUserId(UUID userId);

    // Nuevo método para cargar Favorites y sus Products asociados en una sola consulta
    @Query("SELECT f FROM Favorite f JOIN FETCH f.product WHERE f.user.id = :userId")
    List<Favorite> findByUserIdWithProducts(UUID userId);

    Optional<Favorite> findByUserIdAndProductId(UUID userId, UUID productId);
    long countByUserId(UUID userId);
}