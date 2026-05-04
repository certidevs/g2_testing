package com.ecommerce.service;

import com.ecommerce.dto.CategoryRequestDto;
import com.ecommerce.dto.CategoryResponseDto;
import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService
{
    // Repositorio para operaciones CRUD sobre Category (Inyectado por Spring)
    private final CategoryRepository categoryRepository;

    /**
     * Obtiene todas las categorías sin cargar sus hijos.
     * Se usa para listados simples donde no se necesita el árbol completo.
     *
     * @return lista de CategoryResponseDto sin información de children
     */
    public List<CategoryResponseDto> findAll()
    {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponseDtoWithoutChildren)// convierte cada Category a CategoryResponseDto
                .toList();
    }

    /**
     * Obtiene únicamente las categorías raíz (parent == null)
     * y construye el árbol completo de subcategorías.
     *
     * @return lista de categorías raíz con todos sus hijos anidados
     */
    public List<CategoryResponseDto> findRootCategories()
    {
        return categoryRepository.findByParentIsNull()
                .stream()
                .map(this::toResponseDtoWithChildren)
                .toList();
    }

    /**
     * Busca una categoría por su ID y devuelve su estructura completa.
     *
     * @param id UUID de la categoría
     * @return CategoryResponseDto con hijos incluidos
     * @throws RuntimeException si la categoría no existe
     */
    public CategoryResponseDto findById(UUID id)
    {
        Category category = findCategoryEntityById(id);
        return toResponseDtoWithChildren(category);
    }

    /**
     * Crea una nueva categoría validando:
     * - Si tiene parentId, el padre debe existir.
     * - No puede existir otra categoría con el mismo slug bajo el mismo padre.
     * - Si es categoría raíz, no puede existir otra raíz con el mismo slug.
     *
     * @param dto datos de creación
     * @return categoría creada convertida a DTO con hijos
     */
    public CategoryResponseDto create(CategoryRequestDto dto) {
        Category parent = null;

        if (dto.getParentId() != null)
        {
            parent = findCategoryEntityById(dto.getParentId());

            // Validación: slug único dentro del mismo padre
            if (categoryRepository.existsByParentIdAndSlug(dto.getParentId(), dto.getSlug()))
            {
                throw new RuntimeException("Ya existe una subCategoria con ese slug dentro de esta categoria");
            }
            else
            {
                // Validación: slug único entre categorías raíz
                if (categoryRepository.existsByParentIsNullAndSlug(dto.getSlug()))
                {
                    throw new RuntimeException("Ya existe una categoria raiz con ese slug");
                }
            }
        }
        Category category = Category.builder()
                .name(dto.getName())
                .slug(dto.getSlug())
                .description(dto.getDescription())
                .active(dto.getActive())
                .parent(parent)
                .build();

        return toResponseDtoWithChildren(categoryRepository.save(category));
    }

    /**
     * Actualiza una categoría existente validando:
     * - La categoría debe existir.
     * - No puede asignarse como padre a sí misma.
     * - Si se asigna un nuevo padre, este debe existir.
     *
     * @param id ID de la categoría a actualizar
     * @param dto datos nuevos
     * @return categoría actualizada convertida a DTO con hijos
     */
    public CategoryResponseDto update(UUID id, CategoryRequestDto dto) {
        Category category = findCategoryEntityById(id);

        Category parent = null;

        if (dto.getParentId() != null)
        {
            if (dto.getParentId().equals(id))
            {
                throw new RuntimeException("Una categoria no puede ser padre de si misma");
            }

            parent = findCategoryEntityById(dto.getParentId());
        }

        category.setName(dto.getName());
        category.setSlug(dto.getSlug());
        category.setDescription(dto.getDescription());
        category.setActive(dto.getActive());
        category.setParent(parent);

        return toResponseDtoWithChildren(categoryRepository.save(category));
    }

    /**
     * Elimina una categoría por su ID.
     * Si no existe, lanza excepción.
     *
     * @param id UUID de la categoría a eliminar
     */
    public void delete(UUID id)
    {
        Category category = findCategoryEntityById(id);
        categoryRepository.delete(category);
    }

    /**
     * Recupera una entidad Category por ID o lanza excepción.
     *
     * @param id UUID de la categoría
     * @return entidad Category existente
     */
    private Category findCategoryEntityById(UUID id)
    {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    /**
     * Convierte una categoría a DTO sin cargar hijos.
     * Útil para listados planos.
     *
     * @param category entidad Category
     * @return DTO sin children
     */
    private CategoryResponseDto toResponseDtoWithoutChildren(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .active(category.getActive())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .children(List.of())
                .build();
    }

    /**
     * Convierte una categoría a DTO incluyendo recursivamente todos sus hijos.
     * Construye un árbol completo de categorías.
     *
     * @param category entidad Category
     * @return DTO con children anidados
     */
    private CategoryResponseDto toResponseDtoWithChildren(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .active(category.getActive())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .children(
                        category.getChildren()
                                .stream()
                                .map(this::toResponseDtoWithChildren)
                                .toList()
                )
                .build();
    }
}
