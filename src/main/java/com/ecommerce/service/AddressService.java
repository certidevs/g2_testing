package com.ecommerce.service;

import com.ecommerce.dto.AddressRequestDto;
import com.ecommerce.dto.AddressResponseDto;
import com.ecommerce.model.Address;
import com.ecommerce.model.User;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository usersRepository;

    // Encontrar direcciones por usuario
    public List<AddressResponseDto> findByUser(User user) {
        return addressRepository.findByUser(user)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    // Agregar una nueva dirección
    @Transactional
    public void addAddress(AddressRequestDto form, User user) {
        validateLoggedUser(form, user);

        Address address = new Address();
        address.setCountry(form.getCountry());
        address.setCity(form.getCity());
        address.setState(form.getState());
        address.setZipCode(form.getZipCode());
        address.setStreet(form.getStreet());
        address.setNumber(form.getNumber());
        address.setAddressType(form.getAddressType());
        address.setComplement(form.getComplement());
        address.setUser(user);

        addressRepository.save(address);
    }

    // Obtener todas las direcciones
    public List<AddressResponseDto> findAll() {
        return addressRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    // Encontrar dirección por ID
    public AddressResponseDto findById(UUID id) {
        Address address = findAddressEntityById(id);
        return toResponseDto(address);
    }

    // Actualizar una dirección
    @Transactional
    public AddressResponseDto updateAddress(UUID id, AddressRequestDto dto, User user) {
        validateLoggedUser(dto, user);

        Address address = findAddressEntityById(id);
        validateAddressOwner(address, user);

        address.setStreet(dto.getStreet());
        address.setNumber(dto.getNumber());
        address.setComplement(dto.getComplement());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setZipCode(dto.getZipCode());
        address.setAddressType(dto.getAddressType());

        return toResponseDto(addressRepository.save(address));
    }

    // Eliminar una dirección
    @Transactional
    public void delete(UUID id, User user) {
        Address address = findAddressEntityById(id);
        validateAddressOwner(address, user);

        addressRepository.delete(address);
    }

    // Encontrar dirección por ID
    private Address findAddressEntityById(UUID id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dirección no encontrada con ID: " + id));
    }

    // Encontrar usuario por ID [STANDBY]
    private User findUserEntityById(UUID userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
    }

    // Convertir entidad a DTO
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

    // Valida que el usuario logeado corresponda al usuario del formulario
    private void validateLoggedUser(AddressRequestDto dto, User user) {
        if (user == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        if (dto.getUsersId() == null) {
            throw new RuntimeException("El ID del usuario es obligatorio");
        }

        if (!dto.getUsersId().equals(user.getId())) {
            throw new RuntimeException("El usuario del formulario no coincide con el usuario autenticado");
        }
    }

    // Valida que el usuario autenticado sea el dueño de la dirección
    private void validateAddressOwner(Address address, User user) {
        if (address.getUser() == null || !address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permiso para modificar esta dirección");
        }
    }
}