package com.zura.gymCRM.component.runners;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/component", // Use direct path
        glue = {"com.zura.gymCRM.component.stepdefs", "com.zura.gymCRM.component.config"},
        plugin = {"pretty", "html:target/cucumber-reports/component"}
)
public class RunCucumberTest {
        // This class is empty
}