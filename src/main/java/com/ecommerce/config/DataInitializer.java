package com.ecommerce.config;

import com.ecommerce.model.Brand;
import com.ecommerce.model.Product;
import com.ecommerce.repository.BrandRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.PurchaseLineRepository;
import com.ecommerce.repository.PurchaseRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("!test")
public class DataInitializer  implements CommandLineRunner {
    private ProductRepository productRepo;
    private BrandRepository brandRepo;
    private PurchaseRepository purchaseRepo;
    private PurchaseLineRepository purchaseLineRepo;

    @Override
    public void run(String... args) throws Exception{
        System.out.println("HOLA DESDE DATA INITIALIZER");
        //if (productRepo.count() > 0) return;
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
    }
}
