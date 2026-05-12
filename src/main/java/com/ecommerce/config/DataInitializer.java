package com.ecommerce.config;

import com.ecommerce.model.*;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.*;
import com.ecommerce.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Component
@AllArgsConstructor
@Profile("!test")
public class DataInitializer  implements CommandLineRunner {
    private ProductRepository productRepo;
    private BrandRepository brandRepo;
    private CategoryRepository categoryRepo;
    private PurchaseRepository purchaseRepo;
    private PurchaseLineRepository purchaseLineRepo;
    private UserRepository userRepo;
    private ReviewRepository reviewRepo;
    private AddressRepository addressRepo;

    @Override
    public void run(String... args) throws Exception{
        System.out.println("HOLA DESDE DATA INITIALIZER");
        //if (productRepo.count() > 0) return;

        var user1 = User.builder()
                .name("User 1")
                .lastName("Last Name 1")
                .email("user1@gmail.com")
                .phone("123456789")
                .password("password1")
                .birthday(LocalDateTime.of(1990, Month.JANUARY, 1, 0, 0))
                .gender(Gender.MALE)
                .role(Role.CUSTOMER)
                .build();

        var user2 = User.builder()
                .name("User 2")
                .lastName("Last Name 2")
                .email("user2@gmail.com")
                .phone("987654321")
                .password("password2")
                .birthday(LocalDateTime.of(1995, Month.JUNE, 15, 0, 0))
                .gender(Gender.FEMALE)
                .role(Role.CUSTOMER)
                .build();

        var adminUser = User.builder()
                .name("Admin")
                .lastName("G2")
                .email("admin@g2store.com")
                .phone("600000000")
                .password("admin123")
                .birthday(LocalDateTime.of(1988, Month.JANUARY, 10, 0, 0))
                .gender(Gender.MALE)
                .role(Role.ADMIN)
                .build();
        userRepo.saveAll(List.of(user1, user2, adminUser));

        // Crear direcciones de prueba
        var address1 = Address.builder()
                .street("Calle Mayor")
                .number("123")
                .city("Madrid")
                .state("Madrid")
                .zipCode("28001")
                .country("España")
                .addressType(AddressType.PRIMARY)
                .user(user1)
                .build();

        var address2 = Address.builder()
                .street("Avenida Diagonal")
                .number("456")
                .city("Barcelona")
                .state("Cataluña")
                .zipCode("08019")
                .country("España")
                .addressType(AddressType.SHIPPING)
                .user(user1)
                .complement("Piso 2º Puerta A")
                .build();

        var address3 = Address.builder()
                .street("Gran Via")
                .number("789")
                .city("Bilbao")
                .state("País Vasco")
                .zipCode("48001")
                .country("España")
                .addressType(AddressType.BILLING)
                .user(user2)
                .complement("Bajo Interior")
                .build();

        var address4 = Address.builder()
                .street("Calle Luna")
                .number("321")
                .city("Valencia")
                .state("Comunidad Valenciana")
                .zipCode("46001")
                .country("España")
                .addressType(AddressType.SECONDARY)
                .user(user2)
                .build();

        addressRepo.saveAll(List.of(address1, address2, address3, address4));

        var brand1 = brandRepo.save(Brand.builder()
                .name("Nike")
                .nif("123456789")
                .country("US")
                .website("https://www.nike.com")
                .logo("nike-logo.png")
                .active(true)
                .build());

        // --- Crear categorías iniciales (si no existen) ---
        Category ropaRoot = categoryRepo.findBySlug("ropa")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Ropa")
                        .slug("ropa")
                        .description("Ropa y prendas deportivas")
                        .active(true)
                        .parent(null)
                        .build()));
        Category electronicaRoot = categoryRepo.findBySlug("electronica")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("informatica")
                        .slug("informatica")
                        .description("informatica y tecnologias")
                        .active(true)
                        .parent(null)
                        .build()));

        Category camisetas = categoryRepo.findBySlug("camisetas")
                .orElseGet(() -> {
                    // evitar slug duplicado bajo el mismo padre
                    if (categoryRepo.existsByParentIdAndSlug(ropaRoot.getId(), "camisetas")) {
                        return categoryRepo.findByParentId(ropaRoot.getId()).stream()
                                .filter(c -> "camisetas".equals(c.getSlug()))
                                .findFirst()
                                .orElse(null);
                    }
                    return categoryRepo.save(Category.builder()
                            .name("Camisetas")
                            .slug("camisetas")
                            .description("Camisetas y tops")
                            .active(true)
                            .parent(ropaRoot)
                            .build());
                });

        Category pantalones = categoryRepo.findBySlug("pantalones")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Pantalones")
                        .slug("pantalones")
                        .description("Pantalones deportivos y de entrenamiento")
                        .active(true)
                        .parent(ropaRoot)
                        .build()));

        Category calzado = categoryRepo.findBySlug("calzado")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Calzado")
                        .slug("calzado")
                        .description("Zapatillas y calzado deportivo")
                        .active(true)
                        .parent(ropaRoot)
                        .build()));
        Category moviles = categoryRepo.findBySlug("moviles")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("moviles")
                        .slug("movil")
                        .description("moviles y tablets")
                        .active(true)
                        .parent(electronicaRoot)
                        .build()));
        Category portatil = categoryRepo.findBySlug("portatil")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("portatil")
                        .slug("portatil")
                        .description("portatiles")
                        .active(true)
                        .parent(electronicaRoot)
                        .build()));


// --- fin categorías ---

        var product1 = productRepo.save(Product.builder()
                .title("Camiseta Blanca")
                .subcategory(camisetas)
                .longDescription("Esta camiseta blanca de Nike es perfecta para cualquier ocasión. Confeccionada con algodón de alta calidad.")
                .imageUrl("https://static.nike.com/a/images/t_web_pw_592_v2/f_auto/0b73e9b3-1bba-4a6b-8d4b-ecc9d2a473c8/M+NSW+TEE+M90+FW+MBR+CNCT+HO25.png")
                .subcategory(camisetas)
                .price(30.00).brand(brand1).build());

        var product2 = productRepo.save(Product.builder()
                .title("Pantalon Deporte")
                        .subcategory(pantalones)
                .imageUrl("https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcR6ErZKh_-lTTwptKNoNRmIE7wEd-IIm_GQsU3c5SjQxDU2kj0678uu_vFbVX2QgNrLozE4rcSjki-WGpfYw83KzWSmPjNus4dd5V4hNJJb-SSn-lCSMw5Ez6kRMYbLGeh3rUoMWCpx&usqp=CAc")
                .subcategory(pantalones)
                .price(45.00).brand(brand1).build());

        var product3 = productRepo.save(Product.builder()
                .title("Zapatillas Run")
                        .subcategory(calzado)
                .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQmqrHlyvYmEn1ghVn1P9djF-vH5PCjlwOhgw&s")
                .subcategory(calzado)
                .price(110.00).brand(brand1).build());

        var product4 = productRepo.save(Product.builder()
                .title("Calzetines Run")
                        .subcategory(calzado)
                .imageUrl("https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcRLO_S61-_IXWE6l0J4WiBdUcnglzV7e9ZBYwEPKLQ2whre7AdR-7K8D0MxyEYo-EqsivQma9grEEiWiZEnvPx7W0Q8z5mnyy0oLR7HsXAQQnEtNe8CsRLVdbSDUD3xxZngN83U6efJ&usqp=CAc")
                .price(10.00).brand(brand1).build());
        var product5 = productRepo.save(Product.builder()
                .title("Iphone 15")
                .subcategory(moviles)
                .longDescription("iphone 15 128g")
                .imageUrl("https://imgs.search.brave.com/ESQDLqeCxUyCOQQrWLBxQsuDBwmLcHYDjL-jh_-U7iY/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9pbWFn/ZXMudW5zcGxhc2gu/Y29tL3Bob3RvLTE3/MDQzODA4OTUzMTYt/Y2FhMmU0ZDY4YTdl/P2ZtPWpwZyZxPTYw/Jnc9MzAwMCZhdXRv/PWZvcm1hdCZmaXQ9/Y3JvcCZpeGxpYj1y/Yi00LjEuMCZpeGlk/PU0zd3hNakEzZkRC/OE1IeHpaV0Z5WTJo/OE4zeDhhWEJvYjI1/bEpUSXdNVFY4Wlc1/OE1IeDhNSHg4ZkRB/PQ")
                .price(1200.00).brand(brand1).build());
        var product6 = productRepo.save(Product.builder()
                .title("Macbook Pro 16")
                .subcategory(portatil)
                .longDescription("Macbook Pro 16 pulgadas con chip M1 Pro, 16GB RAM, 512GB SSD")
                        .imageUrl("https://imgs.search.brave.com/sciRFp0EHNluo57mCivZlwX-wnfY53SR_AfdnAIF4PE/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly93d3cu/Y25ldC5jb20vYS9p/bWcvcmVzaXplLzJm/OTg0ZTczODVjYWJl/NjRlYmZhZWFiZjI1/ZjYzNDFjZmUzZDYz/MWEvaHViLzIwMTkv/MTEvMTIvODRlMTE1/OWMtYjhjYi00MzQ5/LTllM2ItM2MzN2Nj/Nzg5NDVmLzM2LW1h/Y2Jvb2stcHJvLTE2/LWluY2guanBnP2F1/dG89d2VicCZ3aWR0/aD0xMjAw")
                .price(2500.00).brand(brand1).build());








        var purchase1 = Purchase.builder()
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

        var purchase2 = Purchase.builder()
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

        var purchase3 = Purchase.builder()
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

        var purchase4 = Purchase.builder()
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

        purchaseRepo.saveAll(List.of(purchase1, purchase2, purchase3, purchase4));
        // purchase lines asociadas a la compra 1: (el usuario añade 3 productos a la compra 1)
        purchaseLineRepo.saveAll(List.of(
                PurchaseLine.builder().quantity(2).product(product1).purchase(purchase1).build(),
                PurchaseLine.builder().quantity(4).product(product2).purchase(purchase1).build(),
                PurchaseLine.builder().quantity(1).product(product3).purchase(purchase1).build()
        ));
        // purchase lines asociadas a la compra 2: el usaurio añade 4 productos a la compra 2)
        purchaseLineRepo.saveAll(List.of(
                PurchaseLine.builder().quantity(1).product(product4).purchase(purchase2).build(),
                PurchaseLine.builder().quantity(4).product(product1).purchase(purchase2).build(),
                PurchaseLine.builder().quantity(6).product(product2).purchase(purchase2).build(),
                PurchaseLine.builder().quantity(6).product(product3).purchase(purchase2).build()
        ));

        // Reseñas de ejemplo
        var review1 = Review.builder()
                .title("Camiseta de muy buena calidad")
                .message("Estoy muy contento con la compra. El tejido es suave y resistente, y la talla es exacta. La recomiendo totalmente.")
                .rating(5)
                .verified(true)
                .status(ReviewStatus.APPROVED)
                .creationDate(LocalDateTime.of(2026, Month.APRIL, 10, 10, 30))
                .modifiedDate(LocalDateTime.of(2026, Month.APRIL, 10, 10, 30))
                .product(product1)
                .user(user1)
                .build();

        var review2 = Review.builder()
                .title("Pantalón correcto pero talla grande")
                .message("El pantalón es cómodo para hacer deporte, pero la talla es bastante grande. Recomendaría pedir una talla menos de lo habitual.")
                .rating(3)
                .verified(true)
                .status(ReviewStatus.APPROVED)
                .creationDate(LocalDateTime.of(2026, Month.MARCH, 20, 15, 0))
                .modifiedDate(LocalDateTime.of(2026, Month.MARCH, 22, 9, 0))
                .product(product2)
                .user(user2)
                .build();

        var review3 = Review.builder()
                .title("Zapatillas increíbles para correr")
                .message("Las mejores zapatillas que he tenido. Muy cómodas desde el primer día, sin necesidad de adaptación. El amortiguamiento es excelente.")
                .rating(5)
                .verified(false)
                .status(ReviewStatus.PENDING_APPROVAL)
                .creationDate(LocalDateTime.of(2026, Month.MAY, 1, 8, 0))
                .modifiedDate(LocalDateTime.of(2026, Month.MAY, 1, 8, 0))
                .product(product3)
                .user(user1)
                .build();

        var review4 = Review.builder()
                .title("Calcetines flojos, se deforman rápido")
                .message("Después de unas pocas lavadas los calcetines pierden la forma. No son malos del todo pero esperaba más durabilidad para el precio.")
                .rating(2)
                .verified(true)
                .status(ReviewStatus.REJECTED)
                .creationDate(LocalDateTime.of(2025, Month.DECEMBER, 5, 18, 45))
                .modifiedDate(LocalDateTime.of(2025, Month.DECEMBER, 6, 11, 0))
                .product(product4)
                .user(user2)
                .build();

        var review5 = Review.builder()
                .title("Camiseta muy bonita pero se arruga")
                .message("El diseño es bonito y la calidad del tejido es buena, pero se arruga bastante al lavarla. Hay que plancharla siempre.")
                .rating(4)
                .verified(true)
                .status(ReviewStatus.APPROVED)
                .creationDate(LocalDateTime.of(2026, Month.APRIL, 15, 9, 0))
                .modifiedDate(LocalDateTime.of(2026, Month.APRIL, 15, 9, 0))
                .product(product1)
                .user(user2)
                .build();

        var review6 = Review.builder()
                .title("Camiseta decepcionante")
                .message("Esperaba más por el precio. El color se fue a la primera lavada y talla muy pequeño para la talla indicada.")
                .rating(1)
                .verified(false)
                .status(ReviewStatus.PENDING_APPROVAL)
                .creationDate(LocalDateTime.of(2026, Month.MAY, 2, 11, 15))
                .modifiedDate(LocalDateTime.of(2026, Month.MAY, 2, 11, 15))
                .product(product1)
                .user(user1)
                .build();

        var review7 = Review.builder()
                .title("Zapatillas cómodas pero poco duraderas")
                .message("Son muy cómodas al principio pero la suela empezó a despegarse después de dos meses de uso regular.")
                .rating(3)
                .verified(true)
                .status(ReviewStatus.APPROVED)
                .creationDate(LocalDateTime.of(2026, Month.APRIL, 20, 14, 30))
                .modifiedDate(LocalDateTime.of(2026, Month.APRIL, 20, 14, 30))
                .product(product3)
                .user(user2)
                .build();

        var review8 = Review.builder()
                .title("Las mejores zapatillas del mercado")
                .message("He probado muchas marcas y estas sin duda son las mejores. Ligeras, transpirables y con un agarre excelente en todo tipo de superficies.")
                .rating(5)
                .verified(true)
                .status(ReviewStatus.APPROVED)
                .creationDate(LocalDateTime.of(2026, Month.MARCH, 10, 16, 0))
                .modifiedDate(LocalDateTime.of(2026, Month.MARCH, 10, 16, 0))
                .product(product3)
                .user(user1)
                .build();

        reviewRepo.saveAll(List.of(review1, review2, review3, review4, review5, review6, review7, review8));
    }
}
