package com.ecommerce.controller.web;

import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CategoryControllerTest
{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findAll_whenUserIsAnonymous_shouldReturnCategoryListView() throws Exception
    {
        categoryRepository.save(buildRootCategory("Informática", "informatica"));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/category-list"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void findTree_whenUserIsAnonymous_shouldReturnCategoryTreeView() throws Exception
    {
        Category root = buildRootCategory("Informática", "informatica");
        root.addChild(buildRootCategory("Portátiles", "portatiles"));
        categoryRepository.save(root);

        mockMvc.perform(get("/categories/tree"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/category-tree"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void findById_whenUserIsAnonymousAndCategoryExists_shouldReturnCategoryDetailView() throws Exception
    {
        Category category = categoryRepository.save(buildRootCategory("Informática", "informatica"));

        mockMvc.perform(get("/categories/{id}", category.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/category-detail"))
                .andExpect(model().attributeExists("category"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    void showCreateForm_whenUserIsAnonymous_shouldRedirectToLogin() throws Exception
    {
        mockMvc.perform(get("/categories/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void showCreateForm_whenUserIsNormalUser_shouldReturnForbidden() throws Exception
    {
        mockMvc.perform(get("/categories/new")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void showCreateForm_whenUserIsAdmin_shouldReturnCategoryFormView() throws Exception
    {
        mockMvc.perform(get("/categories/new")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/category-form"))
                .andExpect(model().attributeExists("category"))
                .andExpect(model().attributeExists("parentCategories"))
                .andExpect(model().attribute("formAction", "/categories"));
    }

    @Test
    void create_whenUserIsAnonymous_shouldRedirectToLogin() throws Exception
    {
        mockMvc.perform(post("/categories")
                        .with(csrf())
                        .param("name", "Informática")
                        .param("slug", "informatica")
                        .param("description", "Categoría informática")
                        .param("imageUrl", "https://example.com/images/informatica.png")
                        .param("active", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertThat(categoryRepository.existsBySlug("informatica")).isFalse();
    }

    @Test
    void create_whenUserIsNormalUser_shouldReturnForbidden() throws Exception
    {
        mockMvc.perform(post("/categories")
                        .with(user("user").roles("USER"))
                        .with(csrf())
                        .param("name", "Informática")
                        .param("slug", "informatica")
                        .param("description", "Categoría informática")
                        .param("imageUrl", "https://example.com/images/informatica.png")
                        .param("active", "true"))
                .andExpect(status().isForbidden());

        assertThat(categoryRepository.existsBySlug("informatica")).isFalse();
    }

    @Test
    void create_whenUserIsAdminAndDataIsValid_shouldCreateCategoryAndRedirect() throws Exception
    {
        mockMvc.perform(post("/categories")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("name", "Informática")
                        .param("slug", "informatica")
                        .param("description", "Categoría informática")
                        .param("imageUrl", "https://example.com/images/informatica.png")
                        .param("active", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/categories*"))
                .andExpect(flash().attributeExists("successMessage"));

        Optional<Category> savedCategory = categoryRepository.findBySlug("informatica");

        assertThat(savedCategory).isPresent();
        assertThat(savedCategory.get().getName()).isEqualTo("Informática");
        assertThat(savedCategory.get().getParent()).isNull();
        assertThat(savedCategory.get().getActive()).isTrue();
    }

    @Test
    void create_whenUserIsAdminAndDataIsInvalid_shouldReturnFormView() throws Exception
    {
        mockMvc.perform(post("/categories")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("name", "")
                        .param("slug", "")
                        .param("description", "Categoría inválida")
                        .param("imageUrl", "invalid-url")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/category-form"))
                .andExpect(model().attributeExists("category"))
                .andExpect(model().attributeExists("parentCategories"))
                .andExpect(model().attribute("formAction", "/categories"))
                .andExpect(model().attributeHasFieldErrors("category", "name", "slug", "imageUrl"));

        assertThat(categoryRepository.count()).isZero();
    }

    @Test
    void create_whenUserIsAdminAndParentExists_shouldCreateChildCategoryAndRedirect() throws Exception
    {
        Category parent = categoryRepository.save(buildRootCategory("Informática", "informatica"));

        mockMvc.perform(post("/categories")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("name", "Portátiles")
                        .param("slug", "portatiles")
                        .param("description", "Categoría de portátiles")
                        .param("imageUrl", "https://example.com/images/portatiles.png")
                        .param("active", "true")
                        .param("parentId", parent.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/categories*"))
                .andExpect(flash().attributeExists("successMessage"));

        Optional<Category> child = categoryRepository.findBySlug("portatiles");

        assertThat(child).isPresent();
        assertThat(child.get().getParent()).isNotNull();
        assertThat(child.get().getParent().getId()).isEqualTo(parent.getId());
    }

    @Test
    void showEditForm_whenUserIsAnonymous_shouldRedirectToLogin() throws Exception
    {
        Category category = categoryRepository.save(buildRootCategory("Informática", "informatica"));

        mockMvc.perform(get("/categories/{id}/edit", category.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void showEditForm_whenUserIsNormalUser_shouldReturnForbidden() throws Exception
    {
        Category category = categoryRepository.save(buildRootCategory("Informática", "informatica"));

        mockMvc.perform(get("/categories/{id}/edit", category.getId())
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void showEditForm_whenUserIsAdminAndCategoryExists_shouldReturnCategoryFormView() throws Exception
    {
        Category category = categoryRepository.save(buildRootCategory("Informática", "informatica"));

        mockMvc.perform(get("/categories/{id}/edit", category.getId())
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/category-form"))
                .andExpect(model().attributeExists("category"))
                .andExpect(model().attributeExists("categoryId"))
                .andExpect(model().attributeExists("parentCategories"))
                .andExpect(model().attribute("formAction", "/categories/" + category.getId() + "/edit"));
    }

    @Test
    void update_whenUserIsAnonymous_shouldRedirectToLogin() throws Exception
    {
        Category category = categoryRepository.save(buildRootCategory("Informática", "informatica"));

        mockMvc.perform(post("/categories/{id}/edit", category.getId())
                        .with(csrf())
                        .param("name", "Informática Gaming")
                        .param("slug", "informatica-gaming")
                        .param("description", "Categoría actualizada")
                        .param("imageUrl", "https://example.com/images/informatica-gaming.png")
                        .param("active", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void update_whenUserIsNormalUser_shouldReturnForbidden() throws Exception
    {
        Category category = categoryRepository.save(buildRootCategory("Informática", "informatica"));

        mockMvc.perform(post("/categories/{id}/edit", category.getId())
                        .with(user("user").roles("USER"))
                        .with(csrf())
                        .param("name", "Informática Gaming")
                        .param("slug", "informatica-gaming")
                        .param("description", "Categoría actualizada")
                        .param("imageUrl", "https://example.com/images/informatica-gaming.png")
                        .param("active", "true"))
                .andExpect(status().isForbidden());
    }

    @Test
    void update_whenUserIsAdminAndDataIsValid_shouldUpdateCategoryAndRedirect() throws Exception
    {
        Category category = categoryRepository.save(buildRootCategory("Informática", "informatica"));

        mockMvc.perform(post("/categories/{id}/edit", category.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("name", "Informática Gaming")
                        .param("slug", "informatica-gaming")
                        .param("description", "Categoría actualizada")
                        .param("imageUrl", "https://example.com/images/informatica-gaming.png")
                        .param("active", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/categories*"))
                .andExpect(flash().attributeExists("successMessage"));

        Category updatedCategory = categoryRepository.findById(category.getId()).orElseThrow();

        assertThat(updatedCategory.getName()).isEqualTo("Informática Gaming");
        assertThat(updatedCategory.getSlug()).isEqualTo("informatica-gaming");
        assertThat(updatedCategory.getImageUrl()).isEqualTo("https://example.com/images/informatica-gaming.png");
        assertThat(updatedCategory.getActive()).isFalse();
    }

    @Test
    void update_whenUserIsAdminAndDataIsInvalid_shouldReturnFormView() throws Exception
    {
        Category category = categoryRepository.save(buildRootCategory("Informática", "informatica"));

        mockMvc.perform(post("/categories/{id}/edit", category.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("name", "")
                        .param("slug", "")
                        .param("description", "Categoría inválida")
                        .param("imageUrl", "invalid-url")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/category-form"))
                .andExpect(model().attributeExists("category"))
                .andExpect(model().attributeExists("categoryId"))
                .andExpect(model().attributeExists("parentCategories"))
                .andExpect(model().attribute("formAction", "/categories/" + category.getId() + "/edit"))
                .andExpect(model().attributeHasFieldErrors("category", "name", "slug", "imageUrl"));
    }

    @Test
    void update_whenUserIsAdminAndCategoryIsAssignedAsItsOwnParent_shouldReturnFormViewWithGlobalError() throws Exception
    {
        Category category = categoryRepository.save(buildRootCategory("Informática", "informatica"));

        mockMvc.perform(post("/categories/{id}/edit", category.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .param("name", "Informática")
                        .param("slug", "informatica")
                        .param("description", "Categoría informática")
                        .param("imageUrl", "https://example.com/images/informatica.png")
                        .param("active", "true")
                        .param("parentId", category.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("categories/category-form"))
                .andExpect(model().attributeExists("category"))
                .andExpect(model().attributeExists("categoryId"))
                .andExpect(model().attributeExists("parentCategories"))
                .andExpect(model().hasErrors());
    }

    @Test
    void delete_whenUserIsAnonymous_shouldRedirectToLogin() throws Exception
    {
        Category category = categoryRepository.save(buildRootCategory("Informática", "informatica"));

        mockMvc.perform(post("/categories/{id}/delete", category.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertThat(categoryRepository.findById(category.getId())).isPresent();
    }

    @Test
    void delete_whenUserIsNormalUser_shouldReturnForbidden() throws Exception
    {
        Category category = categoryRepository.save(buildRootCategory("Informática", "informatica"));

        mockMvc.perform(post("/categories/{id}/delete", category.getId())
                        .with(user("user").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        assertThat(categoryRepository.findById(category.getId())).isPresent();
    }

    @Test
    void delete_whenUserIsAdminAndCategoryExists_shouldDeleteCategoryAndRedirect() throws Exception
    {
        Category category = categoryRepository.save(buildRootCategory("Informática", "informatica"));

        mockMvc.perform(post("/categories/{id}/delete", category.getId())
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/categories*"))
                .andExpect(flash().attributeExists("successMessage"));

        assertThat(categoryRepository.findById(category.getId())).isEmpty();
    }

    private Category buildRootCategory(String name, String slug)
    {
        return Category.builder()
                .name(name)
                .slug(slug)
                .description("Descripción de " + name)
                .imageUrl("https://example.com/images/" + slug + ".png")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
}