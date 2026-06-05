package com.ecommerce.ui;

import org.junit.jupiter.api.Test;

public class SecuritySeleniumTest extends BaseSeleniumTest{
    @Test
    public void anonymousTryAccessProtectedPathRedirectToLogin(){
        driver.get(baseUrl + "purchases");
        wait.until(driver -> driver.getCurrentUrl().equals(baseUrl + "login"));
        loginUser();
        wait.until(driver -> driver.getCurrentUrl().equals(baseUrl + "purchases"));
    }
}
