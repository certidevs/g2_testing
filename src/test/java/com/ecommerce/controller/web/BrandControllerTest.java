package com.ecommerce.controller.web;

import com.ecommerce.dto.BrandResponseDto;
import com.ecommerce.model.Brand;
import com.ecommerce.repository.BrandRepository;
import com.ecommerce.service.BrandService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void setUp()
    {
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
    void findAll() throws Exception
    {
        mockMvc.perform(get("/brands"))
                .andExpect(status().isOk())
                .andExpect(view().name("brands/brand-list"))
                .andExpect(model().attributeExists("brands"))
                .andExpect(model().attribute("brands", hasSize(2)))
                .andExpect(model().attribute("brands", hasItem(hasProperty(
                        "name", is("Nike")))))
                .andExpect(model().attribute("brands", hasItem(hasProperty(
                        "name", is("Adidas")))));
    }

    @Test
    void findById() throws Exception
    {
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
    void showCreateForm() throws Exception
    {
        mockMvc.perform(get("/brands/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("brands/brand-form"))
                .andExpect(model().attributeExists("brand"))
                .andExpect(model().attributeExists("formAction"))
                .andExpect(model().attribute("formAction", is("/brands")));
    }

    @Test
    void create() throws Exception
    {
        mockMvc.perform(post("/brands")
                        .param("name", "Puma")
                        .param("nif", "B11111111")
                        .param("country", "Germany")
                        .param("website", "https://puma.com")
                        .param("logo", "puma.png")
                        .param("active", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/brands*"))
                .andExpect(flash().attribute("susccessMessage", "Marca creada correctamente"));

        assertTrue(brandRepository.existsByName("Puma"));
        assertTrue(brandRepository.existsByNif("B11111111"));
    }

    @Test
    void createWithValidationErrors() throws Exception
    {
        mockMvc.perform(post("/brands")
                        .param("name", "")
                        .param("nif", "")
                        .param("country", "")
                        .param("website", "")
                        .param("logo", "")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("brands/brand-form"))
                .andExpect(model().attributeExists("brand"))
                .andExpect(model().attribute("formAction", is("/brands")));
    }

    @Test
    void showEditForm() throws Exception
    {
        mockMvc.perform(get("/brands/{id}/edit", nike.getId()))
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
                .andExpect(model().attribute("formActive", is("/brands/" + nike.getId() + "/edit")));
    }

    @Test
    void update() throws Exception
    {
        mockMvc.perform(post("/brands/{id}/edit", nike.getId())
                        .param("name", "Nike Updated")
                        .param("nif", "B12345678")
                        .param("country", "USA")
                        .param("website", "https://nikeupdated.com")
                        .param("logo", "nike-updated.png")
                        .param("active", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/brands*"))
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
    void updateWithValidationErrors() throws Exception
    {
        mockMvc.perform(post("/brands/{id}/edit", nike.getId())
                        .param("name", "")
                        .param("nif", "")
                        .param("country", "")
                        .param("website", "")
                        .param("logo", "")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("brands/brand-form"))
                .andExpect(model().attributeExists("brand"))
                .andExpect(model().attribute("brandId", is(nike.getId())))
                .andExpect(model().attribute("formAction", is("/brands/" + nike.getId() + "/edit")));
    }

    @Test
    void delete() throws Exception
    {
        mockMvc.perform(post("/brands/{id}/delete", nike.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/brands*"))
                .andExpect(flash().attribute("successMessage", "Marca eliminada correctamente"));

        assertFalse(brandRepository.existsById(nike.getId()));
    }
}