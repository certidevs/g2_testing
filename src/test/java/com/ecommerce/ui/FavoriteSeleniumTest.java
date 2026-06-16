package com.ecommerce.ui;

import com.ecommerce.model.Favorite;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

public class FavoriteSeleniumTest extends BaseSeleniumTest {

    @Test
    void guestIsRedirectedToLoginWhenOpeningFavorites() {
        // Un usuario sin sesion no puede ver favoritos y debe acabar en login.
        driver.get(baseUrl + "favorites");

        wait.until(ExpectedConditions.urlContains("/login"));

        assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    void userCanAddProductToFavoritesAndSeeItInFavoritesList() {
        // Inicia sesion como cliente normal para activar el boton de favoritos.
        loginUser();

        // Entra al detalle del producto y envia el formulario real de "Añadir a favoritos".
        driver.get(baseUrl + "products/" + camiseta.getId());
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("form[action*='/favorites/add/'] button[type='submit']")
        ));
        clickWithJavaScript(addButton);

        // Acepta la confirmacion del navegador y espera a volver al detalle.
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        wait.until(ExpectedConditions.urlContains("/products/" + camiseta.getId()));

        // Comprueba primero la base de datos y despues la pantalla de favoritos.
        assertTrue(favoriteRepository.findByUserIdAndProductId(user.getId(), camiseta.getId()).isPresent());

        driver.get(baseUrl + "favorites");
        waitUntilPageContains("Mis Favoritos");
        waitUntilPageContains("Camiseta");
    }

    @Test
    void addingSameProductTwiceDoesNotCreateDuplicateFavorite() {
        // Este flujo valida la proteccion del controlador contra favoritos duplicados.
        loginUser();

        addProductToFavoritesFromDetail();
        addProductToFavoritesFromDetail();

        assertEquals(1, favoriteRepository.findByUserIdWithProducts(user.getId()).size());
    }

    @Test
    void userCanRemoveProductFromFavoritesList() {
        // Crea un favorito inicial para probar la eliminacion desde la vista.
        favoriteRepository.save(Favorite.builder()
                .user(user)
                .product(camiseta)
                .build());

        loginUser();
        driver.get(baseUrl + "favorites");
        waitUntilPageContains("Camiseta");

        // Pulsa eliminar y acepta el confirm que protege la accion.
        WebElement removeButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("form[action*='/favorites/remove/'] button[type='submit']")
        ));
        clickWithJavaScript(removeButton);
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        // Comprueba que vuelve a la lista vacia y que el registro desaparecio.
        wait.until(ExpectedConditions.urlContains("/favorites"));
        waitUntilPageContains("No tienes productos favoritos aún");
        assertTrue(favoriteRepository.findByUserIdAndProductId(user.getId(), camiseta.getId()).isEmpty());
    }

    @Test
    void checkEndpointReturnsFavoriteStatusForLoggedUser() {
        // El endpoint auxiliar debe responder true si el producto esta guardado.
        favoriteRepository.save(Favorite.builder()
                .user(user)
                .product(camiseta)
                .build());

        loginUser();
        driver.get(baseUrl + "favorites/check/" + camiseta.getId());

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "true"));
        assertTrue(driver.findElement(By.tagName("body")).getText().contains("true"));
    }

    private void addProductToFavoritesFromDetail() {
        driver.get(baseUrl + "products/" + camiseta.getId());

        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("form[action*='/favorites/add/'] button[type='submit']")
        ));
        clickWithJavaScript(addButton);

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        wait.until(ExpectedConditions.urlContains("/products/" + camiseta.getId()));
    }

    private void clickWithJavaScript(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
}
