package com.ecommerce.ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PurchaseSeleniumTest extends BaseSeleniumTest {

    @Test
    void purchaseList () {

        loginUser();
//        // Login como usuario
//        driver.get(baseUrl + "login");
//
//        String nombreUsuario = "user";
//        login(nombreUsuario, "user");
//
//        wait.until(driver -> !driver.getCurrentUrl().contains("/login"));

        // Ir a compras
        driver.get(baseUrl + "purchases");

        // Título de la página
        String tituloPurchase = driver.findElement(By.id("titlePurchase")).getText();
        assertEquals("GESTIÓN DE PEDIDOS", tituloPurchase);

        // Mensaje de bienvenida + nombre de usuario
        String welcomeMessage = driver.findElement(By.id("welcomeMessage")).getText();
        assertTrue(welcomeMessage.contains("¡Hola de nuevo, " + user.getUsername() + "!"));

        // Botón de crear compra
        WebElement botonCrearCompra = driver.findElement(By.id("createPurchaseBtn"));
        assertTrue(botonCrearCompra.isDisplayed());
        assertTrue(botonCrearCompra.getAttribute("href").endsWith("/purchases/new"));

        // Compras registradas
        String comprasRegistradasTitulo = driver.findElement(By.id("purchasesRegistered")).getText();
        assertTrue(comprasRegistradasTitulo.contains("Compras registradas"));
        WebElement numComprasRegistradas = driver.findElement(By.id("numPurchasesRegistered"));
        assertEquals("2", numComprasRegistradas.getText());

        // Correo
        String correoTitulo = driver.findElement(By.id("emailTitle")).getText();
        assertEquals("Correo", correoTitulo);
        String correoRegistrado = driver.findElement(By.id("email")).getText();
        assertEquals("user@gmail.com", correoRegistrado);

        // Listado de las compras
        List<WebElement> cardPurchasesContaining = driver.findElements(By.id("cardPurchasesContaining"));;
        assertFalse(cardPurchasesContaining.isEmpty());

        String headerTabla = driver.findElement(By.id("headerPurchasesContaining")).getText();
        assertTrue(headerTabla.contains("Compra"));
        assertTrue(headerTabla.contains("Creación"));
        assertTrue(headerTabla.contains("Envío"));
        assertTrue(headerTabla.contains("Estado"));
        assertTrue(headerTabla.contains("Total"));
        assertTrue(headerTabla.contains("Finalización"));
        assertTrue(headerTabla.contains("Acciones"));

        assertEquals(1, cardPurchasesContaining.size());
        WebElement firstPurchase = cardPurchasesContaining.get(0);

        //TODO poner en la vista dentro de los id la variable de manera dinamica para poder hacer correctamente los test con selenium

        // Id de la compra
        /* String purchaseId = firstPurchase.findElement(By.id("purchaseId")).getText();
        assertEquals(purchaseId, compraConProductos.getId().toString()); */

        // Fecha de la creación de la compra
        // [estoy comparando dos formatos diferentes y no se como hacerlo :C]
        // Preguntar si se puede castear variables

        /*

        String creationDate = firstPurchase.findElement(By.id("creationDate")).getText();
        assertEquals(creationDate, compraConProductos.getCreationDate().toString());

        WebElement shippingMode = firstPurchase.findElement(By.id("shippingMode"));
        assertEquals(shippingMode, compraConProductos.getShippingMode().toString());

        WebElement shippingStatus = firstPurchase.findElement(By.id("shippingStatus"));
        assertEquals(shippingStatus, compraConProductos.getShippingStatus().toString());

        WebElement totalPurchase = firstPurchase.findElement(By.id("totalPurchase"));
        assertTrue(totalPurchase.getText().contains(compraConProductos.getTotalPrice().toString()));

        WebElement finishedDate = firstPurchase.findElement(By.id("finishedDate"));
        assertEquals(finishedDate, compraConProductos.getFinishedDate().toString());

         */

        /* WebElement finishedDate = firstPurchase.findElement(By.id("finishedDate" + compraConProductos.getId()));
        assertEquals(finishedDate.getText(), compraConProductos.getFinishedDate().toString()); */

        // Acciones de la tabla del listado de las compras

        // Detalle de la compra
        WebElement botonDetalleCompra = firstPurchase.findElement(By.id("detailPurchaseBtn"));
        assertEquals(botonDetalleCompra.getAttribute("href"), baseUrl + "purchases/" + compraConProductos.getId());

        // Carrito de la compra
        WebElement botonIrAlCarrito = firstPurchase.findElement(By.id("cartPurchaseBtn"));
        assertEquals(botonIrAlCarrito.getAttribute("href"), baseUrl + "purchases/" + compraConProductos.getId() + "/cart");

        // Borrar la compra (comprobar que no está porque somos usuarios y no podemos)

         assertThrows(NoSuchElementException.class,
                 () -> firstPurchase.findElement(By.id("deletePurchaseBtn")));

        // Entrar como admin y comprobar el boton delete :v
    }
/*
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

    // restaurant list filters

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

    // StartPurchase

    // UpdatePurchase | Add Product

    // UpdatePurchase | Remove Product

    // FinishPurchase
}