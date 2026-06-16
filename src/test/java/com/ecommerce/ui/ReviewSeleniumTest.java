package com.ecommerce.ui;

import com.ecommerce.model.Review;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewSeleniumTest extends BaseSeleniumTest {

    @Test
    void reviewListShowsExistingReviews() {
        // Abre la pantalla publica de gestion/listado de reseñas.
        driver.get(baseUrl + "reviews");

        // Espera al titulo principal para confirmar que la vista termino de renderizar.
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".reviews-title")));

        // Comprueba que el listado carga las reseñas iniciales creadas en BaseSeleniumTest.
        assertEquals("Gestión de reseñas", title.getText());
        assertTrue(driver.getPageSource().contains("Fatal"));
        assertTrue(driver.getPageSource().contains("excelente pizza"));
        assertTrue(driver.getPageSource().contains("Pantalón"));
        assertTrue(driver.getPageSource().contains("Camiseta"));

        List<WebElement> rows = driver.findElements(By.cssSelector(".reviews-table tbody tr"));
        assertEquals(2, rows.size());
    }

    @Test
    void reviewDetailShowsReviewInformation() {
        // Entra directamente al detalle de una reseña concreta.
        driver.get(baseUrl + "reviews/" + reviewOK.getId());

        // Espera al encabezado de detalle para confirmar que la pagina esta lista.
        WebElement pageTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".review-title")));

        // Comprueba que la ficha muestra titulo, mensaje, valoracion y producto relacionado.
        assertEquals("Detalle de reseña", pageTitle.getText());
        assertTrue(driver.getPageSource().contains("excelente pizza"));
        assertTrue(driver.getPageSource().contains("ok"));
        assertTrue(driver.getPageSource().contains("5/5"));
        assertTrue(driver.getPageSource().contains("Camiseta"));
    }

    @Test
    void adminCanEditReviewFromDetailPage() {
        // Inicia sesion como administrador para editar desde la pantalla de detalle.
        loginAdmin();

        // Abre el detalle de la reseña que se va a modificar.
        driver.get(baseUrl + "reviews/" + reviewOK.getId());
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editTitle")));

        // Cambia los campos editables del formulario.
        type(By.id("editTitle"), "Reseña editada con Selenium");
        type(By.id("editMessage"), "Mensaje actualizado desde el test Selenium");
        WebElement ratingSelect = driver.findElement(By.id("editRating"));
        ratingSelect.findElement(By.cssSelector("option[value='4']")).click();

        // Guarda los cambios.
        clickWithJavaScript(driver.findElement(By.cssSelector(".edit-form button[type='submit']")));

        // Verifica que la pagina vuelve al detalle y muestra el contenido actualizado.
        wait.until(ExpectedConditions.urlContains("/reviews/" + reviewOK.getId()));
        waitUntilPageContains("Reseña editada con Selenium");
        waitUntilPageContains("Mensaje actualizado desde el test Selenium");
        assertTrue(driver.getPageSource().contains("4/5"));

        // Verifica tambien en base de datos que la reseña se actualizo realmente.
        Review updatedReview = reviewRepository.findById(reviewOK.getId()).orElseThrow();
        assertEquals("Reseña editada con Selenium", updatedReview.getTitle());
        assertEquals("Mensaje actualizado desde el test Selenium", updatedReview.getMessage());
        assertEquals(4, updatedReview.getRating());
    }

    @Test
    void adminCanDeleteReviewFromDetailPage() {
        // Inicia sesion como administrador porque eliminar reseñas es una accion protegida.
        loginAdmin();

        // Abre el detalle de la reseña que se va a borrar.
        driver.get(baseUrl + "reviews/" + reviewMal.getId());
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".review-title")));

        // Pulsa eliminar y acepta el confirm del navegador.
        clickWithJavaScript(driver.findElement(By.cssSelector("a[href*='/reviews/delete/']")));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        // Comprueba que vuelve al listado y la reseña ya no existe.
        wait.until(ExpectedConditions.urlContains("/reviews"));
        waitUntilPageContains("Gestión de reseñas");
        assertFalse(reviewRepository.existsById(reviewMal.getId()));
        assertFalse(driver.getPageSource().contains("Fatal"));
    }

}
