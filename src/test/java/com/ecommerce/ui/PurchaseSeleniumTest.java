package com.ecommerce.ui;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class PurchaseSeleniumTest extends BaseSeleniumTest {

    // Verifica la vista de la lista de compras
    @Test
    void purchaseList () {

        loginUser();

        // Ir a compras
        driver.get(baseUrl + "purchases");

        // Título de la página
        assertEquals("GESTIÓN DE PEDIDOS", driver.findElement(By.id("titlePurchase")).getText());

        // Mensaje de bienvenida + nombre de usuario
        assertTrue(driver.findElement(By.id("welcomeMessage")).getText().contains("¡Hola de nuevo, " + user.getUsername() + "!"));

        // Botón de crear compra
        assertTrue(driver.findElement(By.id("createPurchaseBtn")).isDisplayed());
        assertTrue(driver.findElement(By.id("createPurchaseBtn")).getAttribute("href").endsWith("/purchases/new"));

        // Compras registradas
        assertTrue(driver.findElement(By.id("purchasesRegistered")).getText().contains("Compras registradas"));
        assertEquals(purchaseRepository.findAll().size(), Integer.parseInt(driver.findElement(By.id("numPurchasesRegistered")).getText()));

        // Correo
        String correoTitulo = driver.findElement(By.id("emailTitle")).getText();
        assertEquals("Correo", correoTitulo);
        String correoRegistrado = driver.findElement(By.id("email")).getText();
        assertEquals("user@gmail.com", correoRegistrado);

        // Listado de las compras
        assertFalse(driver.findElements(By.id("cardPurchasesContaining")).isEmpty());

        // Header del listado de las compras
        assertTrue(driver.findElement(By.id("headerPurchasesContaining")).getText().contains("COMPRA"));
        assertTrue(driver.findElement(By.id("headerPurchasesContaining")).getText().contains("CREACIÓN"));
        assertTrue(driver.findElement(By.id("headerPurchasesContaining")).getText().contains("ENVÍO"));
        assertTrue(driver.findElement(By.id("headerPurchasesContaining")).getText().contains("ESTADO"));
        assertTrue(driver.findElement(By.id("headerPurchasesContaining")).getText().contains("TOTAL"));
        assertTrue(driver.findElement(By.id("headerPurchasesContaining")).getText().contains("FINALIZACIÓN"));
        assertTrue(driver.findElement(By.id("headerPurchasesContaining")).getText().contains("ACCIONES"));

        // Id de la compra
        assertTrue(driver.findElement(By.id("purchaseId-" + compraConProductos.getId())).getText().contains(compraConProductos.getId().toString()));

        // Fecha de la creación de la compra
        assertTrue(driver.findElement(By.id("creationDate-" + compraConProductos.getId())).getText().contains(compraConProductos.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));

        // Modo de envío
        assertTrue(driver.findElement(By.id("shippingMode-" + compraConProductos.getId())).getText().contains(compraConProductos.getShippingMode().toString()));

        // Estado de la compra
        assertTrue(driver.findElement(By.id("purchaseStatus-" + compraConProductos.getId())).getText().contains("Iniciada"));

        // Precio total de la compra
        assertTrue(driver.findElement(By.id("totalPurchase-" + compraConProductos.getId())).getText().contains("100,00 €"));

        // Fecha de finalización de la compra
        assertTrue(driver.findElement(By.id("finishedDate-" + compraConProductos.getId())).getText().contains("Pendiente"));

        // Acciones de la tabla del listado de las compras


        // Detalle de la compra
        assertEquals(driver.findElement(By.id("detailPurchaseBtn-" + compraConProductos.getId())).getAttribute("href"), baseUrl + "purchases/" + compraConProductos.getId());

        // Carrito de la compra
        assertEquals(driver.findElement(By.id("cartPurchaseBtn-" + compraConProductos.getId())).getAttribute("href"), baseUrl + "purchases/" + compraConProductos.getId() + "/cart");

        // Borrar la compra (comprobar que no está porque somos usuarios y no podemos)
         assertThrows(NoSuchElementException.class,
                 () -> driver.findElement(By.id("deletePurchaseBtn-" + compraConProductos.getId())));

        // Entrar como admin y comprobar el boton delete :v
    }
/*
    // Verifica la vista del detalle de la compra
    @Test
    void purchaseDetail () {
        
        driver.get(baseUrl + "purchases/" +  compraConProductos.getId());

        // info purchase
        assertEquals(compraConProductos.getCreationDate().toString(), driver.findElement(By.tagName("h1")).getText());
        assertEquals(compraConProductos.getPurchaseStatus().toString(), driver.findElement(By.id("purchaseStatus")).getText());
        assertTrue(driver.findElement(By.id("shippingMode")).getText().contains(compraConProductos.getShippingMode().toString()));
        assertTrue(driver.findElement(By.id("shippingStatus")).getText().contains(compraConProductos.getShippingStatus().toString()));
        assertTrue(driver.findElement(By.id("paymentStatus")).getText().contains(compraConProductos.getPaymentStatus().toString()));
        assertTrue(driver.findElement(By.id("processStatus")).getText().contains(compraConProductos.getProcessStatus().toString()));
        assertTrue(driver.findElement(By.id("totalPrice")).getText().contains(compraConProductos.getTotalPrice().toString()));


        // platos
        List<WebElement> dishes = driver.findElements(By.cssSelector("#dishesTable tbody tr"));
        assertTrue(dishes.size() >= 2);
        assertTrue(dishes.getFirst().getText().contains(camiseta.getTitle()));
        assertTrue(dishes.get(1).getText().contains(pantalon.getTitle()));

        // reviews
        List<WebElement> reviews = driver.findElements(By.cssSelector("#reviewsGrid .card"));
        assertTrue(reviews.size() >= 2);
        WebElement firstReview = reviews.getFirst();
        assertEquals(productOK.getTitle(), firstReview.findElement(By.tagName("h5")).getText());
        assertEquals(productOK.getTitle(), firstReview.findElement(By.cssSelector(".card-title")).getText());
        WebElement secondReview = reviews.get(1);
        assertEquals(productMal.getTitle(), secondReview.findElement(By.tagName("h5")).getText());
        assertEquals(productMal.getContent(), secondReview.findElement(By.cssSelector(".card-text")).getText());
        assertEquals("1/5", secondReview.findElement(By.className("review-rating")).getText());
    }

    // Verifica la vista del formulario de la compra
    @Test
    void purchaseForm(){
        loginAdmin();
        driver.get(baseUrl + "purchases/new");
        driver.findElement(By.id("name")).sendKeys("restaurantes");
        driver.findElement(By.id("averagePrice")).sendKeys("20");
        // driver.findElement(By.id("active")).click(); // ya viene marcado por defecto
        driver.findElement(By.id("description")).sendKeys("descripcion de restaurante");
        driver.findElement(By.id("date")).sendKeys("02/06/2027");
        driver.findElement(By.id("city")).sendKeys("Madrid");
//        driver.findElement(By.id("foodType")).sendKeys("SPANISH");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(driver -> driver.getCurrentUrl().equals(baseUrl + "restaurants"));
        assertEquals(baseUrl + "restaurants", driver.getCurrentUrl());


    }
*/
    // Verifica el proceso de creación de una compra
    @Disabled
@Test
void startPurchase() {
    loginUser();

    driver.get(baseUrl + "products/" + camiseta.getId());

    WebElement addToCartLink = wait.until(ExpectedConditions.elementToBeClickable(
            By.linkText("AÑADIR AL CARRITO")
    ));
    addToCartLink.click();

    wait.until(ExpectedConditions.urlContains("/purchases/"));

    WebElement addQuantityButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.id("addBtn-" + camiseta.getId())
    ));
    addQuantityButton.click();

    wait.until(ExpectedConditions.elementToBeClickable(
            By.id("lessBtn-" + camiseta.getId())
    ));

    WebElement lessQuantityButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.id("lessBtn-" + camiseta.getId())
    ));
    lessQuantityButton.click();

    WebElement numPeopleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("numPeople")
    ));
    numPeopleInput.clear();
    numPeopleInput.sendKeys("2");

    WebElement userSuggestionsInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("userSuggestions")
    ));
    userSuggestionsInput.clear();
    userSuggestionsInput.sendKeys("al fondo a la derecha");

    WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button[type='submit']")
    ));
    submitButton.click();

    WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.tagName("h1")
    ));

    assertTrue(title.getText().contains("Pedido #"));
}

    // UpdatePurchase | Add Product

    // UpdatePurchase | Remove Product

    // FinishPurchase
}