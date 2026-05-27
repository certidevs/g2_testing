package com.ecommerce.controller.web;

import com.ecommerce.dto.CategoryRequestDto;
import com.ecommerce.dto.CategoryResponseDto;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController
{
    private final CategoryService categoryService;
    private final ProductRepository productRepository;

    /**
     * GET /categories
     * Muestra un listado plano de todas las categorías.
     * Añade al modelo el atributo "categories" con la lista obtenida desde el servicio.
     *
     * @param model modelo MVC para pasar datos a la vista
     * @return nombre de la plantilla Thymeleaf (categories/list)
     */
    @GetMapping
    public String findAll(Model model)
    {
        model.addAttribute("categories", categoryService.findAll());
        return "categories/category-list";
    }

    /**
     * GET /categories/tree
     * Muestra la vista con la estructura en árbol de categorías (solo raíces con sus hijos anidados).
     * Útil para menús jerárquicos o administración de la estructura.
     *
     * @param model modelo MVC para la vista
     * @return nombre de la plantilla Thymeleaf (categories/tree)
     */
    @GetMapping("/tree")
    public String findTree(Model model)
    {
        model.addAttribute("categories", categoryService.findRootCategories());
        return "categories/category-tree";
    }

    /**
     * GET /categories/{id}
     * Muestra la página de detalle de una categoría, incluyendo su árbol de hijos.
     *
     * @param id    UUID de la categoría
     * @param model modelo MVC para la vista
     * @return nombre de la plantilla Thymeleaf (categories/detail)
     */
    @GetMapping("/{id}")
    public String findById(@PathVariable UUID id, Model model)
    {
        CategoryResponseDto category = categoryService.findById(id);

        List<UUID> categoryIds = categoryService.getCategoryAndChildrenIds(category);

        model.addAttribute("category", category);
        model.addAttribute("products", productRepository.findBySubcategoryIdIn(categoryIds));

        return "categories/category-detail";
    }

    /**
     * GET /categories/new
     * Muestra el formulario de creación de categoría.
     * Añade al modelo un CategoryRequestDto vacío para enlazar el formulario y la lista de posibles padres.
     *
     * @param model modelo MVC para la vista
     * @return nombre de la plantilla Thymeleaf (categories/form)
     */
    @GetMapping("/new")
    public String showCreateForm(Model model)
    {
        model.addAttribute("category", new CategoryRequestDto());
        // Lista de categorías para seleccionar como padre (puede usarse para dropdown)
        model.addAttribute("parentCategories", categoryService.findAll());
        model.addAttribute("formAction", "/categories");

        return "categories/category-form";
    }

    /**
     * POST /categories
     * Procesa la creación de una nueva categoría.
     * - Valida el DTO con @Valid.
     * - Si hay errores de validación, vuelve a mostrar el formulario con parentCategories.
     * - Si la creación falla por lógica de negocio, añade un error global y vuelve al formulario.
     * - Si todo va bien, añade un mensaje flash y redirige al listado.
     *
     * @param dto                DTO enlazado desde el formulario
     * @param bindingResult      resultado de la validación
     * @param model              modelo MVC para reinyectar datos en caso de error
     * @param redirectAttributes atributos para mensajes flash en la redirección
     * @return redirect o vista del formulario en caso de error
     */
    @PostMapping
    public String create(@Valid @ModelAttribute("category") CategoryRequestDto dto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes)
    {
        //Si hay errores de validacion volver a mostrar el formulario con la lista de padres
        if (bindingResult.hasErrors())
        {
            model.addAttribute("parentCategories", categoryService.findAll());
            model.addAttribute("formAction", "/categories");
            return "categories/category-form";
        }

        try
        {
          categoryService.create(dto);
          redirectAttributes.addFlashAttribute("successMessage", "categoria creada correctamente");
          return "redirect:/categories";
        }
        catch (RuntimeException ex)
        {
            //En caso de error de negocio, reinyectar parentCategories y mostrar el error
            model.addAttribute("parentCategories", categoryService.findAll());
            model.addAttribute("formAction", "/categories");
            bindingResult.reject("category.error", ex.getMessage());
            return "categories/category-form";
        }
    }

    /**
     * GET /categories/{id}/edit
     * Muestra el formulario de edición con los datos actuales de la categoría.
     * Convierte CategoryResponseDto a CategoryRequestDto para enlazar el formulario.
     *
     * @param id    UUID de la categoría a editar
     * @param model modelo MVC para la vista
     * @return nombre de la plantilla Thymeleaf (categories/form)
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable UUID id, Model model)
    {
        CategoryResponseDto category = categoryService.findById(id);

        //Se contruye el DTO de peticion a partir del DTO de respuesta para rellenar el formulario
        CategoryRequestDto dto = CategoryRequestDto.builder()
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .active(category.getActive())
                .parentId(category.getParentId())
                .build();

        model.addAttribute("category", dto);
        // Añadir el id para que la vista sepa que es edición
        model.addAttribute("categoryId", id);
        // Lista de posibles padres para el select
        model.addAttribute("parentCategories", categoryService.findAll());
        model.addAttribute("formAction", "/categories/" + id + "/edit");

        return "categories/category-form";
    }

    /**
     * POST /categories/{id}/edit
     * Procesa la actualización de una categoría existente.
     * - Valida el DTO.
     * - Si hay errores, vuelve a mostrar el formulario manteniendo categoryId y parentCategories.
     * - Si la actualización falla por lógica de negocio, añade error global y vuelve al formulario.
     *
     * @param id                 UUID de la categoría a actualizar
     * @param dto                DTO con los nuevos valores
     * @param bindingResult      resultado de la validación
     * @param model              modelo MVC para reinyectar datos en caso de error
     * @param redirectAttributes atributos para mensajes flash
     * @return redirect al listado o vista del formulario en caso de error
     */
    @PostMapping("/{id}/edit")
    public String update(@PathVariable UUID id, @Valid @ModelAttribute("category") CategoryRequestDto dto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes)
    {
        //Si hay errores de validación devolver el formulario con datos necesarios
        if (bindingResult.hasErrors())
        {
            model.addAttribute("categoryId", id);
            model.addAttribute("parentCategories", categoryService.findAll());
            model.addAttribute("formAction", "/categories/" + id + "/edit");
            return "categories/category-form";
        }

        try
        {
          categoryService.update(id, dto);
          redirectAttributes.addFlashAttribute("successMessage", "Categoria actualizada correctamente");
          return "redirect:/categories";
        }
        catch (RuntimeException ex)
        {
            //En caso de error de negocio, reinyectar datos y mostrar el error
            model.addAttribute("categoryId", id);
            model.addAttribute("parentCategories", categoryService.findAll());
            model.addAttribute("formAction", "/categories/" + id + "/edit");
            bindingResult.reject("category.error", ex.getMessage());
            return "categories/category-form";
        }
    }

    /**
     * POST /categories/{id}/delete
     * Elimina la categoría indicada y redirige al listado con un mensaje flash.
     * Considerar reglas de negocio antes de eliminar (p. ej. reubicar hijos o impedir eliminación si tiene productos).
     *
     * @param id                 UUID de la categoría a eliminar
     * @param redirectAttributes atributos para mensajes flash
     * @return redirect al listado de categorías
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes)
    {
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Categoria eliminada correctamente");
        return "redirect:/categories";
    }
    
}
