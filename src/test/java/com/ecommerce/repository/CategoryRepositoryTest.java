package com.ecommerce.repository;

import com.ecommerce.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CategoryRepositoryTest
{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void save_whenCategoryIsValid_shouldPersistCategoryWithGeneratedIdAndCreatedAt()
    {
        Category category = buildRootCategory("Portátiles", "portatiles");

        Category savedCategory = categoryRepository.save(category);

        assertThat(savedCategory.getId()).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo("Portátiles");
        assertThat(savedCategory.getSlug()).isEqualTo("portatiles");
        assertThat(savedCategory.getCreatedAt()).isNotNull();
        assertThat(savedCategory.getActive()).isTrue();
    }

    @Test
    void save_whenActiveIsNull_shouldSetActiveTrueByPrePersist()
    {
        Category category = buildRootCategory("Sobremesa", "sobremesa");
        category.setActive(null);

        Category savedCategory = categoryRepository.saveAndFlush(category);

        assertThat(savedCategory.getActive()).isTrue();
        assertThat(savedCategory.getCreatedAt()).isNotNull();
    }

    @Test
    void save_whenCategoryIsUpdated_shouldSetUpdateAtByPreUpdate()
    {
        Category category = persistCategory(buildRootCategory("Monitores", "monitores"));

        category.setName("Monitores Gaming");
        Category updatedCategory = categoryRepository.saveAndFlush(category);

        assertThat(updatedCategory.getUpdateAt()).isNotNull();
        assertThat(updatedCategory.getName()).isEqualTo("Monitores Gaming");
    }

    @Test
    void findBySlug_whenSlugExists_shouldReturnCategory()
    {
        persistCategory(buildRootCategory("Teclados", "teclados"));

        Optional<Category> result = categoryRepository.findBySlug("teclados");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Teclados");
    }

    @Test
    void findBySlug_whenSlugDoesNotExist_shouldReturnEmpty()
    {
        Optional<Category> result = categoryRepository.findBySlug("no-existe");

        assertThat(result).isEmpty();
    }

    @Test
    void existsBySlug_whenSlugExists_shouldReturnTrue()
    {
        persistCategory(buildRootCategory("Ratones", "ratones"));

        boolean exists = categoryRepository.existsBySlug("ratones");

        assertThat(exists).isTrue();
    }

    @Test
    void existsBySlug_whenSlugDoesNotExist_shouldReturnFalse()
    {
        boolean exists = categoryRepository.existsBySlug("no-existe");

        assertThat(exists).isFalse();
    }

    @Test
    void existsByName_whenNameExists_shouldReturnTrue()
    {
        persistCategory(buildRootCategory("Componentes", "componentes"));

        boolean exists = categoryRepository.existsByName("Componentes");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_whenNameDoesNotExist_shouldReturnFalse()
    {
        boolean exists = categoryRepository.existsByName("No existe");

        assertThat(exists).isFalse();
    }

    @Test
    void findByParentIsNull_shouldReturnOnlyRootCategories()
    {
        Category root = persistCategory(buildRootCategory("Informática", "informatica"));
        Category child = buildChildCategory("Portátiles", "portatiles", root);
        persistCategory(child);

        List<Category> result = categoryRepository.findByParentIsNull();

        assertThat(result)
                .extracting(Category::getName)
                .contains("Informática")
                .doesNotContain("Portátiles");
    }

    @Test
    void findByParentId_whenParentExists_shouldReturnChildrenOfParent()
    {
        Category parent = persistCategory(buildRootCategory("Informática", "informatica"));
        persistCategory(buildChildCategory("Portátiles", "portatiles", parent));
        persistCategory(buildChildCategory("Sobremesa", "sobremesa", parent));
        persistCategory(buildRootCategory("Hogar", "hogar"));

        List<Category> result = categoryRepository.findByParentId(parent.getId());

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(Category::getName)
                .containsExactlyInAnyOrder("Portátiles", "Sobremesa");
    }

    @Test
    void existsByParentIdAndSlug_whenChildSlugExistsUnderParent_shouldReturnTrue()
    {
        Category parent = persistCategory(buildRootCategory("Informática", "informatica"));
        persistCategory(buildChildCategory("Portátiles", "portatiles", parent));

        boolean exists = categoryRepository.existsByParentIdAndSlug(parent.getId(), "portatiles");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByParentIdAndSlug_whenChildSlugDoesNotExistUnderParent_shouldReturnFalse()
    {
        Category parent = persistCategory(buildRootCategory("Informática", "informatica"));

        boolean exists = categoryRepository.existsByParentIdAndSlug(parent.getId(), "portatiles");

        assertThat(exists).isFalse();
    }

    @Test
    void existsByParentIsNullAndSlug_whenRootSlugExists_shouldReturnTrue()
    {
        persistCategory(buildRootCategory("Informática", "informatica"));

        boolean exists = categoryRepository.existsByParentIsNullAndSlug("informatica");

        assertThat(exists).isTrue();
    }

    @Test
    void existsByParentIsNullAndSlug_whenSlugExistsOnlyAsChild_shouldReturnFalse()
    {
        Category parent = persistCategory(buildRootCategory("Informática", "informatica"));
        persistCategory(buildChildCategory("Portátiles", "portatiles", parent));

        boolean exists = categoryRepository.existsByParentIsNullAndSlug("portatiles");

        assertThat(exists).isFalse();
    }

    @Test
    void findAllRootWithChildren_shouldReturnRootCategoriesWithChildren()
    {
        Category parent = buildRootCategory("Informática", "informatica");
        parent.addChild(buildRootCategory("Portátiles", "portatiles"));
        parent.addChild(buildRootCategory("Sobremesa", "sobremesa"));

        entityManager.persist(parent);
        entityManager.flush();
        entityManager.clear();

        List<Category> result = categoryRepository.findAllRootWithChildren();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Informática");
        assertThat(result.getFirst().getChildren()).hasSize(2);
    }

    @Test
    void findActiveRootCategoriesWithChildren_shouldReturnOnlyActiveRootCategories()
    {
        Category activeRoot = buildRootCategory("Informática", "informatica");
        activeRoot.setActive(true);

        Category inactiveRoot = buildRootCategory("Archivadas", "archivadas");
        inactiveRoot.setActive(false);

        persistCategory(activeRoot);
        persistCategory(inactiveRoot);

        List<Category> result = categoryRepository.findActiveRootCategoriesWithChildren();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Informática");
        assertThat(result.getFirst().getActive()).isTrue();
    }

    @Test
    void findByIdWithCategories_whenCategoryExists_shouldReturnCategory()
    {
        Category parent = buildRootCategory("Informática", "informatica");
        parent.addChild(buildRootCategory("Portátiles", "portatiles"));

        entityManager.persist(parent);
        entityManager.flush();
        entityManager.clear();

        Optional<Category> result = categoryRepository.findByIdWithCategories(parent.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Informática");
        assertThat(result.get().getChildren()).hasSize(1);
    }

    @Test
    void findByIdWithCategories_whenCategoryDoesNotExist_shouldReturnEmpty()
    {
        Optional<Category> result = categoryRepository.findByIdWithCategories(java.util.UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    private Category persistCategory(Category category)
    {
        Category savedCategory = entityManager.persistAndFlush(category);
        entityManager.clear();
        return savedCategory;
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

    private Category buildChildCategory(String name, String slug, Category parent)
    {
        return Category.builder()
                .name(name)
                .slug(slug)
                .description("Descripción de " + name)
                .imageUrl("https://example.com/images/" + slug + ".png")
                .active(true)
                .createdAt(LocalDateTime.now())
                .parent(parent)
                .build();
    }
}