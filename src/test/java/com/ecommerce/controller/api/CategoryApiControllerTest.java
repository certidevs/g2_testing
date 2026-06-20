package com.ecommerce.controller.api;

import com.ecommerce.dto.CategoryRequestDto;
import com.ecommerce.dto.CategoryResponseDto;
import com.ecommerce.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryApiControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        CategoryApiController categoryApiController = new CategoryApiController(categoryService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(categoryApiController)
                .setControllerAdvice(new ApiExceptionAdvice())
                .build();

        objectMapper = new ObjectMapper();
    }

    private CategoryRequestDto createRequestDto() {
        CategoryRequestDto dto = new CategoryRequestDto();
        dto.setName("Informática");
        dto.setSlug("informatica");
        dto.setDescription("Productos de informática");
        dto.setActive(true);
        dto.setParentId(null);
        return dto;
    }

    private CategoryResponseDto createResponseDto(UUID id) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(id);
        dto.setName("Informática");
        dto.setSlug("informatica");
        dto.setDescription("Productos de informática");
        dto.setActive(true);
        return dto;
    }

    @Test
    void findAll_shouldReturnAllCategories() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        CategoryResponseDto category1 = createResponseDto(id1);

        CategoryResponseDto category2 = new CategoryResponseDto();
        category2.setId(id2);
        category2.setName("Gaming");
        category2.setSlug("gaming");
        category2.setDescription("Productos gaming");
        category2.setActive(true);

        when(categoryService.findAll()).thenReturn(List.of(category1, category2));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[0].name").value("Informática"))
                .andExpect(jsonPath("$[0].slug").value("informatica"))
                .andExpect(jsonPath("$[0].description").value("Productos de informática"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].id").value(id2.toString()))
                .andExpect(jsonPath("$[1].name").value("Gaming"))
                .andExpect(jsonPath("$[1].slug").value("gaming"));

        verify(categoryService).findAll();
    }

    @Test
    void findRootCategories_shouldReturnCategoryTree() throws Exception {
        UUID rootId = UUID.randomUUID();

        CategoryResponseDto rootCategory = createResponseDto(rootId);

        when(categoryService.findRootCategories()).thenReturn(List.of(rootCategory));

        mockMvc.perform(get("/api/categories/tree"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(rootId.toString()))
                .andExpect(jsonPath("$[0].name").value("Informática"))
                .andExpect(jsonPath("$[0].slug").value("informatica"))
                .andExpect(jsonPath("$[0].description").value("Productos de informática"))
                .andExpect(jsonPath("$[0].active").value(true));

        verify(categoryService).findRootCategories();
    }

    @Test
    void findById_shouldReturnCategory() throws Exception {
        UUID id = UUID.randomUUID();

        CategoryResponseDto responseDto = createResponseDto(id);

        when(categoryService.findById(id)).thenReturn(responseDto);

        mockMvc.perform(get("/api/categories/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Informática"))
                .andExpect(jsonPath("$.slug").value("informatica"))
                .andExpect(jsonPath("$.description").value("Productos de informática"))
                .andExpect(jsonPath("$.active").value(true));

        verify(categoryService).findById(id);
    }

    @Test
    void create_shouldCreateCategoryAndReturnCreated() throws Exception {
        UUID id = UUID.randomUUID();

        CategoryRequestDto requestDto = createRequestDto();
        CategoryResponseDto responseDto = createResponseDto(id);

        when(categoryService.create(any(CategoryRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Informática"))
                .andExpect(jsonPath("$.slug").value("informatica"))
                .andExpect(jsonPath("$.description").value("Productos de informática"))
                .andExpect(jsonPath("$.active").value(true));

        ArgumentCaptor<CategoryRequestDto> dtoCaptor =
                ArgumentCaptor.forClass(CategoryRequestDto.class);

        verify(categoryService).create(dtoCaptor.capture());

        CategoryRequestDto capturedDto = dtoCaptor.getValue();

        assertEquals("Informática", capturedDto.getName());
        assertEquals("informatica", capturedDto.getSlug());
        assertEquals("Productos de informática", capturedDto.getDescription());
        assertEquals(true, capturedDto.getActive());
        assertEquals(null, capturedDto.getParentId());
    }

    @Test
    void update_shouldUpdateCategoryAndReturnCategory() throws Exception {
        UUID id = UUID.randomUUID();

        CategoryRequestDto requestDto = createRequestDto();

        CategoryResponseDto responseDto = createResponseDto(id);
        responseDto.setName("Informática Actualizada");
        responseDto.setSlug("informatica-actualizada");

        when(categoryService.update(eq(id), any(CategoryRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/categories/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Informática Actualizada"))
                .andExpect(jsonPath("$.slug").value("informatica-actualizada"))
                .andExpect(jsonPath("$.description").value("Productos de informática"))
                .andExpect(jsonPath("$.active").value(true));

        ArgumentCaptor<CategoryRequestDto> dtoCaptor =
                ArgumentCaptor.forClass(CategoryRequestDto.class);

        verify(categoryService).update(eq(id), dtoCaptor.capture());

        CategoryRequestDto capturedDto = dtoCaptor.getValue();

        assertEquals("Informática", capturedDto.getName());
        assertEquals("informatica", capturedDto.getSlug());
        assertEquals("Productos de informática", capturedDto.getDescription());
        assertEquals(true, capturedDto.getActive());
        assertEquals(null, capturedDto.getParentId());
    }

    @Test
    void delete_shouldDeleteCategoryAndReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(categoryService).delete(id);

        mockMvc.perform(delete("/api/categories/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(categoryService).delete(id);
    }

    @Test
    void create_whenDataIntegrityViolationException_shouldReturnConflict() throws Exception {
        CategoryRequestDto requestDto = createRequestDto();

        when(categoryService.create(any(CategoryRequestDto.class)))
                .thenThrow(new DataIntegrityViolationException("Categoría duplicada"));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Conflicto al guardar."));

        verify(categoryService).create(any(CategoryRequestDto.class));
    }

    @Test
    void update_whenDataIntegrityViolationException_shouldReturnConflict() throws Exception {
        UUID id = UUID.randomUUID();

        CategoryRequestDto requestDto = createRequestDto();

        when(categoryService.update(eq(id), any(CategoryRequestDto.class)))
                .thenThrow(new DataIntegrityViolationException("Categoría duplicada"));

        mockMvc.perform(put("/api/categories/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Conflicto al guardar."));

        verify(categoryService).update(eq(id), any(CategoryRequestDto.class));
    }

    @Test
    void delete_whenDataIntegrityViolationException_shouldReturnConflict() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new DataIntegrityViolationException("Categoría relacionada con productos"))
                .when(categoryService)
                .delete(id);

        mockMvc.perform(delete("/api/categories/{id}", id))
                .andExpect(status().isConflict())
                .andExpect(content().string("Conflicto al guardar."));

        verify(categoryService).delete(id);
    }
}