package com.ecommerce.controller;

import com.ecommerce.dto.AddressRequestDto;
import com.ecommerce.dto.AddressResponseDto;
import com.ecommerce.model.Address;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.AddressType;
import com.ecommerce.model.enums.Role;
import com.ecommerce.repository.AddressRepository;
import com.ecommerce.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    private UUID userId;
    private UUID addressId;
    private User customer;
    private User admin;
    private Address address;
    private AddressResponseDto addressDto;
    private AddressResponseDto secondAddressDto;
    private AddressRequestDto requestDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        addressId = UUID.randomUUID();

        customer = User.builder()
                .id(userId)
                .username("customer")
                .name("Customer Name")
                .email("customer@example.com")
                .password("password")
                .role(Role.ROLE_CUSTOMER)
                .active(true)
                .build();

        admin = User.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .name("Admin Name")
                .email("admin@example.com")
                .password("password")
                .role(Role.ROLE_ADMIN)
                .active(true)
                .build();

        address = Address.builder()
                .id(addressId)
                .street("Calle Mayor")
                .number("10")
                .complement("Apt 1")
                .city("Madrid")
                .state("Madrid")
                .country("España")
                .zipCode("28001")
                .addressType(AddressType.BILLING)
                .user(customer)
                .build();

        addressDto = AddressResponseDto.builder()
                .id(addressId)
                .street("Calle Mayor")
                .number("10")
                .complement("Apt 1")
                .city("Madrid")
                .state("Madrid")
                .country("España")
                .zipCode("28001")
                .addressType(AddressType.BILLING)
                .usersId(customer.getId())
                .usersName(customer.getName())
                .usersEmail(customer.getEmail())
                .build();

        secondAddressDto = AddressResponseDto.builder()
                .id(UUID.randomUUID())
                .street("Avenida Diagonal")
                .number("55")
                .city("Barcelona")
                .state("Cataluña")
                .country("España")
                .zipCode("08019")
                .addressType(AddressType.SHIPPING)
                .usersId(customer.getId())
                .usersName(customer.getName())
                .usersEmail(customer.getEmail())
                .build();

        requestDto = validRequestDto(customer.getId());
    }

    @Test
    void listAddressesWhenUserIsNullRedirectsToLogin() {
        Model model = new ExtendedModelMap();

        String view = addressController.listAddresses(model, null);

        assertEquals("redirect:/login", view);
        assertTrue(model.asMap().isEmpty());
        verifyNoInteractions(addressService);
    }

    @Test
    void listAddressesWhenUserIsAdminAddsAllAddressesToModel() {
        Model model = new ExtendedModelMap();
        when(addressService.findAll()).thenReturn(List.of(addressDto, secondAddressDto));

        String view = addressController.listAddresses(model, admin);

        assertEquals("addresses/address-list", view);
        assertEquals(List.of(addressDto, secondAddressDto), model.asMap().get("addresses"));
        assertArrayEquals(AddressType.values(), (AddressType[]) model.asMap().get("addressTypes"));
        verify(addressService).findAll();
        verify(addressService, never()).findByUser(any());
    }

    @Test
    void listAddressesWhenUserIsCustomerAddsOnlyUserAddressesToModel() {
        Model model = new ExtendedModelMap();
        when(addressService.findByUser(customer)).thenReturn(List.of(addressDto));

        String view = addressController.listAddresses(model, customer);

        assertEquals("addresses/address-list", view);
        assertEquals(List.of(addressDto), model.asMap().get("addresses"));
        assertArrayEquals(AddressType.values(), (AddressType[]) model.asMap().get("addressTypes"));
        verify(addressService).findByUser(customer);
        verify(addressService, never()).findAll();
    }

    @Test
    void addressDetailWhenAddressExistsAddsAddressToModel() {
        Model model = new ExtendedModelMap();
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(address));

        String view = addressController.addressDetail(addressId, model);

        assertEquals("addresses/address-detail", view);
        assertSame(address, model.asMap().get("address"));
        verify(addressRepository).findById(addressId);
    }

    @Test
    void addressDetailWhenAddressDoesNotExistThrowsNotFound() {
        Model model = new ExtendedModelMap();
        when(addressRepository.findById(addressId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> addressController.addressDetail(addressId, model));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(addressRepository).findById(addressId);
    }

    @Test
    void showCreateAddressFormWhenUserIsNullRedirectsToLogin() {
        Model model = new ExtendedModelMap();

        String view = addressController.showCreateAddressForm(model, null);

        assertEquals("redirect:/login", view);
        assertTrue(model.asMap().isEmpty());
    }

    @Test
    void showCreateAddressFormWhenUserIsAuthenticatedAddsEmptyDtoToModel() {
        Model model = new ExtendedModelMap();

        String view = addressController.showCreateAddressForm(model, customer);

        AddressRequestDto addressModel = (AddressRequestDto) model.asMap().get("address");
        assertEquals("addresses/address-form", view);
        assertEquals(customer.getId(), addressModel.getUsersId());
        assertArrayEquals(AddressType.values(), (AddressType[]) model.asMap().get("addressTypes"));
    }

    @Test
    void saveAddressWhenUserIsNullRedirectsToLogin() {
        Model model = new ExtendedModelMap();
        BindingResult result = bindingResult(requestDto);

        String view = addressController.saveAddress(requestDto, result, null, model);

        assertEquals("redirect:/login", view);
        verifyNoInteractions(addressService);
    }

    @Test
    void saveAddressWhenValidationFailsReturnsForm() {
        Model model = new ExtendedModelMap();
        BindingResult result = bindingResultWithError(requestDto);

        String view = addressController.saveAddress(requestDto, result, customer, model);

        assertEquals("addresses/address-form", view);
        assertEquals(customer.getId(), requestDto.getUsersId());
        assertArrayEquals(AddressType.values(), (AddressType[]) model.asMap().get("addressTypes"));
        verify(addressService, never()).addAddress(any(), any());
    }

    @Test
    void saveAddressWhenServiceSucceedsRedirectsToAddresses() {
        Model model = new ExtendedModelMap();
        BindingResult result = bindingResult(requestDto);

        String view = addressController.saveAddress(requestDto, result, customer, model);

        assertEquals("redirect:/addresses", view);
        assertEquals(customer.getId(), requestDto.getUsersId());
        verify(addressService).addAddress(requestDto, customer);
    }

    @Test
    void saveAddressWhenServiceFailsReturnsFormWithErrorMessage() {
        Model model = new ExtendedModelMap();
        BindingResult result = bindingResult(requestDto);
        doThrow(new RuntimeException("service failure")).when(addressService).addAddress(requestDto, customer);

        String view = addressController.saveAddress(requestDto, result, customer, model);

        assertEquals("addresses/address-form", view);
        assertEquals("No se pudo registrar la dirección. Inténtelo de nuevo.", model.asMap().get("errorMessage"));
        assertArrayEquals(AddressType.values(), (AddressType[]) model.asMap().get("addressTypes"));
        verify(addressService).addAddress(requestDto, customer);
    }

    @Test
    void editFormWhenUserIsNullRedirectsToLogin() {
        Model model = new ExtendedModelMap();

        String view = addressController.editForm(addressId, model, null);

        assertEquals("redirect:/login", view);
        verify(addressService, never()).findById(any());
    }

    @Test
    void editFormWhenUserIsAuthenticatedAddsAddressDtoToModel() {
        Model model = new ExtendedModelMap();
        when(addressService.findById(addressId)).thenReturn(addressDto);

        String view = addressController.editForm(addressId, model, customer);

        AddressRequestDto addressModel = (AddressRequestDto) model.asMap().get("address");
        assertEquals("addresses/edit", view);
        assertEquals(addressId, model.asMap().get("addressId"));
        assertEquals(addressDto.getStreet(), addressModel.getStreet());
        assertEquals(addressDto.getNumber(), addressModel.getNumber());
        assertEquals(addressDto.getComplement(), addressModel.getComplement());
        assertEquals(addressDto.getCity(), addressModel.getCity());
        assertEquals(addressDto.getState(), addressModel.getState());
        assertEquals(addressDto.getCountry(), addressModel.getCountry());
        assertEquals(addressDto.getZipCode(), addressModel.getZipCode());
        assertEquals(addressDto.getAddressType(), addressModel.getAddressType());
        assertEquals(customer.getId(), addressModel.getUsersId());
        assertArrayEquals(AddressType.values(), (AddressType[]) model.asMap().get("addressTypes"));
        verify(addressService).findById(addressId);
    }

    @Test
    void updateWhenUserIsNullRedirectsToLogin() {
        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        BindingResult result = bindingResult(requestDto);

        String view = addressController.update(addressId, requestDto, result, model, redirectAttributes, null);

        assertEquals("redirect:/login", view);
        verify(addressService, never()).updateAddress(any(), any(), any());
    }

    @Test
    void updateWhenValidationFailsReturnsEditView() {
        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        BindingResult result = bindingResultWithError(requestDto);

        String view = addressController.update(addressId, requestDto, result, model, redirectAttributes, customer);

        assertEquals("addresses/edit", view);
        assertEquals(customer.getId(), requestDto.getUsersId());
        assertEquals(addressId, model.asMap().get("addressId"));
        assertArrayEquals(AddressType.values(), (AddressType[]) model.asMap().get("addressTypes"));
        verify(addressService, never()).updateAddress(any(), any(), any());
    }

    @Test
    void updateWhenServiceSucceedsRedirectsWithMessage() {
        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        BindingResult result = bindingResult(requestDto);

        String view = addressController.update(addressId, requestDto, result, model, redirectAttributes, customer);

        assertEquals("redirect:/addresses", view);
        assertEquals(customer.getId(), requestDto.getUsersId());
        assertEquals("Dirección actualizada correctamente", redirectAttributes.getFlashAttributes().get("message"));
        verify(addressService).updateAddress(addressId, requestDto, customer);
    }

    @Test
    void updateWhenServiceFailsRedirectsWithError() {
        Model model = new ExtendedModelMap();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        BindingResult result = bindingResult(requestDto);
        doThrow(new RuntimeException("sin permiso")).when(addressService).updateAddress(addressId, requestDto, customer);

        String view = addressController.update(addressId, requestDto, result, model, redirectAttributes, customer);

        assertEquals("redirect:/addresses", view);
        assertEquals("Error al actualizar la dirección: sin permiso", redirectAttributes.getFlashAttributes().get("error"));
        verify(addressService).updateAddress(addressId, requestDto, customer);
    }

    @Test
    void deleteAddressWhenUserIsNullRedirectsToLogin() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = addressController.deleteAddress(addressId, redirectAttributes, null);

        assertEquals("redirect:/login", view);
        verify(addressService, never()).delete(any(), any());
    }

    @Test
    void deleteAddressWhenServiceSucceedsRedirectsWithMessage() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String view = addressController.deleteAddress(addressId, redirectAttributes, customer);

        assertEquals("redirect:/addresses", view);
        assertEquals("La dirección se ha eliminado correctamente", redirectAttributes.getFlashAttributes().get("message"));
        verify(addressService).delete(addressId, customer);
    }

    @Test
    void deleteAddressWhenServiceFailsRedirectsWithError() {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        doThrow(new RuntimeException("no encontrada")).when(addressService).delete(addressId, customer);

        String view = addressController.deleteAddress(addressId, redirectAttributes, customer);

        assertEquals("redirect:/addresses", view);
        assertEquals("Error al eliminar la dirección: no encontrada", redirectAttributes.getFlashAttributes().get("error"));
        verify(addressService).delete(addressId, customer);
    }

    private AddressRequestDto validRequestDto(UUID usersId) {
        return AddressRequestDto.builder()
                .street("Calle Mayor")
                .number("10")
                .complement("Apt 1")
                .city("Madrid")
                .state("Madrid")
                .country("España")
                .zipCode("28001")
                .addressType(AddressType.BILLING)
                .usersId(usersId)
                .build();
    }

    private BindingResult bindingResult(AddressRequestDto dto) {
        return new BeanPropertyBindingResult(dto, "address");
    }

    private BindingResult bindingResultWithError(AddressRequestDto dto) {
        BindingResult result = bindingResult(dto);
        result.addError(new FieldError("address", "street", "La calle es obligatoria"));
        return result;
    }
}
