package com.ecommerce.ui;

import com.ecommerce.model.Brand;
import com.ecommerce.repository.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

public class BrandSeleniumTest extends BaseSeleniumTest
{
    @Autowired
    BrandRepository brandRepository;

    private static final String BRAND_NAME = "Selenium Brand";
    private static final String BRAND_NIF = "B12345678";
    private static final String BRAND_COUNTRY = "España";
    private static final String BRAND_WEBSITE = "https://selenium-brand.example.com";
    private static final String BRAND_LOGO = "https://selenium-brand.example.com/logo.png";

    @BeforeEach
    void cleanBrandData() {
        brandRepository.findByNif(BRAND_NIF)
                .ifPresent(brandRepository::delete);
    }

    @Test
    void adminCanCreateEditAndDeleteBrandFromBrowser() {
        loginAdmin();

        String unique = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        String brandName = "Selenium Brand " + unique;
        String brandNif = generateValidNif();
        String brandLogo = "test-brand-logo-" + unique + ".png";
        String updatedBrandName = "Selenium Brand Updated " + unique;

        driver.get(baseUrl + "brands/new");

        type(By.id("name"), brandName);
        type(By.id("nif"), brandNif);
        type(By.id("country"), BRAND_COUNTRY);
        type(By.id("website"), "brand-" + unique + ".example.com");
        type(By.id("logo"), brandLogo);
        check(By.id("active"), true);

        submitBrandForm();

        wait.until(ExpectedConditions.urlMatches(".*/brands/?(\\?.*)?$"));

        Brand createdBrand = wait.until(driver ->
                brandRepository.findByNif(brandNif).orElse(null)
        );

        assertThat(createdBrand.getName()).isEqualTo(brandName);
        assertThat(createdBrand.getCountry()).isEqualTo(BRAND_COUNTRY);
        assertThat(createdBrand.getWebsite()).isEqualTo("brand-" + unique + ".example.com");
        assertThat(createdBrand.getLogo()).isEqualTo(brandLogo);
        assertThat(createdBrand.getActive()).isTrue();

        clickEditButtonForBrand(brandName);

        type(By.id("name"), updatedBrandName);
        type(By.id("country"), "Portugal");
        type(By.id("website"), "updated-brand-" + unique + ".example.com");

        submitBrandForm();

        wait.until(ExpectedConditions.urlMatches(".*/brands/?(\\?.*)?$"));
        waitUntilPageContains(updatedBrandName);

        Brand updatedBrand = wait.until(driver ->
                brandRepository.findByNif(brandNif)
                        .filter(brand -> updatedBrandName.equals(brand.getName()))
                        .orElse(null)
        );

        assertThat(updatedBrand.getName()).isEqualTo(updatedBrandName);
        assertThat(updatedBrand.getCountry()).isEqualTo("Portugal");
        assertThat(updatedBrand.getWebsite()).isEqualTo("updated-brand-" + unique + ".example.com");

        clickDeleteButtonForBrand(updatedBrandName);

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        wait.until(driver -> brandRepository.findByNif(brandNif).isEmpty());

        assertThat(brandRepository.findByNif(brandNif)).isEmpty();
    }

    private void clickEditButtonForBrand(String brandName) {
        driver.findElement(By.xpath(
                "//tr[td[contains(normalize-space(), '" + brandName + "')]]" +
                        "//a[normalize-space()='Editar']"
        )).click();
    }

    private void clickDeleteButtonForBrand(String brandName) {
        driver.findElement(By.xpath(
                "//tr[td[contains(normalize-space(), '" + brandName + "')]]" +
                        "//button[normalize-space()='Eliminar']"
        )).click();
    }

    private void submitBrandForm() {
        By submitButton = By.cssSelector("form[action*='/brands'] button[type='submit']");

        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(submitButton));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});",
                button
        );

        wait.until(ExpectedConditions.elementToBeClickable(button));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
    }

    private String generateValidNif() {
        int number = ThreadLocalRandom.current().nextInt(10_000_000, 100_000_000);
        String letters = "TRWAGMYFPDXBNJZSQVHLCKE";
        char letter = letters.charAt(number % 23);

        return number + String.valueOf(letter);
    }
}
