package com.ecommerce;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

// Suite para ejecutar los tests de selenium

// Comandos terminal:

// ./mvnw test
// ./mvnw test -Dtest=SeleniumTestsSuite
// */
@Suite
@SelectPackages({"com.ecommerce.ui"})
public class SeleniumTestsSuite {

}