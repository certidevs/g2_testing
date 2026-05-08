package com.ecommerce.service;

import com.ecommerce.dto.AddressRequestDto;
import com.ecommerce.dto.AddressResponseDto;
import com.ecommerce.model.Address;
import com.ecommerce.model.User;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository usersRepository;

    /**
     * Recupera todas las direcciones y las transforma a DTO de respuesta.
     */
    public List<AddressResponseDto> findAll() {
        return addressRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    /**
     * Busca una direccion por su ID.
     */
    public AddressResponseDto findById(UUID id) {
        Address address = findAddressEntityById(id);
        return toResponseDto(address);
    }

    /**
     * Crea una direccion a partir del DTO de entrada.
     */
    public AddressResponseDto create(AddressRequestDto dto) {
        User user = findUserEntityById(dto.getUsersId());

        Address address = Address.builder()
                .street(dto.getStreet())
                .number(dto.getNumber())
                .complement(dto.getComplement())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .zipCode(dto.getZipCode())
                .addressType(dto.getAddressType())
                .user(user)
                .build();

        return toResponseDto(addressRepository.save(address));
    }

    /**
     * Actualiza una direccion existente.
     */
    public AddressResponseDto update(UUID id, AddressRequestDto dto) {
        Address address = findAddressEntityById(id);
        User user = findUserEntityById(dto.getUsersId());

        address.setStreet(dto.getStreet());
        address.setNumber(dto.getNumber());
        address.setComplement(dto.getComplement());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setZipCode(dto.getZipCode());
        address.setAddressType(dto.getAddressType());
        address.setUser(user);

        return toResponseDto(addressRepository.save(address));
    }

    /**
     * Elimina una direccion por su ID.
     */
    public void delete(UUID id) {
        Address address = findAddressEntityById(id);
        addressRepository.delete(address);
    }

    private Address findAddressEntityById(UUID id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
    }

    private User findUserEntityById(UUID userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private AddressResponseDto toResponseDto(Address address) {
        return AddressResponseDto.builder()
                .id(address.getId())
                .street(address.getStreet())
                .number(address.getNumber())
                .complement(address.getComplement())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .zipCode(address.getZipCode())
                .addressType(address.getAddressType())
                .usersId(address.getUser() != null ? address.getUser().getId() : null)
                .usersName(address.getUser() != null ? address.getUser().getName() : null)
                .usersEmail(address.getUser() != null ? address.getUser().getEmail() : null)
                .build();
    }
}

