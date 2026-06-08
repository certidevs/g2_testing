package com.ecommerce.ui;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
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
        assertTrue(driver.findElement(By.id("purchaseCreationDate-" + compraConProductos.getId())).getText().contains(compraConProductos.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));

        // Modo de envío
        assertTrue(driver.findElement(By.id("purchaseShippingDate-" + compraConProductos.getId())).getText().contains(compraConProductos.getShippingMode().toString()));

        // Estado de la compra
        assertTrue(driver.findElement(By.id("purchaseStatus-" + compraConProductos.getId())).getText().contains(compraConProductos.getPurchaseStatus().toString()));

        // Precio total de la compra
        assertTrue(driver.findElement(By.id("purchaseTotal-" + compraConProductos.getId())).getText().contains(compraConProductos.getTotalPrice().toString()));

        // Fecha de finalización de la compra
        assertTrue(driver.findElement(By.id("purchaseFinishDate-" + compraConProductos.getId())).getText().contains(compraConProductos.getFinishedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));

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
    @Test
    void startPurchase() {
        loginUser();
        driver.navigate().to(baseUrl + "products/" + camiseta.getId());
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("AÑADIR AL CARRITO")));
        driver.findElement(By.linkText("AÑADIR AL CARRITO")).click();
        wait.until(ExpectedConditions.urlContains("/purchases/purchaseId="));
        driver.findElement(By.id("addBtn-" + camiseta.getId())).click();
        wait.until(ExpectedConditions.urlContains("/purchases/new?purchaseId=" + camiseta.getId()));
        driver.findElement(By.id("lessBtn-" + camiseta.getId())).click();
        wait.until(ExpectedConditions.urlContains("/purchases/new?purchaseId=" + camiseta.getId()));

        driver.findElement(By.id("numPeople")).sendKeys("2");
        driver.findElement(By.id("userSuggestions")).sendKeys("al fondo a la derecha");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/orders/"));
        assertTrue(driver.findElement(By.tagName("h1")).getText().contains("Pedido #"));
    }

    // UpdatePurchase | Add Product

    // UpdatePurchase | Remove Product

    // FinishPurchase
}