package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.web.csrf.DefaultCsrfToken;


import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Activa MockMvc para testing de controller
@Transactional

class ProductControlerTest {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    private MockMvc mockMvc;
    Product productToDeactivate;
    Product productToActivate;


    @BeforeEach
    void setUp() {
        productToDeactivate = new Product();
        productToDeactivate.setTitle("Producto a desactivar");
        productToDeactivate.setAvailable(true);
        productToDeactivate.setPrice(10.0);
        productToDeactivate = productRepository.save(productToDeactivate);

        productToActivate = new Product();
        productToActivate.setTitle("Producto a activar");
        productToActivate.setAvailable(false);
        productToActivate.setPrice(20.0);
        productToActivate = productRepository.save(productToActivate);
    }

    @Test
    void productsDetail() throws Exception {
        UUID id = productToDeactivate.getId();
        mockMvc.perform(get("/products/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product-detail"));
    }

    @Test
    void searchProducts() throws Exception {
        mockMvc.perform(get("/products/search").param("query", "Producto"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product-list"));
    }

    @Test
    void deactivateProduct() throws Exception {
        assertTrue(productToDeactivate.isAvailable());

        UUID id = productToDeactivate.getId();

        mockMvc.perform(get("/products/deactivate/" + id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/products/list*"));

        // traer producto de base de datos y comprobar que available es false
        Product productDB = productRepository.findById(id).orElseThrow();
        assertFalse(productDB.isAvailable());

    }

    @Test
    void activateProduct() throws Exception {
        assertFalse(productToActivate.isAvailable());

        UUID id = productToActivate.getId();

        mockMvc.perform(get("/products/activate/" + id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/products/list*"));

        // traer producto de base de datos y comprobar que available es true
        Product productDB = productRepository.findById(id).orElseThrow();
        assertTrue(productDB.isAvailable());

    }

    @Test
    void navigateToForm() throws Exception {
        mockMvc.perform(get("/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product-form"));
    }

    @Test
    void createProduct() throws Exception {
        //count products
        long before = productRepository.count();
        //mockmvc perform para enviar producto nuevo a controller
        mockMvc.perform(post("/products")
                .param("title", "Producto de prueba")
                .param("shortDescription", "Descripción corta de prueba")
                .param("longDescription", "Descripción larga de prueba")
                .param("price", "9.99")
                .param("stockStatus", "STOCK")
        ).andExpect(status().is3xxRedirection())
         .andExpect(redirectedUrlPattern("/admin/products/list*"));


        //count products
        long after = productRepository.count();
        assertEquals(before + 1, after);
    }

    @Test
    void products() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product-list"));
    }

    @Test
    void listProducts_whenCategoryIsBlank_shouldListAvailableProducts() throws Exception {
        mockMvc.perform(get("/products").param("category", "   "))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("discountedProducts"))
                .andExpect(model().attribute("saludo", "NUESTRA TIENDA"));
    }

    @Test
    void listProducts_whenCategoryIsPresent_shouldFilterBySubcategorySlug() throws Exception {
        mockMvc.perform(get("/products").param("category", "informatica"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("discountedProducts"))
                .andExpect(model().attribute("saludo", "NUESTRA TIENDA"));
    }

    @Test
    void productsDetail_whenProductDoesNotExist_shouldRedirectToProducts() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/products/" + id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/products*"));
    }

    @Test
    void productsDetail_whenCsrfTokenExists_shouldStillShowDetail() throws Exception {
        UUID id = productToDeactivate.getId();

        DefaultCsrfToken csrfToken = new DefaultCsrfToken(
                "X-CSRF-TOKEN",
                "_csrf",
                "token-test"
        );

        mockMvc.perform(get("/products/" + id)
                        .requestAttr("_csrf", csrfToken))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product-detail"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attributeExists("reviews"));
    }

    @Test
    void listProductsByCategory_shouldReturnProductList() throws Exception {
        UUID categoryId = UUID.randomUUID();

        mockMvc.perform(get("/products/categories/" + categoryId))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void listAdminProducts_shouldReturnAdminProductList() throws Exception {
        mockMvc.perform(get("/admin/products/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product-admin-list"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    void updateProductDiscount_whenProductExistsAndDiscountGreaterThan99_shouldNormalizeTo99() throws Exception {
        UUID id = productToDeactivate.getId();

        mockMvc.perform(post("/admin/products/update-discount")
                        .param("productId", id.toString())
                        .param("discountPercentage", "150"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/products/list*"));

        Product productDB = productRepository.findById(id).orElseThrow();

        assertEquals(99, productDB.getDiscountPercentage());
    }

    @Test
    void updateProductDiscount_whenProductDoesNotExist_shouldOnlyRedirect() throws Exception {
        long before = productRepository.count();

        mockMvc.perform(post("/admin/products/update-discount")
                        .param("productId", UUID.randomUUID().toString())
                        .param("discountPercentage", "50"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/products/list*"));

        long after = productRepository.count();

        assertEquals(before, after);
    }

    @Test
    void deactivateProduct_whenProductDoesNotExist_shouldOnlyRedirect() throws Exception {
        mockMvc.perform(get("/products/deactivate/" + UUID.randomUUID()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/products/list*"));
    }

    @Test
    void activateProduct_whenProductDoesNotExist_shouldOnlyRedirect() throws Exception {
        mockMvc.perform(get("/products/activate/" + UUID.randomUUID()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/products/list*"));
    }

    @Test
    void navigateToFormAlias_shouldReturnProductForm() throws Exception {
        mockMvc.perform(get("/products/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product-form"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attributeExists("allStockStatuses"))
                .andExpect(model().attributeExists("brands"))
                .andExpect(model().attributeExists("subcategories"));
    }

    @Test
    void navigateToEditForm_whenProductExists_shouldReturnProductForm() throws Exception {
        UUID id = productToDeactivate.getId();

        mockMvc.perform(get("/products/edit/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("products/product-form"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attributeExists("allStockStatuses"))
                .andExpect(model().attributeExists("brands"))
                .andExpect(model().attributeExists("subcategories"));
    }

    @Test
    void navigateToEditForm_whenProductDoesNotExist_shouldRedirectToAdminList() throws Exception {
        mockMvc.perform(get("/products/edit/" + UUID.randomUUID()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/products/list*"));
    }

    @Test
    void createProduct_whenDiscountIsNegative_shouldNormalizeToZero() throws Exception {
        long before = productRepository.count();

        mockMvc.perform(post("/products")
                        .param("title", "Producto con descuento negativo")
                        .param("shortDescription", "Descripción corta")
                        .param("longDescription", "Descripción larga")
                        .param("price", "25.50")
                        .param("stock", "5")
                        .param("imageUrl", "imagen-test.jpg")
                        .param("available", "true")
                        .param("discountPercentage", "-20")
                        .param("stockStatus", "STOCK"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/products/list*"));

        long after = productRepository.count();

        assertEquals(before + 1, after);

        Product productDB = productRepository.findAll()
                .stream()
                .filter(product -> "Producto con descuento negativo".equals(product.getTitle()))
                .findFirst()
                .orElseThrow();

        assertEquals(0, productDB.getDiscountPercentage());
    }

    @Test
    void createProduct_whenProductHasExistingId_shouldUpdateExistingProduct() throws Exception {
        UUID id = productToDeactivate.getId();

        mockMvc.perform(post("/products")
                        .param("id", id.toString())
                        .param("title", "Producto actualizado")
                        .param("shortDescription", "Nueva descripción corta")
                        .param("longDescription", "Nueva descripción larga")
                        .param("price", "99.99")
                        .param("stock", "12")
                        .param("imageUrl", "nueva-imagen.jpg")
                        .param("available", "false")
                        .param("discountPercentage", "120")
                        .param("stockStatus", "STOCK"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/products/list*"));

        Product productDB = productRepository.findById(id).orElseThrow();

        assertEquals("Producto actualizado", productDB.getTitle());
        assertEquals(99.99, productDB.getPrice());
        assertFalse(productDB.isAvailable());
        assertEquals("Nueva descripción corta", productDB.getShortDescription());
        assertEquals("Nueva descripción larga", productDB.getLongDescription());
        assertEquals(12, productDB.getStock());
        assertEquals("nueva-imagen.jpg", productDB.getImageUrl());
        assertEquals(99, productDB.getDiscountPercentage());
    }

    @Test
    void createProduct_whenProductHasNonExistingId_shouldNotCreateNewProduct() throws Exception {
        long before = productRepository.count();

        mockMvc.perform(post("/products")
                        .param("id", UUID.randomUUID().toString())
                        .param("title", "Producto fantasma")
                        .param("shortDescription", "No debería guardarse")
                        .param("longDescription", "No debería guardarse")
                        .param("price", "55.55")
                        .param("stock", "2")
                        .param("available", "true")
                        .param("discountPercentage", "30")
                        .param("stockStatus", "STOCK"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/products/list*"));

        long after = productRepository.count();

        assertEquals(before, after);
    }
}