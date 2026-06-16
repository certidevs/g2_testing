package com.ecommerce.ui;

import com.ecommerce.model.Brand;
import com.ecommerce.repository.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

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

        driver.get(baseUrl + "brands/new");

        type(By.id("name"), BRAND_NAME);
        type(By.id("nif"), BRAND_NIF);
        type(By.id("country"), BRAND_COUNTRY);
        type(By.id("website"), BRAND_WEBSITE);
        type(By.id("logo"), BRAND_LOGO);
//        click(By.id("active"));
        check(By.id("active"), true);

        new Actions(driver).moveToElement(
                driver.findElement(By.cssSelector("form[action*='/brands'] button[type='submit']"))
        ).click().perform();

        wait.until(ExpectedConditions.urlContains("/brands"));
        waitUntilPageContains(BRAND_NAME);

        Optional<Brand> createdBrand = brandRepository.findByNif(BRAND_NIF);

        assertThat(createdBrand).isPresent();
        assertThat(createdBrand.get().getName()).isEqualTo(BRAND_NAME);
        assertThat(createdBrand.get().getCountry()).isEqualTo(BRAND_COUNTRY);
        assertThat(createdBrand.get().getWebsite()).isEqualTo(BRAND_WEBSITE);
        assertThat(createdBrand.get().getLogo()).isEqualTo(BRAND_LOGO);
        assertThat(createdBrand.get().getActive()).isTrue();

        clickEditButtonForBrand(BRAND_NAME);

        type(By.id("name"), "Selenium Brand Updated");
        type(By.id("country"), "Portugal");
        type(By.id("website"), "https://updated-brand.example.com");

        new Actions(driver).moveToElement(
                driver.findElement(By.cssSelector("form[action*='/brands'] button[type='submit']"))
        ).click().perform();

        wait.until(ExpectedConditions.urlContains("/brands"));
        waitUntilPageContains("Selenium Brand Updated");

        Brand updatedBrand = brandRepository.findByNif(BRAND_NIF).orElseThrow();

        assertThat(updatedBrand.getName()).isEqualTo("Selenium Brand Updated");
        assertThat(updatedBrand.getCountry()).isEqualTo("Portugal");
        assertThat(updatedBrand.getWebsite()).isEqualTo("https://updated-brand.example.com");

        clickDeleteButtonForBrand("Selenium Brand Updated");

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        wait.until(ExpectedConditions.urlContains("/brands"));

        assertThat(brandRepository.findByNif(BRAND_NIF)).isEmpty();
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
}
