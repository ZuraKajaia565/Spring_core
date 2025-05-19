package com.zura.gymCRM.component.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "src/test/resources/features/component",
		glue = {"com.zura.gymCRM.component.stepdefs", "com.zura.gymCRM.component.config"},
		plugin = {"pretty", "html:target/cucumber-reports/component"}
)
public class ComponentTestRunner {
}