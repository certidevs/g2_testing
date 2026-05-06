package com.ecommerce.controller.web;

import com.ecommerce.dto.BrandRequestDto;
import com.ecommerce.dto.BrandResponseDto;
import com.ecommerce.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.naming.Binding;
import javax.script.Bindings;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController
{
    /**
     * Servicio que contiene la lógica de negocio para Brand.
     * Inyectado por constructor gracias a Lombok (@RequiredArgsConstructor).
     */
    private final BrandService brandService;

    /**
     * GET /brands
     * Muestra la lista de marcas en la vista "brands/list".
     * Añade al modelo el atributo "brands" con la lista obtenida desde el servicio.
     *
     * @param model modelo MVC para pasar datos a la vista
     * @return nombre de la plantilla Thymeleaf (brands/list)
     */
    @GetMapping
    public String findAll(Model model)
    {
        model.addAttribute("brands", brandService.findAll());
        return "brands/list";
    }

    /**
     * GET /brands/{id}
     * Muestra la página de detalle de una marca.
     * Recupera la marca por id y la añade al modelo con la clave "brand".
     *
     * @param id    UUID de la marca
     * @param model modelo MVC para la vista
     * @return nombre de la plantilla Thymeleaf (brands/detail)
     */
    @GetMapping("/{id}")
    public String findById(@PathVariable UUID id, Model model)
    {
        BrandResponseDto brand = brandService.findById(id);
        model.addAttribute("brand", brand);
        return "brands/detail";
    }

    /**
     * GET /brands/new
     * Muestra el formulario de creación de marca.
     * Añade al modelo un BrandRequestDto vacío para enlazar el formulario.
     *
     * @param model modelo MVC para la vista
     * @return nombre de la plantilla Thymeleaf (brands/form)
     */
    @GetMapping("/new")
    public String showCreateForm(Model model)
    {
        model.addAttribute("brand", new BrandRequestDto());
        return "brands/form";
    }

    /**
     * POST /brands
     * Procesa la creación de una nueva marca.
     * - Valida el DTO con @Valid.
     * - Si hay errores de validación, vuelve a mostrar el formulario.
     * - Si la creación falla por lógica de negocio, añade un error global al BindingResult y vuelve al formulario.
     * - Si todo va bien, añade un mensaje flash y redirige al listado.
     *
     * @param dto                DTO enlazado desde el formulario
     * @param bindingResult      resultado de la validación
     * @param redirectAttributes atributos para mensajes flash en la redirección
     * @return redirect o vista del formulario en caso de error
     */
    @PostMapping
    public String create(@Valid @ModelAttribute("brand") BrandRequestDto dto, BindingResult bindingResult, RedirectAttributes redirectAttributes)
    {
        // Si hay errores de validación devolver el formulario con los errores
        if (bindingResult.hasErrors())
        {
            return "brands/form";
        }

        try
        {
         brandService.create(dto);
         //Correccion: clave del flash debe ser "successMessage"
         redirectAttributes.addFlashAttribute("susccessMessage", "Marca creada correctamente");
         return "redirect:/brands";
        }
        catch (RuntimeException ex)
        {
            // Añadir error global para mostrar mensaje en la vista
            bindingResult.reject("brand.error", ex.getMessage());
            return "brands/form";
        }
    }

    /**
     * GET /brands/{id}/edit
     * Muestra el formulario de edición con los datos actuales de la marca.
     * Convierte el BrandResponseDto a BrandRequestDto para enlazar el formulario.
     *
     * @param id    UUID de la marca a editar
     * @param model modelo MVC para la vista
     * @return nombre de la plantilla Thymeleaf (brands/form)
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable UUID id, Model model)
    {
        BrandResponseDto brand = brandService.findById(id);

        //Construir DTO de petición a partir del DTO de respuesta para rellenar el formulario
        BrandRequestDto dto = BrandRequestDto.builder()
                .name(brand.getName())
                .nif(brand.getNif())
                .country(brand.getCountry())
                .website(brand.getWebsite())
                .logo(brand.getLogo())
                .active(brand.getActive())
                .build();

        model.addAttribute("brand", dto);
        // Añadir el id de la marca para que el formulario sepa si es edición
        model.addAttribute("brandId", id);

        return "brands/form";
    }

    /**
     * POST /brands/{id}/edit
     * Procesa la actualización de una marca existente.
     * - Valida el DTO.
     * - Si hay errores, vuelve a mostrar el formulario (manteniendo brandId en el modelo).
     * - Si la actualización falla por lógica de negocio, añade error global y vuelve al formulario.
     *
     * @param id                 UUID de la marca a actualizar
     * @param dto                DTO con los nuevos valores
     * @param bindingResult      resultado de la validación
     * @param model              modelo MVC (se usa para reinyectar brandId en caso de error)
     * @param redirectAttributes atributos para mensajes flash
     * @return redirect al listado o vista del formulario en caso de error
     */
    @PostMapping("/{id}/edit")
    public String update(@PathVariable UUID id, @Valid @ModelAttribute("brand") BrandRequestDto dto, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes)
    {
        // Si hay errores de validacion devolver el formulario.
        if (bindingResult.hasErrors())
        {
            model.addAttribute("brandId", id);
            return "brands/form";
        }

        try
        {
            brandService.update(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Marca actualizada correctamente");
            return "redirect:/brands";
        }
        catch (RuntimeException ex)
        {
            // En caso de error de negocio reinyectar el id y mostrar el error en el formulario
            model.addAttribute("brandId", id);
            bindingResult.reject("brand.error", ex.getMessage());
            return "brands/form";
        }
    }

    /**
     * POST /brands/{id}/delete
     * Elimina la marca indicada y redirige al listado con un mensaje flash.
     *
     * @param id                 UUID de la marca a eliminar
     * @param redirectAttributes atributos para mensajes flash
     * @return redirect al listado de marcas
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes)
    {
        brandService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Marca eliminada correctamente");
        return "redirect:/brands";
    }

}
