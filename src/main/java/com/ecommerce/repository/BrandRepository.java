package com.ecommerce.repository;

import com.ecommerce.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID>
{
    Optional<Brand> findByName(String name);

    boolean existsByName(String name);

    Optional<Brand> findByNif(String nif);

    boolean existsByNif(String nif);

    boolean existsByNameAndIdNot(String name, UUID id);

    boolean existsByNifAndIdNot(String nif, UUID id);
}
