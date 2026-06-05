package com.ecommerce.ui;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

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
}
