package com.ecommerce;


import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({"com.ecommerce.controller"})
public class ControllerTestsSuite {
}
