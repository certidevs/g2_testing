package com.ecommerce.model;
import com.ecommerce.model.enums.ProductStockStatus;
import jakarta.validation.constraints.NotBlank;
// importado de jakarta.validation para validar que el
// título del producto no esté en blanco
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) //Delegar el ID a la Base de Datos.
    private UUID id;

    @NotBlank //El titulos del producto Obligatorio y no puede estar en blanco
    @Column(nullable = false)//Obliga a poner el Título porque prohìbe guardar datos nulos (vacios)
    private String  title;

    @PositiveOrZero //El precio del producto no puede ser negativo
    private Double price; //Precio del producto

    @Builder.Default //por defecto True
    private boolean available = true; //Indica si el producto está disponible para la venta.

    private String isbn ; // Codigo de barras para cada producto

    @Column(length = 500) //Limitar la longitud de la descripción corta a 500 caracteres
    private String shortDescription; //Descripción corta del producto

    @Column(length = 2000) //Limitar la longitud de la descripción larga a 2000 caracteres
    private String longDescription; //Descripción larga del producto

//    private Integer stock; // Cantidad de productos disponibles en el inventario

//    @Enumerated(EnumType.STRING)
//    private ProductStockStatus stockStatus;
    private int stock ; // Cantidad de productos disponibles en el inventario
    @Column(length = 1000)
     String imageUrl; //URL de la imagen del producto

    // Porcentaje de 0 a 100. Si es 0, no tiene descuento.
    private Integer discountPercentage = 0;

    // metodo para calcular el precio final
    public Double getFinalPrice() {
        if (discountPercentage != null && discountPercentage > 0) {
            return price - (price * discountPercentage / 100.0);
        }
        return price;
    }

    @ToString.Exclude
    @ManyToOne

    private Brand brand;

    @ToString.Exclude
    @ManyToOne
    private Purchase purchase;

    // Relación al nivel de subcategoría
    @ManyToOne//(fetch = FetchType.LAZY)
    @ToString.Exclude

    private Category subcategory;
    // Dentro de Product.java
    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private List<Review> reviews;


    public boolean getActive() {
        return available;
    }
}
