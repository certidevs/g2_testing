package com.ecommerce.ui;

import com.ecommerce.model.Favorite;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

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
        loginUser();

        driver.get(baseUrl + "products/" + camiseta.getId());

        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("form[action*='/favorites/add/'] button[type='submit']")
        ));

        scrollToElement(addButton);

        try {
            addButton.click();
        } catch (ElementClickInterceptedException e) {
            clickWithJavaScript(addButton);
        }

        acceptAlertIfPresent();

        wait.until(driver -> favoriteRepository
                .findByUserIdAndProductId(user.getId(), camiseta.getId())
                .isPresent()
        );

        assertTrue(
                favoriteRepository.findByUserIdAndProductId(user.getId(), camiseta.getId()).isPresent()
        );

        driver.get(baseUrl + "favorites");

        waitUntilPageContains("Mis Favoritos");
        waitUntilPageContains("Camiseta");
    }

    @Test
    void addingSameProductTwiceDoesNotCreateDuplicateFavorite() {
        loginUser();

        addProductToFavoritesFromDetailWithoutAlert();
        waitUntilFavoriteExists();

        addProductToFavoritesFromDetailWithoutAlert();
        waitUntilFavoriteExists();

        long favoritesForProduct = favoriteRepository.findByUserIdWithProducts(user.getId())
                .stream()
                .filter(favorite -> favorite.getProduct().getId().equals(camiseta.getId()))
                .count();

        assertEquals(1, favoritesForProduct);
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

    private void addProductToFavoritesFromDetailWithoutAlert() {
        driver.get(baseUrl + "products/" + camiseta.getId());

        WebElement form = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("form[action*='/favorites/add/']")
        ));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});",
                form
        );

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].submit();",
                form
        );

        wait.until(driver -> favoriteRepository
                .findByUserIdAndProductId(user.getId(), camiseta.getId())
                .isPresent()
        );
    }

    private void acceptAlertIfPresent() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            Alert alert = shortWait.until(ExpectedConditions.alertIsPresent());
            alert.accept();
        } catch (TimeoutException | NoAlertPresentException ignored) {
            // Si no hay alert, el test continúa.
        }
    }

    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});",
                element
        );
    }

    private void waitUntilFavoriteExists() {
        wait.until(driver -> favoriteRepository
                .findByUserIdAndProductId(user.getId(), camiseta.getId())
                .isPresent()
        );
    }

}
