package com.ecommerce.e2e.product;

import com.ecommerce.entity.Product;
import com.ecommerce.product.internal.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import io.restassured.RestAssured;

import java.util.List;

import static io.restassured.RestAssured.given;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableMongoRepositories
public class ProductSearchStepDefinition {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @LocalServerPort
    private Integer port;
    private Response lastResponse;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Given("the storage has been cleared")
    public void clearStorage() {
        productRepository.deleteAll();
    }

    @And("the following products are available as JSON")
    @SneakyThrows
    public void insertProducts(String json) {
        List<Product> products = objectMapper.readValue(json, new TypeReference<>(){});
        productRepository.saveAll(products);
    }

    @When("the client requests for products with page {int}, size {int}, order {string}, sort {string}, search {string}")
    public void request(int page, int size, String order, String sort, String search) {
        RequestSpecification specification = given()
                .param("page", page)
                .param("size", size);
        if (!"none".equals(order)) {
            specification.param("order", order);
        }
        if (!"none".equals(sort)) {
            specification.param("sort", sort);
        }
        if (!"none".equals(search)) {
            specification.param("search", search);
        }
        this.lastResponse = specification.get("/products");
    }

    @Then("the client receives status code of {int}")
    public void compareCodeStatuses(int statusCode) {
        int  currentStatusCode = lastResponse.getStatusCode();
        Assertions.assertEquals(currentStatusCode, statusCode);
    }

    @And("the user should receive the following data as JSON")
    @SneakyThrows
    public void checkBodyResponse(String json) {
        List<Product> productsFromStorage = objectMapper.readValue(json, new TypeReference<>(){});
        List<Product> productsFromBody = lastResponse.getBody().as(new TypeRef<>() {});
        Assert.assertEquals(productsFromBody, productsFromStorage);
    }
}