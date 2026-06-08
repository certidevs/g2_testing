package com.ecommerce.ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductSeleniumTest extends BaseSeleniumTest {


    @Test
    void productList() {
        // Abre el catalogo de productos.
        driver.get(baseUrl + "products");

        // Espera a que aparezca el titulo del catalogo para confirmar que la vista termino de renderizar.
        WebElement catalogTitle = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("catalogo-title")));

        // Verifica que se muestra el titulo que el controlador envia en el modelo.
        assertEquals("NUESTRA TIENDA", catalogTitle.getText());

        // Verifica que los productos creados como tarjetas del catalogo.
        List<WebElement> productCards = driver.findElements(By.cssSelector(".product-card"));
        assertEquals(2, productCards.size());
        assertTrue(driver.getPageSource().contains("Camiseta"));
        assertTrue(driver.getPageSource().contains("Pantalón"));
    }

    // Comprueba que al entrar en el detalle de un producto se muestran sus datos principales.
    @Test
    void productDetailShowsProductInformation() {
        // Abre directamente el detalle de la camiseta
        driver.get(baseUrl + "products/" + camiseta.getId());

        // Espera a que el titulo del detalle sea visible.
        WebElement productTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1.product-title")));

        // Verifica que el detalle pertenece al producto correcto y que se renderizan sus datos.
        assertEquals("Camiseta", productTitle.getText());
        assertTrue(driver.getPageSource().contains("Camiseta de algodón"));
        assertTrue(driver.getPageSource().contains("Añadir al carrito"));
    }

    // Comprueba que el buscador de productos filtra por el texto enviado en la URL.
    @Test
    void searchProductsByTitle() {
        // Busca solo productos que contengan "Camiseta".
        driver.get(baseUrl + "products/search?query=Camiseta");

        // Espera a que cargue la seccion de catalogo.
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("catalogo-title")));

        // La camiseta debe aparecer y el pantalon no, porque no coincide con la busqueda.
        assertTrue(driver.getPageSource().contains("Camiseta"));
        assertFalse(driver.getPageSource().contains("Pantalón"));
        assertEquals(1, driver.findElements(By.cssSelector(".product-card")).size());
    }

    // Comprueba que un administrador puede abrir el formulario para crear productos.
    @Test
    void adminCanOpenCreateProductForm() {
        // Inicia sesion con el usuario administrador
        loginAdmin();

        // Abre la pantalla protegida de creacion de producto.
        driver.get(baseUrl + "products/new");

        // Verifica que el formulario de creacion carga y muestra los campos principales.
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1")));
        assertEquals("Crear nuevo producto", title.getText());
        assertTrue(driver.findElement(By.id("title")).isDisplayed());
        assertTrue(driver.findElement(By.id("price")).isDisplayed());
        assertTrue(driver.findElement(By.id("stock")).isDisplayed());
    }

    // Comprueba que un administrador puede abrir la pantalla de edicion de un producto existente.
    @Test
    void adminCanOpenEditProductForm() {
        // Inicia sesion como administrador para tener permiso sobre /products/edit/{id}.
        loginAdmin();

        // Abre el formulario de edicion de la camiseta.
        driver.get(baseUrl + "products/edit/" + camiseta.getId());

        // Verifica que el formulario carga en modo edicion.
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1")));
        assertEquals("Editar producto", title.getText());

        // Verifica que los campos se rellenan con los datos guardados del producto.
        assertEquals("Camiseta", driver.findElement(By.id("title")).getAttribute("value"));
        assertEquals("20.0", driver.findElement(By.id("price")).getAttribute("value"));
        assertEquals("100", driver.findElement(By.id("stock")).getAttribute("value"));
    }

    // Comprueba que un usuario normal no puede acceder al formulario de creacion de productos.
    @Test
    void customerCannotOpenCreateProductForm() {
        // Inicia sesion como cliente, no como administrador.
        loginUser();

        // Intenta abrir una ruta protegida de administracion de productos.
        driver.get(baseUrl + "products/new");

        // La aplicacion debe mostrar la pagina 403 en lugar del formulario.
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h2")));
        assertTrue(errorMessage.getText().contains("No tienes permisos para acceder a esta sección."));
    }
}
