package com.ecommerce.ui;

import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
class CategorySeleniumTest extends BaseSeleniumTest {

    @Autowired
    CategoryRepository categoryRepository;

    private static final String CATEGORY_NAME = "Selenium Category";
    private static final String CATEGORY_SLUG = "seleniumcategory";
    private static final String CATEGORY_DESCRIPTION = "Categoria creada desde Selenium";

    private static final String UPDATED_CATEGORY_NAME = "Selenium Category Updated";
    private static final String UPDATED_CATEGORY_SLUG = "seleniumcategoryupdated";
    private static final String UPDATED_CATEGORY_DESCRIPTION = "Categoria actualizada desde Selenium";

    private static final String CATEGORY_IMAGE_URL = "https://example.com/category.png";
    private static final String UPDATED_CATEGORY_IMAGE_URL = "https://example.com/category-updated.png";

    @BeforeEach
    void cleanCategoryData() {
        categoryRepository.findBySlug(UPDATED_CATEGORY_SLUG)
                .ifPresent(categoryRepository::delete);

        categoryRepository.findBySlug(CATEGORY_SLUG)
                .ifPresent(categoryRepository::delete);
    }

    @Test
    void adminCanCreateEditAndDeleteCategoryFromBrowser() {
        loginAdmin();

        open("categories/new");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));

        type(By.id("name"), CATEGORY_NAME);
        type(By.id("slug"), CATEGORY_SLUG);
        type(By.id("description"), CATEGORY_DESCRIPTION);
        setInputValue(By.id("imageUrl"), CATEGORY_IMAGE_URL);
        new Actions(driver).moveToElement(driver.findElement(By.id("active"))).click().perform();
//        click(By.id("active"));

        submitCategoryForm();

        wait.until(ExpectedConditions.urlContains("/categories"));
        waitUntilPageContains("categoria creada correctamente");

        Optional<Category> createdCategory = categoryRepository.findBySlug(CATEGORY_SLUG);

        assertThat(createdCategory).isPresent();
        assertThat(createdCategory.get().getName()).isEqualTo(CATEGORY_NAME);
        assertThat(createdCategory.get().getSlug()).isEqualTo(CATEGORY_SLUG);
        assertThat(createdCategory.get().getDescription()).isEqualTo(CATEGORY_DESCRIPTION);
        assertThat(createdCategory.get().getImageUrl()).isEqualTo(CATEGORY_IMAGE_URL);
        assertThat(createdCategory.get().getActive()).isTrue();
        assertThat(createdCategory.get().getParent()).isNull();

        clickEditButtonForCategory(CATEGORY_NAME);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));

        type(By.id("name"), UPDATED_CATEGORY_NAME);
        type(By.id("slug"), UPDATED_CATEGORY_SLUG);
        type(By.id("description"), UPDATED_CATEGORY_DESCRIPTION);
        setInputValue(By.id("imageUrl"), UPDATED_CATEGORY_IMAGE_URL);

        submitCategoryForm();

        wait.until(ExpectedConditions.urlContains("/categories"));
        waitUntilPageContains("Categoria actualizada correctamente");

        Optional<Category> oldCategory = categoryRepository.findBySlug(CATEGORY_SLUG);
        Optional<Category> updatedCategory = categoryRepository.findBySlug(UPDATED_CATEGORY_SLUG);

        assertThat(oldCategory).isEmpty();
        assertThat(updatedCategory).isPresent();
        assertThat(updatedCategory.get().getName()).isEqualTo(UPDATED_CATEGORY_NAME);
        assertThat(updatedCategory.get().getSlug()).isEqualTo(UPDATED_CATEGORY_SLUG);
        assertThat(updatedCategory.get().getDescription()).isEqualTo(UPDATED_CATEGORY_DESCRIPTION);
        assertThat(updatedCategory.get().getImageUrl()).isEqualTo(UPDATED_CATEGORY_IMAGE_URL);
        assertThat(updatedCategory.get().getActive()).isTrue();

        clickDeleteButtonForCategory(UPDATED_CATEGORY_NAME);

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();

        wait.until(ExpectedConditions.urlContains("/categories"));
        waitUntilPageContains("Categoria eliminada correctamente");

        assertThat(categoryRepository.findBySlug(UPDATED_CATEGORY_SLUG)).isEmpty();
    }

    private void submitCategoryForm() {
        submitForm(By.xpath(
                "//form[.//input[@id='name'] " +
                        "and .//input[@id='slug'] " +
                        "and .//textarea[@id='description'] " +
                        "and .//input[@id='imageUrl']]"
        ));
    }

    private void clickEditButtonForCategory(String categoryName) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//tr[.//*[contains(@class,'category-name') and normalize-space()='" + categoryName + "']]" +
                        "//a[contains(normalize-space(), 'Editar')]"
        ))).click();
    }

    private void clickDeleteButtonForCategory(String categoryName) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//tr[.//*[contains(@class,'category-name') and normalize-space()='" + categoryName + "']]" +
                        "//button[contains(normalize-space(), 'Eliminar')]"
        ))).click();
    }
}