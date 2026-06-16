package com.ecommerce.ui;

import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class RegisterLoginSeleniumTest extends BaseSeleniumTest
{
    @Autowired
    UserRepository userRepository;

    @Test
    void userCanRegisterAndLoginFromBrowser() {
        String username = "selenium_customer";
        String email = "selenium.customer@example.com";
        String password = "Password1!";

        open("register");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));

        type(By.id("username"), username);
        type(By.id("email"), email);
        type(By.id("password"), password);
        type(By.id("passwordConfirm"), password);

        submitRegisterForm();

        wait.until(ExpectedConditions.urlContains("/login"));
        waitUntilPageContains("Cuenta creada correctamente, inicia sesión");

        Optional<User> createdUser = userRepository.findByUsername(username);

        assertThat(createdUser).isPresent();
        assertThat(createdUser.get().getUsername()).isEqualTo(username);
        assertThat(createdUser.get().getEmail()).isEqualTo(email);
        assertThat(createdUser.get().getRole().name()).isEqualTo("ROLE_CUSTOMER");
        assertThat(createdUser.get().isActive()).isTrue();

        type(By.id("username"), username);
        type(By.id("password"), password);

        driver.findElement(By.id("loginButton")).click();

        wait.until(ExpectedConditions.urlContains("/products"));
    }

    @Test
    void registerWithDifferentPasswordsShowsValidationError() {
        open("register");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));

        type(By.id("username"), "selenium_invalid_customer");
        type(By.id("email"), "selenium.invalid@example.com");
        type(By.id("password"), "Password1!");
        type(By.id("passwordConfirm"), "Different1!");

        submitRegisterForm();

        wait.until(ExpectedConditions.urlContains("/register"));
        waitUntilPageContains("Las contraseñas no coinciden");

        assertThat(userRepository.findByUsername("selenium_invalid_customer")).isEmpty();
    }

    private void submitRegisterForm() {
        submitForm(By.xpath(
                "//form[.//input[@id='username'] " +
                        "and .//input[@id='email'] " +
                        "and .//input[@id='password'] " +
                        "and .//input[@id='passwordConfirm']]"
        ));
    }
}
