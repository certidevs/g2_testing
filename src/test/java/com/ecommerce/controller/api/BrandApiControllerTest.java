package com.ecommerce.controller.api;

import com.ecommerce.dto.BrandRequestDto;
import com.ecommerce.dto.BrandResponseDto;
import com.ecommerce.service.BrandService;
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
class BrandApiControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private BrandService brandService;

    @BeforeEach
    void setUp() {
        BrandApiController brandApiController = new BrandApiController(brandService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(brandApiController)
                .setControllerAdvice(new ApiExceptionAdvice())
                .build();

        objectMapper = new ObjectMapper();
    }

    private BrandRequestDto createRequestDto() {
        BrandRequestDto dto = new BrandRequestDto();
        dto.setName("Apple");
        dto.setNif("A12345678");
        dto.setCountry("Estados Unidos");
        dto.setWebsite("https://www.apple.com");
        dto.setActive(true);
        return dto;
    }

    private BrandResponseDto createResponseDto(UUID id) {
        BrandResponseDto dto = new BrandResponseDto();
        dto.setId(id);
        dto.setName("Apple");
        dto.setCountry("Estados Unidos");
        dto.setWebsite("https://www.apple.com");
        dto.setActive(true);
        return dto;
    }

    @Test
    void findAll_shouldReturnAllBrands() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        BrandResponseDto brand1 = createResponseDto(id1);

        BrandResponseDto brand2 = new BrandResponseDto();
        brand2.setId(id2);
        brand2.setName("Samsung");
        brand2.setCountry("Corea del Sur");
        brand2.setWebsite("https://www.samsung.com");
        brand2.setActive(true);

        when(brandService.findAll()).thenReturn(List.of(brand1, brand2));

        mockMvc.perform(get("/api/brands"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(id1.toString()))
                .andExpect(jsonPath("$[0].name").value("Apple"))
                .andExpect(jsonPath("$[0].country").value("Estados Unidos"))
                .andExpect(jsonPath("$[0].website").value("https://www.apple.com"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].id").value(id2.toString()))
                .andExpect(jsonPath("$[1].name").value("Samsung"));

        verify(brandService).findAll();
    }

    @Test
    void findById_shouldReturnBrand() throws Exception {
        UUID id = UUID.randomUUID();

        BrandResponseDto responseDto = createResponseDto(id);

        when(brandService.findById(id)).thenReturn(responseDto);

        mockMvc.perform(get("/api/brands/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Apple"))
                .andExpect(jsonPath("$.country").value("Estados Unidos"))
                .andExpect(jsonPath("$.website").value("https://www.apple.com"))
                .andExpect(jsonPath("$.active").value(true));

        verify(brandService).findById(id);
    }

    @Test
    void create_shouldCreateBrandAndReturnBrand() throws Exception {
        UUID id = UUID.randomUUID();

        BrandRequestDto requestDto = createRequestDto();
        BrandResponseDto responseDto = createResponseDto(id);

        when(brandService.create(any(BrandRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Apple"))
                .andExpect(jsonPath("$.country").value("Estados Unidos"))
                .andExpect(jsonPath("$.website").value("https://www.apple.com"))
                .andExpect(jsonPath("$.active").value(true));

        ArgumentCaptor<BrandRequestDto> dtoCaptor =
                ArgumentCaptor.forClass(BrandRequestDto.class);

        verify(brandService).create(dtoCaptor.capture());

        BrandRequestDto capturedDto = dtoCaptor.getValue();

        assertEquals("Apple", capturedDto.getName());
        assertEquals("Estados Unidos", capturedDto.getCountry());
        assertEquals("https://www.apple.com", capturedDto.getWebsite());
        assertEquals(true, capturedDto.getActive());
    }

    @Test
    void update_shouldUpdateBrandAndReturnBrand() throws Exception {
        UUID id = UUID.randomUUID();

        BrandRequestDto requestDto = createRequestDto();

        BrandResponseDto responseDto = createResponseDto(id);
        responseDto.setName("Apple Actualizada");

        when(brandService.update(eq(id), any(BrandRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/brands/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Apple Actualizada"))
                .andExpect(jsonPath("$.country").value("Estados Unidos"))
                .andExpect(jsonPath("$.website").value("https://www.apple.com"))
                .andExpect(jsonPath("$.active").value(true));

        ArgumentCaptor<BrandRequestDto> dtoCaptor =
                ArgumentCaptor.forClass(BrandRequestDto.class);

        verify(brandService).update(eq(id), dtoCaptor.capture());

        BrandRequestDto capturedDto = dtoCaptor.getValue();

        assertEquals("Apple", capturedDto.getName());
        assertEquals("Estados Unidos", capturedDto.getCountry());
        assertEquals("https://www.apple.com", capturedDto.getWebsite());
        assertEquals(true, capturedDto.getActive());
    }

    @Test
    void delete_shouldDeleteBrandAndReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(brandService).delete(id);

        mockMvc.perform(delete("/api/brands/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(brandService).delete(id);
    }

    @Test
    void create_whenDataIntegrityViolationException_shouldReturnConflict() throws Exception {
        BrandRequestDto requestDto = createRequestDto();

        when(brandService.create(any(BrandRequestDto.class)))
                .thenThrow(new DataIntegrityViolationException("Marca duplicada"));

        mockMvc.perform(post("/api/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Conflicto al guardar."));

        verify(brandService).create(any(BrandRequestDto.class));
    }

    @Test
    void update_whenDataIntegrityViolationException_shouldReturnConflict() throws Exception {
        UUID id = UUID.randomUUID();

        BrandRequestDto requestDto = createRequestDto();

        when(brandService.update(eq(id), any(BrandRequestDto.class)))
                .thenThrow(new DataIntegrityViolationException("Marca duplicada"));

        mockMvc.perform(put("/api/brands/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Conflicto al guardar."));

        verify(brandService).update(eq(id), any(BrandRequestDto.class));
    }
}