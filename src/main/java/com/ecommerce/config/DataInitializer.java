package com.ecommerce.config;

import com.ecommerce.model.Brand;
import com.ecommerce.model.Product;
import com.ecommerce.repository.BrandRepository;
import com.ecommerce.repository.ProductRepository;
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

    @Override
    public void run(String... args) throws Exception{
        System.out.println("HOLA DESDE DATA INITIALIZER");
        //if (productRepo.count() > 0) return;

        var brand1 = brandRepo.save(Brand.builder().name("Nike").nif("123456789").build());

        var product1 = productRepo.save(Product.builder().title("Camiseta Blanca").price(30.00).brand(brand1).build());
        var product2 = productRepo.save(Product.builder().title("Pantalon Deporte").price(45.00).brand(brand1).build());
        var product3 = productRepo.save(Product.builder().title("Zapatillas Run").price(110.00).brand(brand1).build());
        var product4 = productRepo.save(Product.builder().title("Calzetines Run").price(10.00).brand(brand1).build());

    }
}
