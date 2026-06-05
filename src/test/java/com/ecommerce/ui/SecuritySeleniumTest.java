package com.ecommerce.ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecuritySeleniumTest extends BaseSeleniumTest{

    // Verifica que un usuario no autenticado sea redigirigido a la página de login
    @Test
    public void anonymousTryAccessProtectedPathRedirectToLogin(){
        driver.get(baseUrl + "purchases");
        wait.until(ExpectedConditions.urlContains("login"));
        loginUser();
        wait.until(ExpectedConditions.urlContains("purchases"));
    }

    // Verifica que un usuario con rol customer no pueda acceder a la página de creación de productos
    @Test
    void userTryAccessAdminPath(){
        loginUser();
        driver.get(baseUrl + "products/new");
        wait.until(driver -> driver.findElement(By.tagName("h2"))
                .getText().contains("No tienes permisos para acceder a esta sección."));
    }

    // Verifica que un usuario con rol admin pueda acceder a la página de creación de productos
    @Test
    void editProduct(){
        loginAdmin();
        driver.get(baseUrl + "products/edit/" + camiseta.getId());

        // Verifica que el valor del nombre del producto aparezca en la ranura de nombre para editar
        WebElement nameInput = driver.findElement(By.id("nameProduct")); // TODO poner el id "nameProduct" a la casilla donde se edita el nombre del producto
        assertEquals("Camiseta", nameInput.getAttribute("value"));
    }

    @Test
    void logout(){
        loginUser();
        driver.findElement(By.id("logoutBtn")).click();
    }
}