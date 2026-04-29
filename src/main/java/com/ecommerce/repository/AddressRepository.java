package com.ecommerce.repository;

import com.ecommerce.model.Address;
import com.ecommerce.model.Users;
import com.ecommerce.model.enums.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    // Consultas personalizadas para direcciones
    
    // Find addresses by user
    List<Address> findByUser(Users user);
    
    // Find addresses by city
    List<Address> findByCity(String city);
    
    // Find addresses by state
    List<Address> findByState(String state);
    
    // Find addresses by country
    List<Address> findByCountry(String country);
    
    // Find addresses by zip code
    List<Address> findByZipCode(String zipCode);
    
    // Find addresses by user and city
    List<Address> findByUserAndCity(Users user, String city);
    
    // Find addresses by user and country
    List<Address> findByUserAndCountry(Users user, String country);
    
    // Count addresses by user
    Long countByUser(Users user);
    
    // Find addresses by type
    List<Address> findByAddressType(AddressType addressType);
    
    // Find addresses by user and type
    List<Address> findByUserAndAddressType(Users user, AddressType addressType);
}
