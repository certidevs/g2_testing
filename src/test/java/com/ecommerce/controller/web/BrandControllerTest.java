package com.ecommerce.controller.web;

import com.ecommerce.model.Brand;
import com.ecommerce.repository.BrandRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BrandRepository brandRepository;

    private Brand nike;
    private Brand adidas;

    @BeforeEach
    void setUp() {
        brandRepository.deleteAllInBatch();
        brandRepository.flush();

        nike = brandRepository.saveAndFlush(
                Brand.builder()
                        .name("Nike")
                        .nif("B12345678")
                        .country("USA")
                        .website("https://nike.com")
                        .logo("nike.png")
                        .active(true)
                        .build()
        );

        adidas = brandRepository.saveAndFlush(
                Brand.builder()
                        .name("Adidas")
                        .nif("B87654321")
                        .country("Germany")
                        .website("https://adidas.com")
                        .logo("adidas.png")
                        .active(true)
                        .build()
        );
    }

    @Test
    void findAll_shouldReturnBrandListView() throws Exception {
        mockMvc.perform(get("/brands"))
                .andExpect(status().isOk())
                .andExpect(view().name("brands/brand-list"))
                .andExpect(model().attributeExists("brands"))
                .andExpect(model().attribute("brands", hasSize(2)))
                .andExpect(model().attribute("brands", hasItem(hasProperty("name", is("Nike")))))
                .andExpect(model().attribute("brands", hasItem(hasProperty("name", is("Adidas")))));
    }

    @Test
    void findById_shouldReturnBrandDetailView() throws Exception {
        mockMvc.perform(get("/brands/{id}", nike.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("brands/brand-detail"))
                .andExpect(model().attributeExists("brand"))
                .andExpect(model().attribute("brand", hasProperty("id", is(nike.getId()))))
                .andExpect(model().attribute("brand", hasProperty("name", is("Nike"))))
                .andExpect(model().attribute("brand", hasProperty("nif", is("B12345678"))))
                .andExpect(model().attribute("brand", hasProperty("country", is("USA"))))
                .andExpect(model().attribute("brand", hasProperty("website", is("https://nike.com"))))
                .andExpect(model().attribute("brand", hasProperty("logo", is("nike.png"))))
                .andExpect(model().attribute("brand", hasProperty("active", is(true))));
    }

    @Test
    void showCreateForm_whenUserIsAdmin_shouldReturnBrandForm() throws Exception {
        mockMvc.perform(get("/brands/new")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("brands/brand-form"))
                .andExpect(model().attributeExists("brand"))
                .andExpect(model().attributeExists("formAction"))
                .andExpect(model().attribute("formAction", is("/brands")));
    }

    @Test
    void showCreateForm_whenUserIsAnonymous_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/brands/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void create_whenUserIsAdminAndDataIsValid_shouldCreateBrandAndRedirect() throws Exception {
        mockMvc.perform(post("/brands")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("name", "Puma")
                        .param("nif", "B11111111")
                        .param("country", "Germany")
                        .param("website", "https://puma.com")
                        .param("logo", "puma.png")
                        .param("active", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/brands"))
                .andExpect(flash().attribute("successMessage", "Marca creada correctamente"));

        assertTrue(brandRepository.existsByName("Puma"));
        assertTrue(brandRepository.existsByNif("B11111111"));
    }

    @Test
    void create_whenUserIsNotAdmin_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/brands")
                        .with(user("user").roles("USER"))
                        .with(csrf())
                        .param("name", "Puma")
                        .param("nif", "B11111111")
                        .param("country", "Germany")
                        .param("website", "https://puma.com")
                        .param("logo", "puma.png")
                        .param("active", "true"))
                .andExpect(status().isForbidden());

        assertFalse(brandRepository.existsByName("Puma"));
    }

    @Test
    void create_whenUserIsAnonymous_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(post("/brands")
                        .with(csrf())
                        .param("name", "Puma")
                        .param("nif", "B11111111")
                        .param("country", "Germany")
                        .param("website", "https://puma.com")
                        .param("logo", "puma.png")
                        .param("active", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        assertFalse(brandRepository.existsByName("Puma"));
    }

    @Test
    void create_whenValidationErrors_shouldReturnBrandForm() throws Exception {
        mockMvc.perform(post("/brands")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("name", "")
                        .param("nif", "")
                        .param("country", "")
                        .param("website", "")
                        .param("logo", "")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("brands/brand-form"))
                .andExpect(model().attributeExists("brand"))
                .andExpect(model().attributeHasFieldErrors("brand", "name", "nif"))
                .andExpect(model().attribute("formAction", is("/brands")));
    }

    @Test
    void create_whenDuplicatedName_shouldReturnBrandFormWithGlobalError() throws Exception {
        mockMvc.perform(post("/brands")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("name", "Nike")
                        .param("nif", "B99999999")
                        .param("country", "USA")
                        .param("website", "https://nike2.com")
                        .param("logo", "nike2.png")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("brands/brand-form"))
                .andExpect(model().attributeExists("brand"))
                .andExpect(model().attribute("formAction", is("/brands")))
                .andExpect(model().hasErrors());

        assertFalse(brandRepository.existsByNif("B99999999"));
    }

    @Test
    void showEditForm_whenUserIsAdmin_shouldReturnBrandForm() throws Exception {
        mockMvc.perform(get("/brands/{id}/edit", nike.getId())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("brands/brand-form"))
                .andExpect(model().attributeExists("brand"))
                .andExpect(model().attribute("brand", hasProperty("name", is("Nike"))))
                .andExpect(model().attribute("brand", hasProperty("nif", is("B12345678"))))
                .andExpect(model().attribute("brand", hasProperty("country", is("USA"))))
                .andExpect(model().attribute("brand", hasProperty("website", is("https://nike.com"))))
                .andExpect(model().attribute("brand", hasProperty("logo", is("nike.png"))))
                .andExpect(model().attribute("brand", hasProperty("active", is(true))))
                .andExpect(model().attribute("brandId", is(nike.getId())))
                .andExpect(model().attribute("formAction", is("/brands/" + nike.getId() + "/edit")));
    }

    @Test
    void showEditForm_whenUserIsNotAdmin_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/brands/{id}/edit", nike.getId())
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void showEditForm_whenUserIsAnonymous_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/brands/{id}/edit", nike.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void update_whenUserIsAdminAndDataIsValid_shouldUpdateBrandAndRedirect() throws Exception {
        mockMvc.perform(post("/brands/{id}/edit", nike.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("name", "Nike Updated")
                        .param("nif", "B12345678")
                        .param("country", "USA")
                        .param("website", "https://nikeupdated.com")
                        .param("logo", "nike-updated.png")
                        .param("active", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/brands"))
                .andExpect(flash().attribute("successMessage", "Marca actualizada correctamente"));

        Brand updatedBrand = brandRepository.findById(nike.getId())
                .orElseThrow();

        assertEquals("Nike Updated", updatedBrand.getName());
        assertEquals("B12345678", updatedBrand.getNif());
        assertEquals("USA", updatedBrand.getCountry());
        assertEquals("https://nikeupdated.com", updatedBrand.getWebsite());
        assertEquals("nike-updated.png", updatedBrand.getLogo());
        assertFalse(updatedBrand.getActive());
    }

    @Test
    void update_whenUserIsNotAdmin_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/brands/{id}/edit", nike.getId())
                        .with(user("user").roles("USER"))
                        .with(csrf())
                        .param("name", "Nike Updated")
                        .param("nif", "B12345678")
                        .param("country", "USA")
                        .param("website", "https://nikeupdated.com")
                        .param("logo", "nike-updated.png")
                        .param("active", "false"))
                .andExpect(status().isForbidden());

        Brand originalBrand = brandRepository.findById(nike.getId())
                .orElseThrow();

        assertEquals("Nike", originalBrand.getName());
    }

    @Test
    void update_whenBrandDoesNotExist_shouldReturnFormWithGlobalError() throws Exception {
        UUID fakeId = UUID.randomUUID();

        mockMvc.perform(post("/brands/{id}/edit", fakeId)
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("name", "Nike Updated")
                        .param("nif", "B12345678")
                        .param("country", "USA")
                        .param("website", "https://nikeupdated.com")
                        .param("logo", "nike-updated.png")
                        .param("active", "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("brands/brand-form"))
                .andExpect(model().attributeExists("brand"))
                .andExpect(model().attribute("brandId", is(fakeId)))
                .andExpect(model().attribute("formAction", is("/brands/" + fakeId + "/edit")))
                .andExpect(model().hasErrors());

        Brand originalBrand = brandRepository.findById(nike.getId())
                .orElseThrow();

        assertEquals("Nike", originalBrand.getName());
        assertEquals("B12345678", originalBrand.getNif());
        assertEquals("USA", originalBrand.getCountry());
        assertEquals("https://nike.com", originalBrand.getWebsite());
        assertEquals("nike.png", originalBrand.getLogo());
        assertTrue(originalBrand.getActive());
    }

    @Test
    void update_whenValidationErrors_shouldReturnBrandForm() throws Exception {
        mockMvc.perform(post("/brands/{id}/edit", nike.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("name", "")
                        .param("nif", "")
                        .param("country", "")
                        .param("website", "")
                        .param("logo", "")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("brands/brand-form"))
                .andExpect(model().attributeExists("brand"))
                .andExpect(model().attributeHasFieldErrors("brand", "name", "nif"))
                .andExpect(model().attribute("brandId", is(nike.getId())))
                .andExpect(model().attribute("formAction", is("/brands/" + nike.getId() + "/edit")));
    }

    @Test
    void delete_whenUserIsAdmin_shouldDeleteBrandAndRedirect() throws Exception {
        mockMvc.perform(post("/brands/{id}/delete", nike.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/brands"))
                .andExpect(flash().attribute("successMessage", "Marca eliminada correctamente"));

        assertFalse(brandRepository.existsById(nike.getId()));
    }

    @Test
    void delete_whenUserIsNotAdmin_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/brands/{id}/delete", nike.getId())
                        .with(user("user").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        assertTrue(brandRepository.existsById(nike.getId()));
    }

    @Test
    void delete_whenUserIsAnonymous_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(post("/brands/{id}/delete", nike.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        assertTrue(brandRepository.existsById(nike.getId()));
    }
}