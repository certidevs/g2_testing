package com.ecommerce;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
/*

Suite para ejecutar los tests de repositorios

por terminal:

./mvnw test
./mvnw test -Dtest=RepositoryTestsSuite
 */
@Suite
@SelectPackages({"com.ecommerce.repository"})
public class RepositoryTestsSuite {

}