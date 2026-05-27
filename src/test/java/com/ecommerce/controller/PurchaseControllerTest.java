package com.ecommerce.controller;

import com.ecommerce.model.Product;
import com.ecommerce.model.Purchase;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.*;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.PurchaseLineRepository;
import com.ecommerce.repository.PurchaseRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.PurchaseService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@AutoConfigureMockMvc // Desactiva security para las pruebas
@Transactional
@ActiveProfiles("test")
class PurchaseControllerTest {

    @Autowired
    PurchaseRepository purchaseRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PurchaseLineRepository purchaseLineRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    // Utilizamos la anotación MockitoBean para inyectar un mock de PurchaseService y así poder verificar las interacciones que tengan con este servicio durante las pruebas
    @MockitoBean
    PurchaseService purchaseService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    User user1;
    User user2;

    Product product1;
    Product product2;

    Purchase purchase1;
    Purchase purchase2;
    Purchase purchase3;
    Purchase purchase4;
    User user;
    User admin;
    @BeforeEach
    void setUp(){
        purchaseLineRepository.deleteAll();
        purchaseRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Hacemos reset del mock de purchaseService para asegurar que no haya nada previo que afecte a las pruebas etc
        reset(purchaseService);

        user = userRepository.save(User.builder()
                .username("user")
                .email("user@gmail.com")
                .password(passwordEncoder.encode("useruseruserA*"))
                .role(Role.ROLE_CUSTOMER)
                .build());
        admin = userRepository.save(User.builder()
                .username("adminadmin")
                .email("adminadmin@gmail.com")
                .password(passwordEncoder.encode("adminadminadmAin8*"))
                .role(Role.ROLE_ADMIN)
                .build());
        user1 = User.builder()
                .username("user1.purchase.controller")
                .name("User 1")
                .lastName("Last Name 1")
                .email("user1@gmail.com")
                .phone("123456789")
                .password("password1")
                .birthday(LocalDateTime.of(1990, Month.JANUARY, 1, 0, 0))
                .gender(Gender.MALE)
                .role(Role.ROLE_CUSTOMER)
                .build();

        user2 = User.builder()
                .username("user2.purchase.controller")
                .name("User 2")
                .lastName("Last Name 2")
                .email("user2@gmail.com")
                .phone("987654321")
                .password("password2")
                .birthday(LocalDateTime.of(1995, Month.JUNE, 15, 0, 0))
                .gender(Gender.FEMALE)
                .role(Role.ROLE_CUSTOMER)
                .build();

        userRepository.saveAll(List.of(user1, user2));

        product1 = Product.builder()
                .title("Product 1")
                .available(true)
                .price(20.00)
                .build();

        product2 = Product.builder()
                .title("Product 2")
                .available(true)
                .price(10.00)
                .build();

        productRepository.saveAll(List.of(product1, product2));

        purchase1 = Purchase.builder()
                .user(user1)
                .creationDate(LocalDateTime.of(2026, Month.MARCH, 15, 12, 45))
                .finishedDate(LocalDateTime.of(2026, Month.APRIL, 28, 17, 30))
                .purchaseStatus(PurchaseStatus.FINISHED)
                .paymentStatus(PaymentStatus.PAID)
                .processStatus(ProcessStatus.COMPLETED)
                .shippingStatus(ShippingStatus.DELIVERED)
                .shippingMode(ShippingMode.STANDARD)
                .totalPrice(50.00)
                .userComment("Me ha llegado el producto en mal estado")
                .build();

        purchase2 = Purchase.builder()
                .user(user2)
                .creationDate(LocalDateTime.of(2025, Month.JUNE, 10, 18, 35))
                .finishedDate(LocalDateTime.of(2025, Month.DECEMBER, 25, 16, 15))
                .purchaseStatus(PurchaseStatus.FINISHED)
                .paymentStatus(PaymentStatus.PAID)
                .processStatus(ProcessStatus.COMPLETED)
                .shippingStatus(ShippingStatus.DELIVERED)
                .shippingMode(ShippingMode.EXPRESS)
                .totalPrice(15.45)
                .userComment("El producto ha llegado bien pero he tardado mucho más de lo esperado teniendo en cuenta que era EXPRESS")
                .build();

        purchase3 = Purchase.builder()
                .user(user1)
                .creationDate(LocalDateTime.of(2026, Month.FEBRUARY, 10, 11, 50))
                .finishedDate(null)
                .purchaseStatus(PurchaseStatus.INITIATED)
                .paymentStatus(PaymentStatus.PENDING)
                .processStatus(ProcessStatus.PENDING)
                .shippingStatus(ShippingStatus.PENDING)
                .shippingMode(ShippingMode.PREMIUM)
                .totalPrice(150.75)
                .userComment(null)
                .build();

        purchase4 = Purchase.builder()
                .user(user2)
                .creationDate(LocalDateTime.of(2020, Month.MAY, 30, 8, 30))
                .finishedDate(null)
                .purchaseStatus(PurchaseStatus.INACTIVE)
                .paymentStatus(PaymentStatus.PENDING)
                .processStatus(ProcessStatus.PENDING)
                .shippingStatus(ShippingStatus.PENDING)
                .shippingMode(ShippingMode.STANDARD)
                .totalPrice(73.00)
                .userComment(null)
                .build();

        purchaseRepository.saveAll(List.of(purchase1, purchase2, purchase3, purchase4));
    }

    // Verifica que la lista de compras se muestra correctamente con datos completos
    @Test
    void purchasesFull() throws Exception {
        mockMvc.perform(get("/purchases").with(user(admin)))
                .andExpect(status().isOk())
                .andExpect(view().name("purchases/purchase-list"))
                .andExpect(model().attributeExists("purchases"))
                .andExpect(model().attribute("purchases", hasSize(4)))
                .andExpect(model().attribute("purchases", hasItem(hasProperty("id", is(purchase1.getId())))))
                .andExpect(model().attribute("purchases", hasItem(hasProperty("id", is(purchase4.getId())))));
    }

    // Verifica que la lista de compras se muestra correctamente cuando está vacía
    @Test
    void purchasesEmpty() throws Exception {
        purchaseRepository.deleteAll();

        mockMvc.perform(get("/purchases").with(user(admin)))
                .andExpect(status().isOk())
                .andExpect(view().name("purchases/purchase-list"))
                .andExpect(model().attributeExists("purchases"))
                .andExpect(model().attribute("purchases", hasSize(0)));
    }

    // Verifica que el detalle de una compra se muestra correctamente cuando la compra existe (purchase-detail)
    @Test
    void purchaseDetailIsPresentTrue() throws Exception {

        Purchase purchase = new Purchase();
        purchase = purchaseRepository.save(purchase);

        mockMvc.perform(get("/purchases/{id}", purchase.getId()).with(user(admin)))
                .andExpect(status().isOk())
                .andExpect(view().name("purchases/purchase-detail"))
                .andExpect(model().attributeExists("purchase"))
                .andExpect(model().attribute("purchase", hasProperty("id", is(purchase.getId()))));
    }

    // Verifica que se muestra un error 404 cuando se intenta acceder al detalle de una compra que no existe (purchase-detail)
    @Test
    void purchaseDetailIsPresentFalse() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/purchases/{id}", randomId).with(user(admin)))
                .andExpect(status().isNotFound());
    }

    // Verifica que se muestra el formulario de creación de compra con los datos necesarios para crear una nueva compra (purchase-form)
    @Test
    void showCreatePurchaseForm() throws Exception {
        mockMvc.perform(get("/purchases/new").with(user(admin)))
                .andExpect(status().isOk())
                .andExpect(view().name("purchases/purchase-form"))
                .andExpect(model().attributeExists("purchase"));
    }

    // Verifica que al enviar el formulario de creación de compra se redirige a la lista de compras
    @Test
    void createPurchaseRedirectsToPurchaseList() throws Exception {

        doNothing().when(purchaseService).createPurchase(
                        any(Purchase.class),
                        any(User.class)
        );
        mockMvc.perform(post("/purchases")
                        .with(csrf())
                        .with(user(admin))
                        .param("totalPrice", "99.99")
                        .param("purchaseStatus", PurchaseStatus.INITIATED.name())
                        .param("paymentStatus", PaymentStatus.PENDING.name())
                        .param("processStatus", ProcessStatus.PENDING.name())
                        .param("shippingStatus", ShippingStatus.PENDING.name())
                        .param("shippingMode", ShippingMode.STANDARD.name()))
                // [ is3xxRedirection() se utiliza para verificar que la respuesta HTTP es una redirección ]
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/purchases*"));

        // Cuando hagamos pruebas que tengamos que verificar la lógica de service utilizaremos verify,
        // de tal manera se comprueba que se han llamado a los métodos correspondientes del servicio con los argumentos esperados, lo que nos permite asegurarnos de que la lógica de negocio se está ejecutando correctamente durante las pruebas
        verify(purchaseService).createPurchase(any(Purchase.class), nullable(User.class));
    }

    // Verifica que al eliminar una compra se redirige a la lista de compras
    @Test
    void deletePurchaseRedirectsAndAddsFlashMessage() throws Exception {
        mockMvc.perform(get("/purchases/delete/{id}", purchase1.getId()).with(user(admin)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/purchases*"))
                .andExpect(flash().attribute("message", "Purchase deleted successfully"));

        verify(purchaseService).deletePurchase(purchase1.getId());
    }

    // Verifica que al agregar un producto a la compra se redirige al detalle de la compra
    @Test
    void addProductRedirectsToPurchaseDetail() throws Exception {
        Purchase cart = Purchase.builder().build();
        cart.setId(purchase3.getId());
        when(purchaseService.addProductToCart(eq(product1.getId()), nullable(User.class))).thenReturn(cart);

        mockMvc.perform(get("/purchases/add/{productId}", product1.getId()).with(user(admin)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/purchases/" + purchase3.getId() + "*"));

        verify(purchaseService).addProductToCart(eq(product1.getId()), nullable(User.class));
    }

    // Verifica que al eliminar un producto de la compra se redirige al detalle de la compra
    @Test
    void showCartWithExistingCart() throws Exception {
        when(purchaseService.getOrCreateCartForUser(any(UUID.class))).thenReturn(Optional.of(purchase3));

        mockMvc.perform(get("/purchases/{id}/cart", purchase3.getId()).with(user(admin)))
                .andExpect(status().isOk())
                .andExpect(view().name("purchases/cart"))
                .andExpect(model().attribute("cart", hasProperty("id", is(purchase3.getId()))))
                .andExpect(model().attributeExists("lines"));

        verify(purchaseService).getOrCreateCartForUser(any(UUID.class));
    }

    // Verifica que al intentar mostrar el carrito sin un carrito existente se muestra la vista de carrito con un atributo "cart" nulo
    @Test
    void showCartWithoutExistingCart() throws Exception {
        when(purchaseService.getOrCreateCartForUser(any(UUID.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/purchases/{id}/cart", purchase3.getId()).with(user(admin)))
                .andExpect(status().isOk())
                .andExpect(view().name("purchases/cart"))
                .andExpect(model().attribute("cart", nullValue()));

        verify(purchaseService).getOrCreateCartForUser(any(UUID.class));
    }

    // Verifica que al finalizar una compra se redirige a la lista de compras
    @Test
    void finishPurchaseRedirectsToPurchaseList() throws Exception {
        mockMvc.perform(get("/purchases/{id}/finish", purchase1.getId()).with(user(admin)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/purchases*"));

        verify(purchaseService).completePurchase(purchase1.getId());
    }

    // Verifica que al cancelar una compra se redirige a la lista de compras
    @Test
    void incrementLineQuantityRedirectsToPurchaseDetail() throws Exception {
        mockMvc.perform(get("/purchases/{purchaseId}/lines/add/{productId}", purchase1.getId(), product1.getId()).with(user(admin)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/purchases/" + purchase1.getId() + "*"));

        verify(purchaseService).addProductToCart(eq(product1.getId()), nullable(User.class));
    }

    // Verifica que al eliminar un producto de la compra se redirige al detalle de la compra
    @Test
    void decrementLineQuantityRedirectsToPurchaseDetail() throws Exception {
        mockMvc.perform(get("/purchases/{purchaseId}/lines/remove/{productId}", purchase1.getId(), product1.getId()).with(user(admin)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/purchases/" + purchase1.getId() + "*"));

        verify(purchaseService).removeProductFromCart(eq(product1.getId()), nullable(User.class));
    }
}