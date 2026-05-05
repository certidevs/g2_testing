package com.ecommerce.config;

import com.ecommerce.model.Brand;
import com.ecommerce.model.Product;
import com.ecommerce.model.Purchase;
import com.ecommerce.model.Reviews;
import com.ecommerce.model.Users;
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
    private PurchaseRepository purchaseRepo;
    private PurchaseLineRepository purchaseLineRepo;
    private UsersRepository userRepo;
    private ReviewRepository reviewRepo;

    @Override
    public void run(String... args) throws Exception{
        System.out.println("HOLA DESDE DATA INITIALIZER");
        //if (productRepo.count() > 0) return;

        var user1 = Users.builder()
                .name("User 1")
                .lastName("Last Name 1")
                .email("user1@gmail.com")
                .phone("123456789")
                .password("password1")
                .birthday(LocalDateTime.of(1990, Month.JANUARY, 1, 0, 0))
                .gender(Gender.MALE)
                .role(Role.CUSTOMER)
                .build();

        var user2 = Users.builder()
                .name("User 2")
                .lastName("Last Name 2")
                .email("user2@gmail.com")
                .phone("987654321")
                .password("password2")
                .birthday(LocalDateTime.of(1995, Month.JUNE, 15, 0, 0))
                .gender(Gender.FEMALE)
                .role(Role.CUSTOMER)
                .build();
        userRepo.saveAll(List.of(user1,user2));

        var brand1 = brandRepo.save(Brand.builder().name("Nike").nif("123456789").build());

        var product1 = productRepo.save(Product.builder().title("Camiseta Blanca")
                        .longDescription("Esta camiseta blanca de Nike es perfecta para cualquier ocasión. Confeccionada con algodón de alta calidad.")
                .imageUrl("https://static.nike.com/a/images/t_web_pw_592_v2/f_auto/0b73e9b3-1bba-4a6b-8d4b-ecc9d2a473c8/M+NSW+TEE+M90+FW+MBR+CNCT+HO25.png")
                .price(30.00).brand(brand1).build());
        var product2 = productRepo.save(Product.builder().title("Pantalon Deporte").imageUrl("https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcR6ErZKh_-lTTwptKNoNRmIE7wEd-IIm_GQsU3c5SjQxDU2kj0678uu_vFbVX2QgNrLozE4rcSjki-WGpfYw83KzWSmPjNus4dd5V4hNJJb-SSn-lCSMw5Ez6kRMYbLGeh3rUoMWCpx&usqp=CAc")
                .price(45.00).brand(brand1).build());
        var product3 = productRepo.save(Product.builder().title("Zapatillas Run").imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQmqrHlyvYmEn1ghVn1P9djF-vH5PCjlwOhgw&s")
                .price(110.00).brand(brand1).build());
        var product4 = productRepo.save(Product.builder().title("Calzetines Run").imageUrl("https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcRLO_S61-_IXWE6l0J4WiBdUcnglzV7e9ZBYwEPKLQ2whre7AdR-7K8D0MxyEYo-EqsivQma9grEEiWiZEnvPx7W0Q8z5mnyy0oLR7HsXAQQnEtNe8CsRLVdbSDUD3xxZngN83U6efJ&usqp=CAc")
                .price(10.00).brand(brand1).build());

        var purchase1 = Purchase.builder()
                .users(user1)
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
                .users(user2)
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
                .users(user1)
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
                .users(user2)
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

        // Reseñas de ejemplo
        var review1 = Reviews.builder()
                .title("Camiseta de muy buena calidad")
                .message("Estoy muy contento con la compra. El tejido es suave y resistente, y la talla es exacta. La recomiendo totalmente.")
                .rating(5)
                .verified(true)
                .status(ReviewStatus.APPROVED)
                .creationDate(LocalDateTime.of(2026, Month.APRIL, 10, 10, 30))
                .modifiedDate(LocalDateTime.of(2026, Month.APRIL, 10, 10, 30))
                .product(product1)
                .users(user1)
                .build();

        var review2 = Reviews.builder()
                .title("Pantalón correcto pero talla grande")
                .message("El pantalón es cómodo para hacer deporte, pero la talla es bastante grande. Recomendaría pedir una talla menos de lo habitual.")
                .rating(3)
                .verified(true)
                .status(ReviewStatus.APPROVED)
                .creationDate(LocalDateTime.of(2026, Month.MARCH, 20, 15, 0))
                .modifiedDate(LocalDateTime.of(2026, Month.MARCH, 22, 9, 0))
                .product(product2)
                .users(user2)
                .build();

        var review3 = Reviews.builder()
                .title("Zapatillas increíbles para correr")
                .message("Las mejores zapatillas que he tenido. Muy cómodas desde el primer día, sin necesidad de adaptación. El amortiguamiento es excelente.")
                .rating(5)
                .verified(false)
                .status(ReviewStatus.PENDING_APPROVAL)
                .creationDate(LocalDateTime.of(2026, Month.MAY, 1, 8, 0))
                .modifiedDate(LocalDateTime.of(2026, Month.MAY, 1, 8, 0))
                .product(product3)
                .users(user1)
                .build();

        var review4 = Reviews.builder()
                .title("Calcetines flojos, se deforman rápido")
                .message("Después de unas pocas lavadas los calcetines pierden la forma. No son malos del todo pero esperaba más durabilidad para el precio.")
                .rating(2)
                .verified(true)
                .status(ReviewStatus.REJECTED)
                .creationDate(LocalDateTime.of(2025, Month.DECEMBER, 5, 18, 45))
                .modifiedDate(LocalDateTime.of(2025, Month.DECEMBER, 6, 11, 0))
                .product(product4)
                .users(user2)
                .build();

        reviewRepo.saveAll(List.of(review1, review2, review3, review4));
    }
}
