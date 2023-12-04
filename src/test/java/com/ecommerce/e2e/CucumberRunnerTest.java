package com.ecommerce.e2e;

import com.ecommerce.MongoTestContainer;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/main.feature",
        glue = "com.ecommerce.product",
        plugin = {"pretty"}
)
public class CucumberRunnerTest extends MongoTestContainer {}
