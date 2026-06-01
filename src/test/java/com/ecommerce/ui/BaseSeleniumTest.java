package com.ecommerce.ui;

import com.ecommerce.model.Product;
import com.ecommerce.model.Purchase;
import com.ecommerce.model.Review;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.AddressType;
import com.ecommerce.model.enums.Role;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.PurchaseRepository;
import com.ecommerce.repository.ReviewRepository;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseSeleniumTest {

    @LocalServerPort
    int port;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    PurchaseRepository purchaseRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    String baseUrl;
    WebDriver driver;
    WebDriverWait wait;

    Restaurant pizzeria; // con platos
    Restaurant taberna; // sin platos
    Dish pizza;
    Dish tiramisu;
    Review pizzeriaOK;
    Review pizzeriaMal;
    Review pizzaOK;
    User user, admin;

    @BeforeEach
    void setUp() {
        // crear datos demo
        reviewRepo.deleteAll();
        dishRepo.deleteAll();
        restaurantRepo.deleteAll();
        userRepo.deleteAll();

        user = userRepo.save(User.builder()
                .username("user").email("user@gmail.com").password(passwordEncoder.encode("user")).role(Role.ROLE_USER)
                .build());

        admin = userRepo.save(User.builder()
                .username("admin").email("admin@gmail.com").password(passwordEncoder.encode("admin")).role(Role.ROLE_ADMIN)
                .build());

        pizzeria = restaurantRepo.save(Restaurant.builder().name("Pizzeria Luigi").averagePrice(10.0).active(true).description("Masa artesanal").build());
        taberna = restaurantRepo.save(Restaurant.builder().name("Taberna").averagePrice(20.0).active(false).build());
        pizza = dishRepo.save(Dish.builder().name("Pizza 4 Quesos").price(12d).type(DishType.MAIN_COURSE).description("pizza bien").restaurant(pizzeria).build());
        tiramisu = dishRepo.save(Dish.builder().name("Tiramisú Café").price(3d).type(DishType.DESSERT).description("fetén").restaurant(pizzeria).build());
        pizzeriaOK = reviewRepo.save(Review.builder().title("Pectacular").rating(5).restaurant(pizzeria).content("Asombroso").build());
        pizzeriaMal = reviewRepo.save(Review.builder().title("Fatal").rating(1).restaurant(pizzeria).content("Nada bien").creationDate(LocalDateTime.now().minusDays(1)).build());
        pizzaOK = reviewRepo.save(Review.builder().title("excelente pizza").rating(5).dish(pizza).content("ok").creationDate(LocalDateTime.now().minusDays(1)).build());

        // inicializar y configuración de driver
        baseUrl = "http://localhost:" + port + "/";
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        // subimos el timeout para operaciones como login y procesamiento de formularios
        wait = new WebDriverWait(driver, Duration.ofSeconds(30L));
    }
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }


    void loginAdmin() {
        login("admin", "admin");
    }
    void loginUser() {
        login("user", "user");
    }
    void login(String username, String password) {
        driver.get(baseUrl + "login");
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(driver -> driver.getCurrentUrl().equals(baseUrl + "restaurants"));
    }
}
