package com.zura.gymCRM.integration.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "classpath:features/integration",
    glue = {"com.zura.gymCRM.integration.stepdefs"},
    plugin = {"pretty", "html:target/cucumber-reports/integration"})
public class IntegrationTestRunner {}
