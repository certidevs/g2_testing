package com.ecommerce.service;

import com.ecommerce.dto.CategoryRequestDto;
import com.ecommerce.dto.CategoryResponseDto;
import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest
{

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void findAll_whenCategoriesExist_shouldReturnCategoryResponseDtoListWithoutChildren()
    {
        Category category = buildCategory(UUID.randomUUID(), "Portátiles", "portatiles", true);

        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<CategoryResponseDto> result = categoryService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(category.getId());
        assertThat(result.get(0).getName()).isEqualTo("Portátiles");
        assertThat(result.get(0).getSlug()).isEqualTo("portatiles");
        assertThat(result.get(0).getChildren()).isEmpty();

        verify(categoryRepository).findAll();
    }

    @Test
    void findRootCategories_whenRootCategoriesExist_shouldReturnRootCategoriesWithChildren()
    {
        Category root = buildCategory(UUID.randomUUID(), "Informática", "informatica", true);
        Category child = buildCategory(UUID.randomUUID(), "Portátiles", "portatiles", true);
        root.addChild(child);

        when(categoryRepository.findByParentIsNull()).thenReturn(List.of(root));

        List<CategoryResponseDto> result = categoryService.findRootCategories();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Informática");
        assertThat(result.get(0).getChildren()).hasSize(1);
        assertThat(result.get(0).getChildren().get(0).getName()).isEqualTo("Portátiles");
        assertThat(result.get(0).getChildren().get(0).getParentId()).isEqualTo(root.getId());

        verify(categoryRepository).findByParentIsNull();
    }

    @Test
    void findById_whenCategoryExists_shouldReturnCategoryWithChildren()
    {
        UUID id = UUID.randomUUID();
        Category root = buildCategory(id, "Informática", "informatica", true);
        Category child = buildCategory(UUID.randomUUID(), "Portátiles", "portatiles", true);
        root.addChild(child);

        when(categoryRepository.findByIdWithCategories(id)).thenReturn(Optional.of(root));

        CategoryResponseDto result = categoryService.findById(id);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Informática");
        assertThat(result.getChildren()).hasSize(1);
        assertThat(result.getChildren().get(0).getName()).isEqualTo("Portátiles");

        verify(categoryRepository).findByIdWithCategories(id);
    }

    @Test
    void findById_whenCategoryDoesNotExist_shouldThrowException()
    {
        UUID id = UUID.randomUUID();

        when(categoryRepository.findByIdWithCategories(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found");

        verify(categoryRepository).findByIdWithCategories(id);
    }

    @Test
    void create_whenRootCategoryIsValid_shouldCreateCategory()
    {
        CategoryRequestDto dto = buildRequestDto(
                "Informática",
                "informatica",
                "https://example.com/images/informatica.png",
                true,
                null
        );

        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(UUID.randomUUID());
            return category;
        });

        CategoryResponseDto result = categoryService.create(dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Informática");
        assertThat(result.getSlug()).isEqualTo("informatica");
        assertThat(result.getParentId()).isNull();
        assertThat(result.getActive()).isTrue();

        verify(categoryRepository).existsByParentIsNullAndSlug("informatica");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void create_whenRootCategorySlugAlreadyExists_shouldThrowException()
    {
        CategoryRequestDto dto = buildRequestDto(
                "Informática",
                "informatica",
                "https://example.com/images/informatica.png",
                true,
                null
        );

        when(categoryRepository.existsByParentIsNullAndSlug("informatica")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe una categoría raíz con ese slug");

        verify(categoryRepository).existsByParentIsNullAndSlug("informatica");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void create_whenChildCategoryIsValid_shouldCreateCategoryWithParent()
    {
        UUID parentId = UUID.randomUUID();
        Category parent = buildCategory(parentId, "Informática", "informatica", true);

        CategoryRequestDto dto = buildRequestDto(
                "Portátiles",
                "portatiles",
                "https://example.com/images/portatiles.png",
                true,
                parentId
        );

        when(categoryRepository.findByIdWithCategories(parentId)).thenReturn(Optional.of(parent));

        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(UUID.randomUUID());
            return category;
        });

        CategoryResponseDto result = categoryService.create(dto);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Portátiles");
        assertThat(result.getParentId()).isEqualTo(parentId);
        assertThat(result.getParentName()).isEqualTo("Informática");

        verify(categoryRepository).findByIdWithCategories(parentId);
        verify(categoryRepository).existsByParentIdAndSlug(parentId, "portatiles");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void create_whenChildSlugAlreadyExistsUnderSameParent_shouldThrowException()
    {
        UUID parentId = UUID.randomUUID();
        Category parent = buildCategory(parentId, "Informática", "informatica", true);

        CategoryRequestDto dto = buildRequestDto(
                "Portátiles",
                "portatiles",
                "https://example.com/images/portatiles.png",
                true,
                parentId
        );

        when(categoryRepository.findByIdWithCategories(parentId)).thenReturn(Optional.of(parent));
        when(categoryRepository.existsByParentIdAndSlug(parentId, "portatiles")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Ya existe una subcategoría con ese slug dentro de esta categoría");

        verify(categoryRepository).findByIdWithCategories(parentId);
        verify(categoryRepository).existsByParentIdAndSlug(parentId, "portatiles");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void create_whenParentDoesNotExist_shouldThrowException()
    {
        UUID parentId = UUID.randomUUID();

        CategoryRequestDto dto = buildRequestDto(
                "Portátiles",
                "portatiles",
                "https://example.com/images/portatiles.png",
                true,
                parentId
        );

        when(categoryRepository.findByIdWithCategories(parentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.create(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found");

        verify(categoryRepository).findByIdWithCategories(parentId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void update_whenCategoryExistsAndDataIsValid_shouldUpdateCategory()
    {
        UUID id = UUID.randomUUID();
        Category category = buildCategory(id, "Informática", "informatica", true);

        CategoryRequestDto dto = buildRequestDto(
                "Informática Gaming",
                "informatica-gaming",
                "https://example.com/images/informatica-gaming.png",
                false,
                null
        );

        when(categoryRepository.findByIdWithCategories(id)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponseDto result = categoryService.update(id, dto);

        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo("Informática Gaming");
        assertThat(result.getSlug()).isEqualTo("informatica-gaming");
        assertThat(result.getImageUrl()).isEqualTo("https://example.com/images/informatica-gaming.png");
        assertThat(result.getActive()).isFalse();
        assertThat(result.getParentId()).isNull();

        verify(categoryRepository).findByIdWithCategories(id);
        verify(categoryRepository).save(category);
    }

    @Test
    void update_whenActiveIsNull_shouldKeepPreviousActiveValue()
    {
        UUID id = UUID.randomUUID();
        Category category = buildCategory(id, "Informática", "informatica", true);

        CategoryRequestDto dto = buildRequestDto(
                "Informática",
                "informatica",
                "https://example.com/images/informatica.png",
                null,
                null
        );

        when(categoryRepository.findByIdWithCategories(id)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponseDto result = categoryService.update(id, dto);

        assertThat(result.getActive()).isTrue();

        verify(categoryRepository).findByIdWithCategories(id);
        verify(categoryRepository).save(category);
    }

    @Test
    void update_whenCategoryDoesNotExist_shouldThrowException()
    {
        UUID id = UUID.randomUUID();

        CategoryRequestDto dto = buildRequestDto(
                "Informática",
                "informatica",
                "https://example.com/images/informatica.png",
                true,
                null
        );

        when(categoryRepository.findByIdWithCategories(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.update(id, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found");

        verify(categoryRepository).findByIdWithCategories(id);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void update_whenCategoryIsAssignedAsItsOwnParent_shouldThrowException()
    {
        UUID id = UUID.randomUUID();
        Category category = buildCategory(id, "Informática", "informatica", true);

        CategoryRequestDto dto = buildRequestDto(
                "Informática",
                "informatica",
                "https://example.com/images/informatica.png",
                true,
                id
        );

        when(categoryRepository.findByIdWithCategories(id)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> categoryService.update(id, dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Una categoría no puede ser padre de sí misma");

        verify(categoryRepository).findByIdWithCategories(id);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void update_whenNewParentExists_shouldUpdateParent()
    {
        UUID id = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();

        Category category = buildCategory(id, "Portátiles", "portatiles", true);
        Category parent = buildCategory(parentId, "Informática", "informatica", true);

        CategoryRequestDto dto = buildRequestDto(
                "Portátiles",
                "portatiles",
                "https://example.com/images/portatiles.png",
                true,
                parentId
        );

        when(categoryRepository.findByIdWithCategories(id)).thenReturn(Optional.of(category));
        when(categoryRepository.findByIdWithCategories(parentId)).thenReturn(Optional.of(parent));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponseDto result = categoryService.update(id, dto);

        assertThat(result.getParentId()).isEqualTo(parentId);
        assertThat(result.getParentName()).isEqualTo("Informática");

        verify(categoryRepository).findByIdWithCategories(id);
        verify(categoryRepository).findByIdWithCategories(parentId);
        verify(categoryRepository).save(category);
    }

    @Test
    void delete_whenCategoryExists_shouldDeleteCategory()
    {
        UUID id = UUID.randomUUID();
        Category category = buildCategory(id, "Informática", "informatica", true);

        when(categoryRepository.findByIdWithCategories(id)).thenReturn(Optional.of(category));

        categoryService.delete(id);

        verify(categoryRepository).findByIdWithCategories(id);
        verify(categoryRepository).delete(category);
    }

    @Test
    void delete_whenCategoryDoesNotExist_shouldThrowException()
    {
        UUID id = UUID.randomUUID();

        when(categoryRepository.findByIdWithCategories(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.delete(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found");

        verify(categoryRepository).findByIdWithCategories(id);
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void getCategoryAndChildrenIds_whenCategoryHasChildren_shouldReturnAllIds()
    {
        UUID rootId = UUID.randomUUID();
        UUID childId = UUID.randomUUID();
        UUID grandChildId = UUID.randomUUID();

        CategoryResponseDto grandChild = CategoryResponseDto.builder()
                .id(grandChildId)
                .name("Gaming")
                .children(List.of())
                .build();

        CategoryResponseDto child = CategoryResponseDto.builder()
                .id(childId)
                .name("Portátiles")
                .children(List.of(grandChild))
                .build();

        CategoryResponseDto root = CategoryResponseDto.builder()
                .id(rootId)
                .name("Informática")
                .children(List.of(child))
                .build();

        List<UUID> result = categoryService.getCategoryAndChildrenIds(root);

        assertThat(result)
                .containsExactly(rootId, childId, grandChildId);
    }

    @Test
    void findActiveRootCategoriesForNavbar_shouldReturnSortedRootsAndOnlyActiveChildren()
    {
        Category zetaRoot = buildCategory(UUID.randomUUID(), "Zeta", "zeta", true);
        Category activeChild = buildCategory(UUID.randomUUID(), "Activo", "activo", true);
        Category inactiveChild = buildCategory(UUID.randomUUID(), "Inactivo", "inactivo", false);

        zetaRoot.addChild(activeChild);
        zetaRoot.addChild(inactiveChild);

        Category audioRoot = buildCategory(UUID.randomUUID(), "Audio", "audio", true);

        when(categoryRepository.findActiveRootCategoriesWithChildren())
                .thenReturn(List.of(zetaRoot, audioRoot));

        List<CategoryResponseDto> result = categoryService.findActiveRootCategoriesForNavbar();

        assertThat(result)
                .extracting(CategoryResponseDto::getName)
                .containsExactly("Audio", "Zeta");

        CategoryResponseDto zetaResult = result.get(1);

        assertThat(zetaResult.getChildren()).hasSize(1);
        assertThat(zetaResult.getChildren().get(0).getName()).isEqualTo("Activo");

        verify(categoryRepository).findActiveRootCategoriesWithChildren();
    }

    private Category buildCategory(UUID id, String name, String slug, Boolean active)
    {
        return Category.builder()
                .id(id)
                .name(name)
                .slug(slug)
                .description("Descripción de " + name)
                .imageUrl("https://example.com/images/" + slug + ".png")
                .active(active)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private CategoryRequestDto buildRequestDto(
            String name,
            String slug,
            String imageUrl,
            Boolean active,
            UUID parentId
    ) {
        return CategoryRequestDto.builder()
                .name(name)
                .slug(slug)
                .description("Descripción de " + name)
                .imageUrl(imageUrl)
                .active(active)
                .parentId(parentId)
                .build();
    }
}
