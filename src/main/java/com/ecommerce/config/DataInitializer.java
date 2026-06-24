package com.ecommerce.config;

import com.ecommerce.model.*;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.*;
import com.ecommerce.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception{
        System.out.println("HOLA DESDE DATA INITIALIZER");
        //if (productRepo.count() > 0) return;

        // -------- USUARIOS --------
        var user1 = User.builder()
                .name("User 1")
                .username("user1")
                .lastName("Last Name 1")
                .email("user1@gmail.com")
                .phone("123456789")
                .password(passwordEncoder.encode("Password1*"))
                .birthday(LocalDateTime.of(1990, Month.JANUARY, 1, 0, 0))
                .gender(Gender.MALE)
                .role(Role.ROLE_CUSTOMER)
                .build();

        var user2 = User.builder()
                .name("User 2")
                .username("user2")
                .lastName("Last Name 2")
                .email("user2@gmail.com")
                .phone("987654321")
                .password(passwordEncoder.encode("Password2*"))
                .birthday(LocalDateTime.of(1995, Month.JUNE, 15, 0, 0))
                .gender(Gender.FEMALE)
                .role(Role.ROLE_CUSTOMER)
                .build();

        var adminUser = User.builder()
                .name("Admin")
                .username("admin")
                .lastName("G2")
                .email("admin@g2store.com")
                .phone("600000000")
                .password(passwordEncoder.encode("Admin123*"))
                .birthday(LocalDateTime.of(1988, Month.JANUARY, 10, 0, 0))
                .gender(Gender.MALE)
                .role(Role.ROLE_ADMIN)
                .build();

        userRepo.saveAll(List.of(user1, user2, adminUser));

        // -------- DIRECCIONES --------
        var address1 = Address.builder()
                .street("Calle Mayor")
                .number("123")
                .city("Madrid")
                .state("Madrid")
                .zipCode("28001")
                .country("España")
                .addressType(AddressType.BILLING)
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
                .addressType(AddressType.SHIPPING)
                .user(user2)
                .build();

        addressRepo.saveAll(List.of(address1, address2, address3, address4));

        // -------- MARCAS --------

        var brand1 = brandRepo.save(Brand.builder()
                .name("Nike")
                .nif("123456789")
                .country("US")
                .website("https://www.nike.com")
                .logo("https://i.imgur.com/sNeRWzU.png")
                .active(true)
                .build());

        var brand2 = brandRepo.save(Brand.builder()
                .name("Apple")
                .nif("223456789")
                .country("US")
                .website("https://www.apple.com")
                .logo("https://upload.wikimedia.org/wikipedia/commons/f/fa/Apple_logo_black.svg")
                .active(true)
                .build());

        var brand3 = brandRepo.save(Brand.builder()
                .name("Pandora")
                .nif("323456789")
                .country("DK")
                .website("https://www.pandora.net")
                .logo("https://upload.wikimedia.org/wikipedia/commons/2/25/Pandora_Logo.svg")
                .active(true)
                .build());

        var brand4 = brandRepo.save(Brand.builder()
                .name("Decathlon")
                .nif("423456789")
                .country("FR")
                .website("https://www.decathlon.es")
                .logo("https://upload.wikimedia.org/wikipedia/commons/c/c4/Decathlon_Logo.svg")
                .active(true)
                .build());

        var brand5 = brandRepo.save(Brand.builder()
                .name("LEGO")
                .nif("523456789")
                .country("DK")
                .website("https://www.lego.com")
                .logo("https://upload.wikimedia.org/wikipedia/commons/2/24/LEGO_logo.svg")
                .active(true)
                .build());

        var brand6 = brandRepo.save(Brand.builder()
                .name("IKEA")
                .nif("623456789")
                .country("SE")
                .website("https://www.ikea.com")
                .logo("https://upload.wikimedia.org/wikipedia/commons/c/c5/Ikea_logo.svg")
                .active(true)
                .build());

        // -------- CATEGORÍAS INICIALES (SI NO EXISTEN) --------
        Category ropaRoot = categoryRepo.findBySlug("ropa")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Ropa")
                        .slug("ropa")
                        .description("Ropa y prendas deportivas")
                        .imageUrl("https://armariosalcala.com/wp-content/uploads/551ce816214c7ed15c93c3d195840fbe.jpg")
                        .active(true)
                        .parent(null)
                        .build()));
        Category electronicaRoot = categoryRepo.findBySlug("electronica")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Informática")
                        .slug("informatica")
                        .description("Informática y tecnologías")
                        .imageUrl("https://media.adeo.com/mkp/54f2844efecc96ca9d51b1bc00d19a46/media.jpg?width=3000&height=3000&format=jpg&quality=80&fit=bounds")
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
                            .imageUrl("https://static2.goldengoose.com/public/Style/ECOMM/GMP01220.P000638-10363.jpg?im=Resize=(1200)")
                            .active(true)
                            .parent(ropaRoot)
                            .build());
                });

        Category pantalones = categoryRepo.findBySlug("pantalones")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Pantalones")
                        .slug("pantalones")
                        .description("Pantalones deportivos y de entrenamiento")
                        .imageUrl("https://www.masuniformes.com/cdnassets/ADV210899-NEGRO-1_l.jpg")
                        .active(true)
                        .parent(ropaRoot)
                        .build()));

        Category calzado = categoryRepo.findBySlug("calzado")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Calzado")
                        .slug("calzado")
                        .description("Zapatillas y calzado deportivo")
                        .imageUrl("https://media-rockport.fra1.digitaloceanspaces.com/media-rockport/product/images/019b6ff9-8bce-706f-9012-7d4868c79897_CHARLESROADPLAINTOE-JBL61-RZ.jpg")
                        .active(true)
                        .parent(ropaRoot)
                        .build()));
        Category moviles = categoryRepo.findBySlug("moviles")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Móviles")
                        .slug("movil")
                        .description("Móviles y tablets")
                        .imageUrl("https://dam.elcorteingles.es/producto/www-001094612301195-00.jpg?impolicy=frontWeb&width=1200&shape=square")
                        .active(true)
                        .parent(electronicaRoot)
                        .build()));
        Category portatil = categoryRepo.findBySlug("portatil")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Portátil")
                        .slug("portatil")
                        .description("Portátiles")
                        .imageUrl("https://img.pccomponentes.com/pcblog/104/mejores-portatiles-ligeros.jpg")
                        .active(true)
                        .parent(electronicaRoot)
                        .build()));

        Category joyeriaRoot = categoryRepo.findBySlug("joyeria")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Joyería")
                        .slug("joyeria")
                        .description("Joyería elegante para regalos y ocasiones especiales")
                        .imageUrl("https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?auto=format&fit=crop&w=1200&q=80")
                        .active(true)
                        .parent(null)
                        .build()));

        Category deporteRoot = categoryRepo.findBySlug("deporte")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Deporte")
                        .slug("deporte")
                        .description("Material deportivo para entrenar en casa, gimnasio o exterior")
                        .imageUrl("https://images.unsplash.com/photo-1517836357463-d25dfeac3438?auto=format&fit=crop&w=1200&q=80")
                        .active(true)
                        .parent(null)
                        .build()));

        Category juguetesRoot = categoryRepo.findBySlug("juguetes")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Juguetes")
                        .slug("juguetes")
                        .description("Juguetes educativos, creativos y familiares")
                        .imageUrl("https://images.unsplash.com/photo-1558060370-d644479cb6f7?auto=format&fit=crop&w=1200&q=80")
                        .active(true)
                        .parent(null)
                        .build()));

        Category hogarRoot = categoryRepo.findBySlug("hogar")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Hogar")
                        .slug("hogar")
                        .description("Decoración, organización y accesorios para casa")
                        .imageUrl("https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=1200&q=80")
                        .active(true)
                        .parent(null)
                        .build()));

        Category collares = categoryRepo.findBySlug("collares")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Collares")
                        .slug("collares")
                        .description("Collares y cadenas para looks diarios o eventos")
                        .imageUrl("https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&w=1200&q=80")
                        .active(true)
                        .parent(joyeriaRoot)
                        .build()));

        Category relojes = categoryRepo.findBySlug("relojes")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Relojes")
                        .slug("relojes")
                        .description("Relojes modernos y accesorios premium")
                        .imageUrl("https://images.unsplash.com/photo-1523170335258-f5ed11844a49?auto=format&fit=crop&w=1200&q=80")
                        .active(true)
                        .parent(joyeriaRoot)
                        .build()));

        Category fitness = categoryRepo.findBySlug("fitness")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Fitness")
                        .slug("fitness")
                        .description("Accesorios para entrenamientos de fuerza y movilidad")
                        .imageUrl("https://images.unsplash.com/photo-1599058917212-d750089bc07e?auto=format&fit=crop&w=1200&q=80")
                        .active(true)
                        .parent(deporteRoot)
                        .build()));

        Category ciclismo = categoryRepo.findBySlug("ciclismo")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Ciclismo")
                        .slug("ciclismo")
                        .description("Productos para rutas urbanas y salidas deportivas")
                        .imageUrl("https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&w=1200&q=80")
                        .active(true)
                        .parent(deporteRoot)
                        .build()));

        Category construccion = categoryRepo.findBySlug("construccion")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Construcción")
                        .slug("construccion")
                        .description("Sets de construcción y juegos creativos")
                        .imageUrl("https://images.unsplash.com/photo-1587654780291-39c9404d746b?auto=format&fit=crop&w=1200&q=80")
                        .active(true)
                        .parent(juguetesRoot)
                        .build()));

        Category educativos = categoryRepo.findBySlug("educativos")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Educativos")
                        .slug("educativos")
                        .description("Juegos para aprender, experimentar y resolver retos")
                        .imageUrl("https://images.unsplash.com/photo-1503676260728-1c00da094a0b?auto=format&fit=crop&w=1200&q=80")
                        .active(true)
                        .parent(juguetesRoot)
                        .build()));

        Category decoracion = categoryRepo.findBySlug("decoracion")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Decoración")
                        .slug("decoracion")
                        .description("Piezas decorativas para dar personalidad al hogar")
                        .imageUrl("https://images.unsplash.com/photo-1513519245088-0e12902e5a38?auto=format&fit=crop&w=1200&q=80")
                        .active(true)
                        .parent(hogarRoot)
                        .build()));

        Category cocina = categoryRepo.findBySlug("cocina")
                .orElseGet(() -> categoryRepo.save(Category.builder()
                        .name("Cocina")
                        .slug("cocina")
                        .description("Utensilios y organizadores para cocinar mejor")
                        .imageUrl("https://images.unsplash.com/photo-1556911220-bff31c812dba?auto=format&fit=crop&w=1200&q=80")
                        .active(true)
                        .parent(hogarRoot)
                        .build()));


        // -------- PRODUCTOS --------
        var product1 = productRepo.save(Product.builder()
                .title("Camiseta Blanca")
                .subcategory(camisetas)
                .longDescription("Esta camiseta blanca de Nike es perfecta para cualquier ocasión. Confeccionada con algodón de alta calidad.")
                .imageUrl("https://static.nike.com/a/images/t_web_pw_592_v2/f_auto/0b73e9b3-1bba-4a6b-8d4b-ecc9d2a473c8/M+NSW+TEE+M90+FW+MBR+CNCT+HO25.png")
                .subcategory(camisetas)
                .stock(25)
               .price(30.00)
                        .discountPercentage(40)
                .brand(brand1).build());

        var product2 = productRepo.save(Product.builder()
                .title("Pantalón Deporte")
                        .subcategory(pantalones)
                .imageUrl("https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcR6ErZKh_-lTTwptKNoNRmIE7wEd-IIm_GQsU3c5SjQxDU2kj0678uu_vFbVX2QgNrLozE4rcSjki-WGpfYw83KzWSmPjNus4dd5V4hNJJb-SSn-lCSMw5Ez6kRMYbLGeh3rUoMWCpx&usqp=CAc")
                .subcategory(pantalones)
                .stock(40)
                .price(45.00)
                        .discountPercentage(20)
                .brand(brand1).build());

        var product3 = productRepo.save(Product.builder()
                .title("Zapatillas Run")
                .subcategory(calzado)
                .imageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQmqrHlyvYmEn1ghVn1P9djF-vH5PCjlwOhgw&s")
                .subcategory(calzado)
                .stock(15)
                .price(110.00).brand(brand1).build());

        var product4 = productRepo.save(Product.builder()
                .title("Calzetines Run")
                .subcategory(calzado)
                .imageUrl("https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcRLO_S61-_IXWE6l0J4WiBdUcnglzV7e9ZBYwEPKLQ2whre7AdR-7K8D0MxyEYo-EqsivQma9grEEiWiZEnvPx7W0Q8z5mnyy0oLR7HsXAQQnEtNe8CsRLVdbSDUD3xxZngN83U6efJ&usqp=CAc")
                        .stock(100)
                .price(10.00).brand(brand1).build());
        var product5 = productRepo.save(Product.builder()
                .title("Iphone 15")
                .subcategory(moviles)
                .longDescription("iphone 15 128g")
                .imageUrl("https://imgs.search.brave.com/ESQDLqeCxUyCOQQrWLBxQsuDBwmLcHYDjL-jh_-U7iY/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9pbWFn/ZXMudW5zcGxhc2gu/Y29tL3Bob3RvLTE3/MDQzODA4OTUzMTYt/Y2FhMmU0ZDY4YTdl/P2ZtPWpwZyZxPTYw/Jnc9MzAwMCZhdXRv/PWZvcm1hdCZmaXQ9/Y3JvcCZpeGxpYj1y/Yi00LjEuMCZpeGlk/PU0zd3hNakEzZkRC/OE1IeHpaV0Z5WTJo/OE4zeDhhWEJvYjI1/bEpUSXdNVFY4Wlc1/OE1IeDhNSHg4ZkRB/PQ")
                        .stock(20)
                .price(1200.00).brand(brand2).build());
        var product6 = productRepo.save(Product.builder()
                .title("Macbook Pro 16")
                .subcategory(portatil)
                .longDescription("Macbook Pro 16 pulgadas con chip M1 Pro, 16GB RAM, 512GB SSD")
                        .imageUrl("https://imgs.search.brave.com/sciRFp0EHNluo57mCivZlwX-wnfY53SR_AfdnAIF4PE/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly93d3cu/Y25ldC5jb20vYS9p/bWcvcmVzaXplLzJm/OTg0ZTczODVjYWJl/NjRlYmZhZWFiZjI1/ZjYzNDFjZmUzZDYz/MWEvaHViLzIwMTkv/MTEvMTIvODRlMTE1/OWMtYjhjYi00MzQ5/LTllM2ItM2MzN2Nj/Nzg5NDVmLzM2LW1h/Y2Jvb2stcHJvLTE2/LWluY2guanBnP2F1/dG89d2VicCZ3aWR0/aD0xMjAw")
                        .stock(10)
                .price(2500.00).brand(brand2).build());

        var product7 = productRepo.save(Product.builder()
                .title("Collar Corazón Plata")
                .subcategory(collares)
                .shortDescription("Collar de plata con colgante en forma de corazón.")
                .longDescription("Collar de plata de ley con acabado brillante, ideal para regalo. Incluye cadena ajustable y cierre de seguridad.")
                .imageUrl("https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?auto=format&fit=crop&w=1200&q=80")
                .stock(35)
                .price(59.90)
                .discountPercentage(15)
                .brand(brand3).build());

        var product8 = productRepo.save(Product.builder()
                .title("Pulsera Charms Elegance")
                .subcategory(collares)
                .shortDescription("Pulsera personalizable con charms decorativos.")
                .longDescription("Pulsera ajustable con charms intercambiables, pensada para combinar con estilos casuales o eventos especiales.")
                .imageUrl("https://images.unsplash.com/photo-1611591437281-460bfbe1220a?auto=format&fit=crop&w=1200&q=80")
                .stock(28)
                .price(49.90)
                .brand(brand3).build());

        var product9 = productRepo.save(Product.builder()
                .title("Reloj Minimal Black")
                .subcategory(relojes)
                .shortDescription("Reloj analógico negro con esfera minimalista.")
                .longDescription("Reloj elegante con correa de acero inoxidable, resistente al uso diario y fácil de combinar.")
                .imageUrl("https://images.unsplash.com/photo-1523170335258-f5ed11844a49?auto=format&fit=crop&w=1200&q=80")
                .stock(18)
                .price(89.90)
                .discountPercentage(10)
                .brand(brand3).build());

        var product10 = productRepo.save(Product.builder()
                .title("Esterilla Yoga Pro")
                .subcategory(fitness)
                .shortDescription("Esterilla antideslizante para yoga, pilates y estiramientos.")
                .longDescription("Esterilla acolchada con superficie antideslizante, fácil de transportar y perfecta para entrenar en casa.")
                .imageUrl("https://images.unsplash.com/photo-1599901860904-17e6ed7083a0?auto=format&fit=crop&w=1200&q=80")
                .stock(50)
                .price(24.99)
                .brand(brand4).build());

        var product11 = productRepo.save(Product.builder()
                .title("Set Mancuernas 10kg")
                .subcategory(fitness)
                .shortDescription("Set de mancuernas ajustables para entrenamiento funcional.")
                .longDescription("Pack de mancuernas con discos intercambiables, agarre cómodo y cierres seguros para rutinas de fuerza.")
                .imageUrl("https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?auto=format&fit=crop&w=1200&q=80")
                .stock(22)
                .price(39.95)
                .discountPercentage(12)
                .brand(brand4).build());

        var product12 = productRepo.save(Product.builder()
                .title("Casco Ciclismo Aero")
                .subcategory(ciclismo)
                .shortDescription("Casco ligero con ventilación para rutas largas.")
                .longDescription("Casco de ciclismo con ajuste trasero, interior acolchado y diseño ventilado para mayor comodidad.")
                .imageUrl("https://images.unsplash.com/photo-1507035895480-2b3156c31fc8?auto=format&fit=crop&w=1200&q=80")
                .stock(16)
                .price(54.99)
                .brand(brand4).build());

        var product13 = productRepo.save(Product.builder()
                .title("Balón Fútbol Training")
                .subcategory(fitness)
                .shortDescription("Balón resistente para entrenamientos y partidos casuales.")
                .longDescription("Balón de fútbol con cubierta resistente y buen tacto, preparado para césped artificial y pistas exteriores.")
                .imageUrl("https://images.unsplash.com/photo-1614632537190-23e4146777db?auto=format&fit=crop&w=1200&q=80")
                .stock(60)
                .price(19.99)
                .discountPercentage(5)
                .brand(brand4).build());

        var product14 = productRepo.save(Product.builder()
                .title("LEGO Ciudad Creativa")
                .subcategory(construccion)
                .shortDescription("Set de construcción con edificios, vehículos y personajes.")
                .longDescription("Caja de construcción para crear una pequeña ciudad con piezas variadas, ideal para desarrollar creatividad y paciencia.")
                .imageUrl("https://images.unsplash.com/photo-1587654780291-39c9404d746b?auto=format&fit=crop&w=1200&q=80")
                .stock(30)
                .price(69.99)
                .discountPercentage(8)
                .brand(brand5).build());

        var product15 = productRepo.save(Product.builder()
                .title("Puzzle Mapa del Mundo")
                .subcategory(educativos)
                .shortDescription("Puzzle educativo para aprender geografía jugando.")
                .longDescription("Puzzle de piezas resistentes con mapa del mundo ilustrado, perfecto para aprender países, continentes y océanos.")
                .imageUrl("https://images.unsplash.com/photo-1606092195730-5d7b9af1efc5?auto=format&fit=crop&w=1200&q=80")
                .stock(45)
                .price(16.50)
                .brand(brand5).build());

        var product16 = productRepo.save(Product.builder()
                .title("Kit Ciencia Junior")
                .subcategory(educativos)
                .shortDescription("Juego educativo con experimentos sencillos y seguros.")
                .longDescription("Kit de ciencia para descubrir conceptos básicos con actividades guiadas, materiales incluidos e instrucciones paso a paso.")
                .imageUrl("https://images.unsplash.com/photo-1532094349884-543bc11b234d?auto=format&fit=crop&w=1200&q=80")
                .stock(24)
                .price(29.90)
                .discountPercentage(10)
                .brand(brand5).build());

        var product17 = productRepo.save(Product.builder()
                .title("Lámpara Mesa Nordic")
                .subcategory(decoracion)
                .shortDescription("Lámpara decorativa con luz cálida para dormitorio o salón.")
                .longDescription("Lámpara de mesa con diseño nórdico, pantalla textil y luz cálida para crear ambientes acogedores.")
                .imageUrl("https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=1200&q=80")
                .stock(32)
                .price(34.99)
                .brand(brand6).build());

        var product18 = productRepo.save(Product.builder()
                .title("Set Organizadores Bambú")
                .subcategory(cocina)
                .shortDescription("Organizadores de bambú para cajones y encimeras.")
                .longDescription("Set modular de organizadores de bambú para cubiertos, especias y pequeños utensilios de cocina.")
                .imageUrl("https://images.unsplash.com/photo-1556911220-bff31c812dba?auto=format&fit=crop&w=1200&q=80")
                .stock(40)
                .price(22.95)
                .discountPercentage(7)
                .brand(brand6).build());

        var product19 = productRepo.save(Product.builder()
                .title("Manta Sofá Soft")
                .subcategory(decoracion)
                .shortDescription("Manta suave para sofá, cama o rincón de lectura.")
                .longDescription("Manta de tacto suave con textura decorativa, ideal para dar color al salón y mejorar el confort diario.")
                .imageUrl("https://images.unsplash.com/photo-1519710164239-da123dc03ef4?auto=format&fit=crop&w=1200&q=80")
                .stock(27)
                .price(27.99)
                .brand(brand6).build());

        var product20 = productRepo.save(Product.builder()
                .title("Sartén Antiadherente 28cm")
                .subcategory(cocina)
                .shortDescription("Sartén antiadherente para cocina diaria.")
                .longDescription("Sartén de 28 cm con revestimiento antiadherente, mango ergonómico y base apta para varios tipos de cocina.")
                .imageUrl("https://images.unsplash.com/photo-1556910103-1c02745aae4d?auto=format&fit=crop&w=1200&q=80")
                .stock(36)
                .price(31.90)
                .brand(brand6).build());

//        // -------- COMPRAS --------
//        var purchase1 = Purchase.builder()
//                .user(user1)
//                .creationDate(LocalDateTime.of(2026, Month.MARCH, 15, 12, 45))
//                .finishedDate(LocalDateTime.of(2026, Month.APRIL, 28, 17, 30))
//                .purchaseStatus(PurchaseStatus.FINISHED)
//                .paymentStatus(PaymentStatus.PAID)
//                .processStatus(ProcessStatus.COMPLETED)
//                .shippingStatus(ShippingStatus.DELIVERED)
//                .shippingMode(ShippingMode.STANDARD)
//                .totalPrice(50.00)
//                .userComment("Necesito que me lo entreguen por la tarde, a partir de las 17:00, porque trabajo hasta esa hora. Gracias!")
//                .build();
//
//        var purchase2 = Purchase.builder()
//                .user(user2)
//                .creationDate(LocalDateTime.of(2025, Month.JUNE, 10, 18, 35))
//                .finishedDate(LocalDateTime.of(2025, Month.DECEMBER, 25, 16, 15))
//                .purchaseStatus(PurchaseStatus.FINISHED)
//                .paymentStatus(PaymentStatus.PAID)
//                .processStatus(ProcessStatus.COMPLETED)
//                .shippingStatus(ShippingStatus.DELIVERED)
//                .shippingMode(ShippingMode.EXPRESS)
//                .totalPrice(15.45)
//                .userComment("Por favor, entregadlo lo antes posible, es un regalo para el cumpleaños de mi hermano que es el día 1 de julio. Si no es posible la entrega antes del 1 de julio, por favor cancelad la compra. Gracias!")
//                .build();
//
//        var purchase3 = Purchase.builder()
//                .user(user1)
//                .creationDate(LocalDateTime.of(2026, Month.FEBRUARY, 10, 11, 50))
//                .finishedDate(null)
//                .purchaseStatus(PurchaseStatus.INITIATED)
//                .paymentStatus(PaymentStatus.PENDING)
//                .processStatus(ProcessStatus.PENDING)
//                .shippingStatus(ShippingStatus.PENDING)
//                .shippingMode(ShippingMode.PREMIUM)
//                .totalPrice(150.75)
//                .userComment(null)
//                .build();
//
//        var purchase4 = Purchase.builder()
//                .user(user2)
//                .creationDate(LocalDateTime.of(2020, Month.MAY, 30, 8, 30))
//                .finishedDate(null)
//                .purchaseStatus(PurchaseStatus.INACTIVE)
//                .paymentStatus(PaymentStatus.PENDING)
//                .processStatus(ProcessStatus.PENDING)
//                .shippingStatus(ShippingStatus.PENDING)
//                .shippingMode(ShippingMode.STANDARD)
//                .totalPrice(73.00)
//                .userComment(null)
//                .build();
//
//        purchaseRepo.saveAll(List.of(purchase1, purchase2, purchase3, purchase4));
//
//        // -------- LÍNEAS DE COMPRA --------
//        // Líneas de compra asociadas a la compra 1: (el usuario añade 3 productos a la compra 1)
//        purchaseLineRepo.saveAll(List.of(
//                PurchaseLine.builder().quantity(2).product(product1).purchase(purchase1).build(),
//                PurchaseLine.builder().quantity(4).product(product2).purchase(purchase1).build(),
//                PurchaseLine.builder().quantity(1).product(product3).purchase(purchase1).build()
//        ));
//        // Líneas de compra asociadas a la compra 2: el usaurio añade 4 productos a la compra 2)
//        purchaseLineRepo.saveAll(List.of(
//                PurchaseLine.builder().quantity(1).product(product4).purchase(purchase2).build(),
//                PurchaseLine.builder().quantity(4).product(product1).purchase(purchase2).build(),
//                PurchaseLine.builder().quantity(6).product(product2).purchase(purchase2).build(),
//                PurchaseLine.builder().quantity(6).product(product3).purchase(purchase2).build()
//        ));

        // -------- RESEÑAS --------
        var review1 = Review.builder()
                .title("Camiseta de muy buena calidad")
                .message("Estoy muy contento con la compra. El tejido es suave y resistente, y la talla es exacta. La recomiendo totalmente.")
                .rating(5)
                .creationDate(LocalDateTime.of(2026, Month.APRIL, 10, 10, 30))
                .product(product1)
                .user(user1)
                .build();

        var review2 = Review.builder()
                .title("Pantalón correcto pero talla grande")
                .message("El pantalón es cómodo para hacer deporte, pero la talla es bastante grande. Recomendaría pedir una talla menos de lo habitual.")
                .rating(3)
                .creationDate(LocalDateTime.of(2026, Month.MARCH, 20, 15, 0))
                .product(product2)
                .user(user2)
                .build();

        var review3 = Review.builder()
                .title("Zapatillas increíbles para correr")
                .message("Las mejores zapatillas que he tenido. Muy cómodas desde el primer día, sin necesidad de adaptación. El amortiguamiento es excelente.")
                .rating(5)
                .creationDate(LocalDateTime.of(2026, Month.MAY, 1, 8, 0))
                .product(product3)
                .user(user1)
                .build();

        var review4 = Review.builder()
                .title("Calcetines flojos, se deforman rápido")
                .message("Después de unas pocas lavadas los calcetines pierden la forma. No son malos del todo pero esperaba más durabilidad para el precio.")
                .rating(2)
                .creationDate(LocalDateTime.of(2025, Month.DECEMBER, 5, 18, 45))
                .product(product4)
                .user(user2)
                .build();

        var review5 = Review.builder()
                .title("Camiseta muy bonita pero se arruga")
                .message("El diseño es bonito y la calidad del tejido es buena, pero se arruga bastante al lavarla. Hay que plancharla siempre.")
                .rating(4)
                .creationDate(LocalDateTime.of(2026, Month.APRIL, 15, 9, 0))
                .product(product1)
                .user(user2)
                .build();

        var review6 = Review.builder()
                .title("Camiseta decepcionante")
                .message("Esperaba más por el precio. El color se fue a la primera lavada y talla muy pequeño para la talla indicada.")
                .rating(1)
                .creationDate(LocalDateTime.of(2026, Month.MAY, 2, 11, 15))
                .product(product1)
                .user(user1)
                .build();

        var review7 = Review.builder()
                .title("Zapatillas cómodas pero poco duraderas")
                .message("Son muy cómodas al principio pero la suela empezó a despegarse después de dos meses de uso regular.")
                .rating(3)
                .creationDate(LocalDateTime.of(2026, Month.APRIL, 20, 14, 30))
                .product(product3)
                .user(user2)
                .build();

        var review8 = Review.builder()
                .title("Las mejores zapatillas del mercado")
                .message("He probado muchas marcas y estas sin duda son las mejores. Ligeras, transpirables y con un agarre excelente en todo tipo de superficies.")
                .rating(5)
                .creationDate(LocalDateTime.of(2026, Month.MARCH, 10, 16, 0))
                .product(product3)
                .user(user1)
                .build();

        reviewRepo.saveAll(List.of(review1, review2, review3, review4, review5, review6, review7, review8));
    }
}
