package com.sky.fodmapApp.ft;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

// Use cucumber class as the entry point for the test classes
@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = "html:reports/report.html",
        glue = "com.sky.fodmapApp.ft",
        features = "src/test/resources/features")
public class CucumberTestRunner {

}
