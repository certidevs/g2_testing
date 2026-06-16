package com.ecommerce.ui;

import com.ecommerce.model.Product;
import com.ecommerce.model.Purchase;
import com.ecommerce.model.Review;
import com.ecommerce.model.User;
import com.ecommerce.model.enums.*;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.PurchaseRepository;
import com.ecommerce.repository.ReviewRepository;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Map;

@ExtendWith(ScreenshotOnFailure.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
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

    Purchase compraConProductos;
    Purchase compraSinProductos;
    Product camiseta;
    Product pantalon;
    Review reviewMal;
    Review reviewOK;
    User user;
    User admin;

    @BeforeEach
    void setUp() {

        reviewRepository.deleteAll();
        productRepository.deleteAll();
        purchaseRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(User.builder()
                .username("user")
                .email("user@gmail.com")
                .password(passwordEncoder.encode("user"))
                .role(Role.ROLE_CUSTOMER)
                .build());

        admin = userRepository.save(User.builder()
                .username("admin")
                .email("admin@gmail.com")
                .password(passwordEncoder.encode("admin"))
                .role(Role.ROLE_ADMIN)
                .build());

        compraConProductos = purchaseRepository.save(Purchase.builder()
                .user(user)
                .purchaseStatus(PurchaseStatus.INITIATED)
                .shippingMode(ShippingMode.STANDARD)
                .shippingStatus(ShippingStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .processStatus(ProcessStatus.PROCESSING)
                .totalPrice(100.0)
                .build());

        compraSinProductos = purchaseRepository.save(Purchase.builder()
                .creationDate(LocalDateTime.of(2020, Month.MAY, 30, 18, 45))
                .user(user)
                .purchaseStatus(PurchaseStatus.INITIATED)
                .shippingMode(ShippingMode.STANDARD)
                .shippingStatus(ShippingStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .processStatus(ProcessStatus.ON_HOLD)
                .totalPrice(0.0)
                .build());

        camiseta = productRepository.save(Product.builder()
                .title("Camiseta")
                .shortDescription("Camiseta de algodón")
                .isbn("1234567890")
                .price(20.0)
                .stock(100)
                .build());

        pantalon = productRepository.save(Product.builder()
                .title("Pantalón")
                .shortDescription("Pantalón vaquero")
                .isbn("0987654321")
                .price(40.0)
                .stock(50)
                .build());

        reviewMal = reviewRepository.save(Review.builder()
                .title("Fatal")
                .rating(1)
                .product(pantalon)
                .message("Fatal")
                .creationDate(LocalDateTime.now().minusDays(1))
                .build());

        reviewOK = reviewRepository.save(Review.builder()
                .title("excelente pizza")
                .rating(5)
                .product(camiseta)
                .message("ok")
                .creationDate(LocalDateTime.now().minusDays(1))
                .build());


        baseUrl = "http://localhost:" + port + "/";

        boolean ci = System.getenv("CI") != null;

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--window-size=1920,1080");
        chromeOptions.addArguments("--lang=es-ES");
        chromeOptions.setExperimentalOption("prefs", Map.of("intl.accept_languages", "es-ES"));
        chromeOptions.addArguments("--force-device-scale-factor=1", "--start-maximized");
        if (ci) {
            chromeOptions.addArguments(
                    "--headless=new",
                    "--no-sandbox",
                    "--disable-gpu",
                    "--disable-dev-shm-usage"
            );
        }

        driver = new ChromeDriver(chromeOptions);
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

        wait.until(driver -> !driver.getCurrentUrl().contains("/login"));
    }

    void open(String path) {
        driver.get(baseUrl + path);
    }

    void type(By locator, String value) {
        var element = wait.until(driver -> driver.findElement(locator));

        element.clear();
        element.sendKeys(value);
    }

    void click(By locator) {
        var element = wait.until(driver -> {
            var foundElement = driver.findElement(locator);
            return foundElement.isDisplayed() && foundElement.isEnabled()
                    ? foundElement
                    : null;
        });

        element.click();
    }
    void check(By locator, boolean shouldBeChecked) {
        var element = wait.until(driver -> driver.findElement(locator));
        if (element.isSelected() != shouldBeChecked) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    void waitUntilPageContains(String text) {
        wait.until(driver -> driver.getPageSource().contains(text));
    }

    void setInputValue(By locator, String value) {
        var element = wait.until(driver -> driver.findElement(locator));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];",
                element,
                value
        );
    }

    void submitForm(By formLocator) {
        var form = wait.until(driver -> driver.findElement(formLocator));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'}); arguments[0].requestSubmit();",
                form
        );
    }
}