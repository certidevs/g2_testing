package com.ecommerce.service;

import com.ecommerce.dto.AddressRequestDto;
import com.ecommerce.dto.AddressResponseDto;
import com.ecommerce.model.Address;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.AddressType;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressService addressService;

    private UUID userId;
    private UUID otherUserId;
    private UUID addressId;
    private User user;
    private User otherUser;
    private Address address;
    private Address secondAddress;
    private AddressRequestDto request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();
        addressId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .name("Test User")
                .email("test@example.com")
                .username("testuser")
                .password("password")
                .build();

        otherUser = User.builder()
                .id(otherUserId)
                .name("Other User")
                .email("other@example.com")
                .username("otheruser")
                .password("password")
                .build();

        address = Address.builder()
                .id(addressId)
                .street("123 Main St")
                .number("10")
                .complement("Apt 1")
                .city("Anytown")
                .state("State")
                .country("Country")
                .zipCode("12345")
                .addressType(AddressType.SHIPPING)
                .user(user)
                .build();

        secondAddress = Address.builder()
                .id(UUID.randomUUID())
                .street("456 Oak Ave")
                .number("20")
                .city("Otherville")
                .state("Other State")
                .country("Other Country")
                .zipCode("67890")
                .addressType(AddressType.BILLING)
                .user(user)
                .build();

        request = validRequest(userId);
    }

    @Test
    void findByUserReturnsAddressDtos() {
        when(addressRepository.findByUser(user)).thenReturn(List.of(address, secondAddress));

        List<AddressResponseDto> foundAddresses = addressService.findByUser(user);

        assertEquals(2, foundAddresses.size());
        assertAddressDto(address, foundAddresses.get(0));
        assertAddressDto(secondAddress, foundAddresses.get(1));
        verify(addressRepository).findByUser(user);
    }

    @Test
    void addAddressSavesAddressForLoggedUser() {
        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);

        addressService.addAddress(request, user);

        verify(addressRepository).save(captor.capture());
        Address savedAddress = captor.getValue();
        assertNull(savedAddress.getId());
        assertEquals(request.getStreet(), savedAddress.getStreet());
        assertEquals(request.getNumber(), savedAddress.getNumber());
        assertEquals(request.getComplement(), savedAddress.getComplement());
        assertEquals(request.getCity(), savedAddress.getCity());
        assertEquals(request.getState(), savedAddress.getState());
        assertEquals(request.getCountry(), savedAddress.getCountry());
        assertEquals(request.getZipCode(), savedAddress.getZipCode());
        assertEquals(request.getAddressType(), savedAddress.getAddressType());
        assertSame(user, savedAddress.getUser());
    }

    @Test
    void addAddressThrowsWhenUserIsNotAuthenticated() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> addressService.addAddress(request, null));

        assertEquals("Usuario no autenticado", exception.getMessage());
        verifyNoInteractions(addressRepository);
    }

    @Test
    void addAddressThrowsWhenRequestUserIdIsMissing() {
        request.setUsersId(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> addressService.addAddress(request, user));

        assertEquals("El ID del usuario es obligatorio", exception.getMessage());
        verifyNoInteractions(addressRepository);
    }

    @Test
    void addAddressThrowsWhenRequestUserDoesNotMatchAuthenticatedUser() {
        request.setUsersId(otherUserId);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> addressService.addAddress(request, user));

        assertEquals("El usuario del formulario no coincide con el usuario autenticado", exception.getMessage());
        verifyNoInteractions(addressRepository);
    }

    @Test
    void findAllReturnsDtosWithAndWithoutUserData() {
        Address addressWithoutUser = Address.builder()
                .id(UUID.randomUUID())
                .street("No User St")
                .number("30")
                .city("Nowhere")
                .state("None")
                .country("Country")
                .zipCode("00000")
                .addressType(AddressType.SHIPPING)
                .build();

        when(addressRepository.findAll()).thenReturn(List.of(address, addressWithoutUser));

        List<AddressResponseDto> allAddresses = addressService.findAll();

        assertEquals(2, allAddresses.size());
        assertAddressDto(address, allAddresses.get(0));
        assertNull(allAddresses.get(1).getUsersId());
        assertNull(allAddresses.get(1).getUsersName());
        assertNull(allAddresses.get(1).getUsersEmail());
        verify(addressRepository).findAll();
    }

    @Test
    void findByIdReturnsAddressDto() {
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        AddressResponseDto foundAddress = addressService.findById(addressId);

        assertAddressDto(address, foundAddress);
        verify(addressRepository).findById(addressId);
    }

    @Test
    void findByIdThrowsWhenAddressDoesNotExist() {
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> addressService.findById(addressId));

        assertEquals("Dirección no encontrada con ID: " + addressId, exception.getMessage());
        verify(addressRepository).findById(addressId);
    }

    @Test
    void updateAddressUpdatesAndReturnsDtoWhenUserOwnsAddress() {
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));
        when(addressRepository.save(address)).thenReturn(address);

        AddressResponseDto updatedAddress = addressService.updateAddress(addressId, request, user);

        assertEquals(request.getStreet(), address.getStreet());
        assertEquals(request.getNumber(), address.getNumber());
        assertEquals(request.getComplement(), address.getComplement());
        assertEquals(request.getCity(), address.getCity());
        assertEquals(request.getState(), address.getState());
        assertEquals(request.getCountry(), address.getCountry());
        assertEquals(request.getZipCode(), address.getZipCode());
        assertEquals(request.getAddressType(), address.getAddressType());
        assertAddressDto(address, updatedAddress);
        verify(addressRepository).findById(addressId);
        verify(addressRepository).save(address);
    }

    @Test
    void updateAddressThrowsWhenAddressHasNoOwner() {
        address.setUser(null);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> addressService.updateAddress(addressId, request, user));

        assertEquals("No tienes permiso para modificar esta dirección", exception.getMessage());
        verify(addressRepository).findById(addressId);
        verify(addressRepository, never()).save(any());
    }

    @Test
    void updateAddressThrowsWhenAuthenticatedUserIsNotTheOwner() {
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> addressService.updateAddress(addressId, validRequest(otherUserId), otherUser));

        assertEquals("No tienes permiso para modificar esta dirección", exception.getMessage());
        verify(addressRepository).findById(addressId);
        verify(addressRepository, never()).save(any());
    }

    @Test
    void deleteRemovesAddressWhenUserOwnsAddress() {
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        addressService.delete(addressId, user);

        verify(addressRepository).findById(addressId);
        verify(addressRepository).delete(address);
    }

    @Test
    void deleteThrowsWhenAddressDoesNotExist() {
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> addressService.delete(addressId, user));

        assertEquals("Dirección no encontrada con ID: " + addressId, exception.getMessage());
        verify(addressRepository).findById(addressId);
        verify(addressRepository, never()).delete(any());
    }

    @Test
    void findUserEntityByIdReturnsUser() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = invokeFindUserEntityById(userId);

        assertSame(user, foundUser);
        verify(userRepository).findById(userId);
    }

    @Test
    void findUserEntityByIdThrowsWhenUserDoesNotExist() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        InvocationTargetException exception = assertThrows(InvocationTargetException.class,
                () -> invokeFindUserEntityById(userId));

        Throwable cause = exception.getCause();
        assertInstanceOf(RuntimeException.class, cause);
        assertEquals("Usuario no encontrado con ID: " + userId, cause.getMessage());
        verify(userRepository).findById(userId);
    }

    private AddressRequestDto validRequest(UUID requestUserId) {
        return AddressRequestDto.builder()
                .street("Updated Main St")
                .number("99")
                .complement("Floor 2")
                .city("Updated City")
                .state("Updated State")
                .country("Updated Country")
                .zipCode("54321")
                .addressType(AddressType.BILLING)
                .usersId(requestUserId)
                .build();
    }

    private void assertAddressDto(Address expected, AddressResponseDto actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getStreet(), actual.getStreet());
        assertEquals(expected.getNumber(), actual.getNumber());
        assertEquals(expected.getComplement(), actual.getComplement());
        assertEquals(expected.getCity(), actual.getCity());
        assertEquals(expected.getState(), actual.getState());
        assertEquals(expected.getCountry(), actual.getCountry());
        assertEquals(expected.getZipCode(), actual.getZipCode());
        assertEquals(expected.getAddressType(), actual.getAddressType());
        assertEquals(expected.getUser().getId(), actual.getUsersId());
        assertEquals(expected.getUser().getName(), actual.getUsersName());
        assertEquals(expected.getUser().getEmail(), actual.getUsersEmail());
    }

    private User invokeFindUserEntityById(UUID id) throws Exception {
        Method method = AddressService.class.getDeclaredMethod("findUserEntityById", UUID.class);
        method.setAccessible(true);
        return (User) method.invoke(addressService, id);
    }
}
