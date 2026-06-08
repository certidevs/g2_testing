package com.ecommerce.controller.api;

import com.ecommerce.model.Purchase;
import com.ecommerce.model.enums.*;
import com.ecommerce.repository.PurchaseRepository;
import com.ecommerce.repository.UserRepository;
import org.h2.engine.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Quitamos los filtros de seguridad
@Transactional
class PurchaseRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PurchaseRepository purchaseRepository;

    Purchase purchase;

    @BeforeEach
    void setUp() {


        purchase = purchaseRepository.save(Purchase.builder()
                .id(UUID.randomUUID())
                .creationDate(LocalDateTime.of(2026, 5, 30, 12, 30))
                .purchaseStatus(PurchaseStatus.INITIATED)
                .shippingStatus(ShippingStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .processStatus(ProcessStatus.PENDING)
                .shippingMode(ShippingMode.STANDARD)
                .userComment("Comentario de prueba")
                .finishedDate(null)
                .totalPrice(100.0)
                .build());
    }

    @Test
    void findAll() throws Exception{

        mockMvc.perform(get("/api/v1/purchases"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(purchase.getId()))
                .andExpect(jsonPath("$[0].creationDate").value(purchase.getCreationDate()))
                .andExpect(jsonPath("$[0].shippingMode").value(purchase.getShippingMode().name()))
                .andExpect(jsonPath("$[0].purchaseStatus").value(purchase.getPurchaseStatus().name()))
                .andExpect(jsonPath("$[0].shippingStatus").value(purchase.getShippingStatus().name()))
                .andExpect(jsonPath("$[0].paymentStatus").value(purchase.getPaymentStatus().name()))
                .andExpect(jsonPath("$[0].processStatus").value(purchase.getProcessStatus().name()))
                .andExpect(jsonPath("$[0].userComment").value(purchase.getUserComment()))
                .andExpect(jsonPath("$[0].finishedDate").value(purchase.getFinishedDate()))
                .andExpect(jsonPath("$[0].totalPrice").value(purchase.getTotalPrice()));
    }

    @Test
    void findOne_OK() throws Exception{
        mockMvc.perform(get("/api/v1/purchases/" +  purchase.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(purchase.getId()))
                .andExpect(jsonPath("$.creationDate").value(purchase.getCreationDate()))
                .andExpect(jsonPath("$.shippingMode").value(purchase.getShippingMode().name()))
                .andExpect(jsonPath("$.purchaseStatus").value(purchase.getPurchaseStatus().name()))
                .andExpect(jsonPath("$.shippingStatus").value(purchase.getShippingStatus().name()))
                .andExpect(jsonPath("$.paymentStatus").value(purchase.getPaymentStatus().name()))
                .andExpect(jsonPath("$.processStatus").value(purchase.getProcessStatus().name()))
                .andExpect(jsonPath("$.userComment").value(purchase.getUserComment()))
                .andExpect(jsonPath("$.finishedDate").value(purchase.getFinishedDate()))
                .andExpect(jsonPath("$.totalPrice").value(purchase.getTotalPrice()));
    }

    @Test
    void findOne_NotFound() throws Exception{
        mockMvc.perform(get("/api/v1/purchases/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPurchase_OK() throws Exception {
        String purchaseJSON = """
                {
                  "id": null,
                  "purchaseStatus": "INITIATED",
                  "shippingMode": "STANDARD",
                  "totalPrice": 100.0
               }
               """;

        mockMvc.perform(
                        post("/api/v1/purchases")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(purchaseJSON)
                ).andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.purchaseStatus").value("INITIATED"));
                //.andExpect(jsonPath("$.shippingMode").value("STANDARD"));
                //.andExpect(jsonPath("$.totalPrice").value(100.0));
    }

    @Test
    void createPurchase_BadRequest() throws Exception {
        String purchaseJSON = """
                {
                  "id": 1,
                  "purchaseStatus": "INITIATED",
                  "shippingMode": "STANDARD",
                  "totalPrice": 100.0
               }
               """;
        mockMvc.perform(
                post("/api/v1/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(purchaseJSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void createPurchase_NameUnique() throws Exception {
        String purchaseJSON = """
                {
                    "id": null,
                    "purchaseStatus": "INITIATED",
                    "shippingMode": "STANDARD",
                    "totalPrice": 100.0
               }
               """;
        mockMvc.perform(
                post("/api/v1/purchases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(purchaseJSON)
        ).andExpect(status().isConflict()); // 409
    }

    @Test
    void updatePurchase_complete_OK() throws Exception {
        String purchaseJSON = """
                {
                  "id": null,
                  "purchaseStatus": "INITIATED",
                  "shippingMode": "STANDARD",
                  "totalPrice": 100.0
               }
               """;

        mockMvc.perform(
                        put("/api/v1/purchases/" + purchase.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(purchaseJSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(purchase.getId()))
                .andExpect(jsonPath("$.purchaseStatus").value("INITIATED"))
                .andExpect(jsonPath("$.shippingMode").value("STANDARD"))
                .andExpect(jsonPath("$.totalPrice").value(100.0));
    }

    @Test
    void updatePurchase_NotFound() throws Exception {
        String purchaseJSON = """
                {
                    "id": null,
                    "purchaseStatus": "INITIATED",
                    "shippingMode": "STANDARD",
                    "totalPrice": 100.0
               }
               """;

        mockMvc.perform(
                put("/api/v1/purchases/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(purchaseJSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void patchPartial_OK() throws Exception{
        String purchaseJSON = """
                {
                  "id": null;
                  "purchaseStatus": "INITIATED",
                  "shippingMode": "STANDARD",
                  "totalPrice": 100.0
               }
               """;

        mockMvc.perform(
                        patch("/api/v1/purchases/" + purchase.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(purchaseJSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(purchase.getId()))
                .andExpect(jsonPath("$.purchaseStatus").value("INITIATED"))
                .andExpect(jsonPath("$.shippingMode").value("STANDARD"))
                .andExpect(jsonPath("$.totalPrice").value(100.0));

    }

    @Test
    void deletePurchase() throws Exception {
        mockMvc.perform(delete("/api/v1/purchases/" + purchase.getId()))
                .andExpect(status().isNoContent()); // 204

        // faltaría probar el 409 conflict si hay productos apuntando a purchase
        // mockMvc.perform(delete("/api/v1/purchases/" + purchase2.getId()))
        //.andExpect(status().isConflict()); // 409
    }
}