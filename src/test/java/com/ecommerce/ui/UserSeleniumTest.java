package com.ecommerce.ui;

import com.ecommerce.model.User;
import com.ecommerce.model.enums.Role;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class UserSeleniumTest extends BaseSeleniumTest
{
    @Autowired
    UserRepository userRepository;

    private static final String MANAGED_USERNAME = "selenium_managed_user";
    private static final String MANAGED_EMAIL = "selenium.managed@example.com";
    private static final String UPDATED_EMAIL = "selenium.managed.updated@example.com";

    private User managedUser;

    @BeforeEach
    void createManagedUser() {
        userRepository.findByUsername(MANAGED_USERNAME)
                .ifPresent(userRepository::delete);

        userRepository.findByEmail(UPDATED_EMAIL)
                .ifPresent(userRepository::delete);

        managedUser = userRepository.save(User.builder()
                .username(MANAGED_USERNAME)
                .name("Selenium")
                .lastName("User")
                .email(MANAGED_EMAIL)
                .phone("600111222")
                .password(passwordEncoder.encode("Password1!"))
                .role(Role.ROLE_CUSTOMER)
                .active(true)
                .creationDate(LocalDateTime.now())
                .build());
    }

    @Test
    void adminCanOpenUsersPanelFromBrowser() {
        loginAdmin();

        open("admin/users");

        wait.until(ExpectedConditions.urlContains("/admin/users"));
        waitUntilPageContains("Gestionar usuarios");
        waitUntilPageContains(MANAGED_USERNAME);
        waitUntilPageContains(MANAGED_EMAIL);
    }

    @Test
    void adminCanEditUserFromBrowser() {
        loginAdmin();

        open("admin/users");

        wait.until(ExpectedConditions.urlContains("/admin/users"));
        waitUntilPageContains(MANAGED_USERNAME);

        clickEditButtonForUser(MANAGED_USERNAME);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));

        type(By.id("name"), "Selenium Updated");
        type(By.id("lastName"), "Customer Updated");
        type(By.id("email"), UPDATED_EMAIL);
        type(By.id("phone"), "600333444");

        new Select(driver.findElement(By.id("gender"))).selectByValue("MALE");
        new Select(driver.findElement(By.id("paymentMethod"))).selectByValue("PAYPAL");
        new Select(driver.findElement(By.id("role"))).selectByValue("ROLE_CUSTOMER");

        setInputValue(By.id("birthday"), "1995-01-15");

        submitAdminEditForm();

        wait.until(ExpectedConditions.urlContains("/admin/users"));
        waitUntilPageContains("Usuario actualizado correctamente");
        waitUntilPageContains(UPDATED_EMAIL);

        User updatedUser = userRepository.findByUsername(MANAGED_USERNAME).orElseThrow();

        assertThat(updatedUser.getName()).isEqualTo("Selenium Updated");
        assertThat(updatedUser.getLastName()).isEqualTo("Customer Updated");
        assertThat(updatedUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(updatedUser.getPhone()).isEqualTo("600333444");
        assertThat(updatedUser.getGender().name()).isEqualTo("MALE");
        assertThat(updatedUser.getPaymentMethod().name()).isEqualTo("PAYPAL");
        assertThat(updatedUser.getRole()).isEqualTo(Role.ROLE_CUSTOMER);
    }

    @Test
    void adminCanDeactivateAndActivateUserFromBrowser() {
        loginAdmin();

        open("admin/users");

        wait.until(ExpectedConditions.urlContains("/admin/users"));
        waitUntilPageContains(MANAGED_USERNAME);

        clickToggleStatusButtonForUser(MANAGED_USERNAME, "Desactivar");

        wait.until(ExpectedConditions.urlContains("/admin/users"));
        waitUntilPageContains("Estado del usuario actualizado");

        User inactiveUser = userRepository.findByUsername(MANAGED_USERNAME).orElseThrow();

        assertThat(inactiveUser.isActive()).isFalse();

        clickToggleStatusButtonForUser(MANAGED_USERNAME, "Activar");

        wait.until(ExpectedConditions.urlContains("/admin/users"));
        waitUntilPageContains("Estado del usuario actualizado");

        User activeUser = userRepository.findByUsername(MANAGED_USERNAME).orElseThrow();

        assertThat(activeUser.isActive()).isTrue();
    }

    private void clickEditButtonForUser(String username) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//tr[.//*[contains(@class,'user-name') and normalize-space()='" + username + "']]" +
                        "//a[contains(normalize-space(), 'Editar')]"
        ))).click();
    }

    private void clickToggleStatusButtonForUser(String username, String buttonText) {
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                "//tr[.//*[contains(@class,'user-name') and normalize-space()='" + username + "']]" +
                        "//button[contains(normalize-space(), '" + buttonText + "')]"
        ))).click();
    }

    private void submitAdminEditForm() {
        submitForm(By.xpath(
                "//form[.//input[@id='name'] " +
                        "and .//input[@id='lastName'] " +
                        "and .//input[@id='email'] " +
                        "and .//select[@id='role']]"
        ));
    }
}
