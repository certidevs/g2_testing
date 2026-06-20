package com.ecommerce.controller;

import com.ecommerce.dto.AddressRequestDto;
import com.ecommerce.dto.AddressResponseDto;
import com.ecommerce.model.User;
import com.ecommerce.service.AddressService;
import com.ecommerce.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService usersService;

    @Mock
    private AddressService addressService;

    private User authenticatedUser;
    private User profileUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        ProfileController controller = new ProfileController(usersService, addressService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        authenticatedUser = createUser("cliente", "cliente@test.com");
        profileUser = createUser("perfil", "perfil@test.com");
        adminUser = createUser("admin", "admin@test.com");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private User createUser(String username, String email) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setEmail(email);
        user.setName("Nombre " + username);
        user.setLastName("Apellido " + username);
        return user;
    }

    private RequestPostProcessor authenticatedAs(User user) {
        return request -> {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            return request;
        };
    }

    @Test
    void viewProfile_whenAuthenticatedUserIsNotAdmin_shouldUseAuthenticatedUser() throws Exception {
        when(usersService.isAdmin(authenticatedUser)).thenReturn(false);
        when(usersService.findByUsername(authenticatedUser.getUsername()))
                .thenReturn(Optional.of(profileUser));

        mockMvc.perform(get("/profile").with(authenticatedAs(authenticatedUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("users/profile"))
                .andExpect(model().attribute("user", profileUser));

        verify(usersService).isAdmin(authenticatedUser);
        verify(usersService).findByUsername(authenticatedUser.getUsername());
        verify(usersService, never()).findAnyProfileUser();
    }

    @Test
    void viewProfile_whenAdminAndEmailIsPresent_shouldFindProfileByEmail() throws Exception {
        String email = "otro@test.com";

        when(usersService.isAdmin(adminUser)).thenReturn(true);
        when(usersService.findProfileByEmail(email)).thenReturn(profileUser);

        mockMvc.perform(get("/user/profile")
                        .param("email", email)
                        .with(authenticatedAs(adminUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("users/profile"))
                .andExpect(model().attribute("user", profileUser));

        verify(usersService).isAdmin(adminUser);
        verify(usersService).findProfileByEmail(email);
    }

    @Test
    void viewProfile_whenAdminAndEmailIsBlank_shouldUseAuthenticatedAdmin() throws Exception {
        when(usersService.isAdmin(adminUser)).thenReturn(true);
        when(usersService.findByUsername(adminUser.getUsername()))
                .thenReturn(Optional.of(adminUser));

        mockMvc.perform(get("/users/profile")
                        .param("email", "   ")
                        .with(authenticatedAs(adminUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("users/profile"))
                .andExpect(model().attribute("user", adminUser));

        verify(usersService).isAdmin(adminUser);
        verify(usersService).findByUsername(adminUser.getUsername());
        verify(usersService, never()).findProfileByEmail("   ");
    }

    @Test
    void viewProfile_whenNoAuthenticatedUserAndNoEmail_shouldFindAnyProfileUser() throws Exception {
        when(usersService.findAnyProfileUser()).thenReturn(profileUser);

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/profile"))
                .andExpect(model().attribute("user", profileUser));

        verify(usersService).findAnyProfileUser();
    }

    @Test
    void viewProfile_whenAuthenticatedUserNotFoundByUsername_shouldFindByEmailFallback() throws Exception {
        when(usersService.isAdmin(authenticatedUser)).thenReturn(false);
        when(usersService.findByUsername(authenticatedUser.getUsername()))
                .thenReturn(Optional.empty());
        when(usersService.findProfileByEmail(authenticatedUser.getEmail()))
                .thenReturn(profileUser);

        mockMvc.perform(get("/profile").with(authenticatedAs(authenticatedUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("users/profile"))
                .andExpect(model().attribute("user", profileUser));

        verify(usersService).findByUsername(authenticatedUser.getUsername());
        verify(usersService).findProfileByEmail(authenticatedUser.getEmail());
    }

    @Test
    void editProfile_shouldReturnProfileForm() throws Exception {
        when(usersService.isAdmin(authenticatedUser)).thenReturn(false);
        when(usersService.findByUsername(authenticatedUser.getUsername()))
                .thenReturn(Optional.of(profileUser));

        mockMvc.perform(get("/profile/edit").with(authenticatedAs(authenticatedUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("users/profile-form"))
                .andExpect(model().attribute("user", profileUser));
    }

    @Test
    void updateProfile_whenSuccess_shouldRedirectWithSuccessMessage() throws Exception {
        when(usersService.findByUsername(authenticatedUser.getUsername()))
                .thenReturn(Optional.of(profileUser));

        mockMvc.perform(post("/profile/update")
                        .param("name", "Nuevo nombre")
                        .param("lastName", "Nuevo apellido")
                        .param("phone", "600000000")
                        .with(authenticatedAs(authenticatedUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/profile*"))
                .andExpect(flash().attribute("success", "Perfil actualizado correctamente"));

        verify(usersService).updateProfile(eq(profileUser.getId()), any(User.class));
    }

    @Test
    void updateProfile_whenServiceThrowsException_shouldRedirectWithErrorMessage() throws Exception {
        when(usersService.findByUsername(authenticatedUser.getUsername()))
                .thenReturn(Optional.of(profileUser));

        doThrow(new RuntimeException("fallo test"))
                .when(usersService)
                .updateProfile(eq(profileUser.getId()), any(User.class));

        mockMvc.perform(post("/user/update")
                        .param("name", "Nombre incorrecto")
                        .with(authenticatedAs(authenticatedUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/profile*"))
                .andExpect(flash().attributeExists("error"));

        verify(usersService).updateProfile(eq(profileUser.getId()), any(User.class));
    }

    @Test
    void updateProfile_whenNoAuthenticatedUser_shouldRedirectWithErrorMessage() throws Exception {
        mockMvc.perform(post("/users/profile/update")
                        .param("name", "Sin usuario"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/profile*"))
                .andExpect(flash().attributeExists("error"));

        verify(usersService, never()).updateProfile(any(), any());
    }

    @Test
    void newAddress_shouldReturnAddressFormWithEmail() throws Exception {
        when(usersService.isAdmin(authenticatedUser)).thenReturn(false);
        when(usersService.findByUsername(authenticatedUser.getUsername()))
                .thenReturn(Optional.of(profileUser));

        mockMvc.perform(get("/profile/addresses/new")
                        .with(authenticatedAs(authenticatedUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("users/address-form"))
                .andExpect(model().attribute("email", profileUser.getEmail()));
    }

    @Test
    void createAddress_whenSuccess_shouldSetUserIdAndRedirectWithSuccessMessage() throws Exception {
        when(usersService.isAdmin(authenticatedUser)).thenReturn(false);
        when(usersService.findByUsername(authenticatedUser.getUsername()))
                .thenReturn(Optional.of(profileUser));

        mockMvc.perform(post("/profile/addresses/create")
                        .param("email", profileUser.getEmail())
                        .param("street", "Calle Test")
                        .param("number", "10")
                        .param("city", "Madrid")
                        .param("country", "España")
                        .param("zipCode", "28001")
                        .with(authenticatedAs(authenticatedUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/profile*"))
                .andExpect(flash().attribute("success", "Dirección creada correctamente"));

        ArgumentCaptor<AddressRequestDto> dtoCaptor =
                ArgumentCaptor.forClass(AddressRequestDto.class);

        verify(addressService).addAddress(dtoCaptor.capture(), eq(profileUser));

        assertEquals(profileUser.getId(), dtoCaptor.getValue().getUsersId());
    }

    @Test
    void createAddress_whenServiceThrowsException_shouldRedirectWithErrorMessage() throws Exception {
        when(usersService.isAdmin(authenticatedUser)).thenReturn(false);
        when(usersService.findByUsername(authenticatedUser.getUsername()))
                .thenReturn(Optional.of(profileUser));

        doThrow(new RuntimeException("fallo dirección"))
                .when(addressService)
                .addAddress(any(AddressRequestDto.class), eq(profileUser));

        mockMvc.perform(post("/user/addresses/create")
                        .param("email", profileUser.getEmail())
                        .param("street", "Calle Error")
                        .with(authenticatedAs(authenticatedUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/profile*"))
                .andExpect(flash().attributeExists("error"));

        verify(addressService).addAddress(any(AddressRequestDto.class), eq(profileUser));
    }

    @Test
    void editAddress_shouldReturnAddressFormWithAddressAndEmail() throws Exception {
        UUID addressId = UUID.randomUUID();
        AddressResponseDto addressResponseDto = mock(AddressResponseDto.class);

        when(usersService.isAdmin(authenticatedUser)).thenReturn(false);
        when(usersService.findByUsername(authenticatedUser.getUsername()))
                .thenReturn(Optional.of(profileUser));
        when(addressService.findById(addressId)).thenReturn(addressResponseDto);

        mockMvc.perform(get("/profile/addresses/" + addressId + "/edit")
                        .with(authenticatedAs(authenticatedUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("users/address-form"))
                .andExpect(model().attribute("address", addressResponseDto))
                .andExpect(model().attribute("email", profileUser.getEmail()));

        verify(addressService).findById(addressId);
    }

    @Test
    void updateAddress_whenSuccess_shouldSetUserIdAndRedirectWithSuccessMessage() throws Exception {
        UUID addressId = UUID.randomUUID();

        when(usersService.isAdmin(authenticatedUser)).thenReturn(false);
        when(usersService.findByUsername(authenticatedUser.getUsername()))
                .thenReturn(Optional.of(profileUser));

        mockMvc.perform(post("/profile/addresses/" + addressId + "/update")
                        .param("email", profileUser.getEmail())
                        .param("street", "Calle Actualizada")
                        .param("number", "20")
                        .param("city", "Madrid")
                        .param("country", "España")
                        .param("zipCode", "28002")
                        .with(authenticatedAs(authenticatedUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/profile*"))
                .andExpect(flash().attribute("success", "Dirección actualizada correctamente"));

        ArgumentCaptor<AddressRequestDto> dtoCaptor =
                ArgumentCaptor.forClass(AddressRequestDto.class);

        verify(addressService).updateAddress(eq(addressId), dtoCaptor.capture(), eq(profileUser));

        assertEquals(profileUser.getId(), dtoCaptor.getValue().getUsersId());
    }

    @Test
    void updateAddress_whenServiceThrowsException_shouldRedirectWithErrorMessage() throws Exception {
        UUID addressId = UUID.randomUUID();

        when(usersService.isAdmin(authenticatedUser)).thenReturn(false);
        when(usersService.findByUsername(authenticatedUser.getUsername()))
                .thenReturn(Optional.of(profileUser));

        doThrow(new RuntimeException("fallo actualización"))
                .when(addressService)
                .updateAddress(eq(addressId), any(AddressRequestDto.class), eq(profileUser));

        mockMvc.perform(post("/user/addresses/" + addressId + "/update")
                        .param("email", profileUser.getEmail())
                        .param("street", "Calle Error")
                        .with(authenticatedAs(authenticatedUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/profile*"))
                .andExpect(flash().attributeExists("error"));

        verify(addressService).updateAddress(eq(addressId), any(AddressRequestDto.class), eq(profileUser));
    }

    @Test
    void deleteAddressFromProfile_whenUserIsNull_shouldRedirectToLogin() throws Exception {
        UUID addressId = UUID.randomUUID();

        mockMvc.perform(post("/profile/addresses/" + addressId + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/login*"));

        verify(addressService, never()).delete(any(), any());
    }

    @Test
    void deleteAddressFromProfile_whenSuccess_shouldRedirectWithSuccessMessage() throws Exception {
        UUID addressId = UUID.randomUUID();

        mockMvc.perform(post("/profile/addresses/" + addressId + "/delete")
                        .with(authenticatedAs(authenticatedUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/profile*"))
                .andExpect(flash().attribute("success", "Dirección eliminada correctamente"));

        verify(addressService).delete(addressId, authenticatedUser);
    }

    @Test
    void deleteAddressFromProfile_whenServiceThrowsException_shouldRedirectWithErrorMessage() throws Exception {
        UUID addressId = UUID.randomUUID();

        doThrow(new RuntimeException("fallo delete"))
                .when(addressService)
                .delete(addressId, authenticatedUser);

        mockMvc.perform(post("/user/profile/addresses/" + addressId + "/delete")
                        .with(authenticatedAs(authenticatedUser)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/profile*"))
                .andExpect(flash().attributeExists("error"));

        verify(addressService).delete(addressId, authenticatedUser);
    }
}