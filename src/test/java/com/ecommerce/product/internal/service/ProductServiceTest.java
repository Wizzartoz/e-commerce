package com.ecommerce.product.internal.service;

import com.ecommerce.MongoTestContainer;
import com.ecommerce.entity.Product;
import com.ecommerce.product.internal.repository.ProductRepository;
import com.ecommerce.product.internal.service.search.SearchEngine;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:test.properties")
class ProductServiceTest extends MongoTestContainer {

    @Autowired
    SearchEngine productService;

    @Autowired
    ProductRepository productRepository;

    @AfterEach
    void clearStorage() {
        productRepository.deleteAll();
    }

    @Test
    void getDataFromFirstPageWithDefaultOrderAndSort() {
        List<Product> products = new ArrayList<>();
        products.add(Product.builder().name("Product A").description("Description A").price(100).photoLink("PhotoLink A").build());
        products.add(Product.builder().name("Product B").description("Description B").price(200).photoLink("PhotoLink B").build());
        productRepository.insert(products);
        Sort sort = Sort.by(Sort.Direction.ASC, "price");
        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, 10, sort);

        assertEquals(products, productService.search(pageable, null, Optional.empty()));
    }

    @Test
    void getDataFromSecondPageWithDefaultOrderAndSort() {
        List<Product> products = new ArrayList<>();
        products.add(Product.builder().name("Product A").description("Description A").price(100).photoLink("PhotoLink A").build());
        products.add(Product.builder().name("Product B").description("Description B").price(200).photoLink("PhotoLink B").build());
        productRepository.insert(products);
        Sort sort = Sort.by(Sort.Direction.ASC, "price");
        org.springframework.data.domain.Pageable pageable = PageRequest.of(1, 1, sort);

        assertEquals(Collections.singletonList(products.get(1)), productService.search(pageable, null, Optional.empty()));
    }

    @Test
    void getDataFromFirstPageWithDescOrderAndDefaultSort() {
        List<Product> products = new ArrayList<>();
        products.add(Product.builder().name("Product A").description("Description A").price(100).photoLink("PhotoLink A").build());
        products.add(Product.builder().name("Product B").description("Description B").price(200).photoLink("PhotoLink B").build());
        productRepository.insert(products);
        Sort sort = Sort.by(Sort.Direction.DESC, "price");
        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, 2, sort);
        Collections.reverse(products);

        assertEquals(products, productService.search(pageable, null, Optional.empty()));
    }

    @Test
    void getDataFromFirstPageWithDefaultOrderAndSortIncludeSearch() {
        List<Product> products = new ArrayList<>();
        products.add(Product.builder().name("Product A").description("Description A").price(100).photoLink("PhotoLink A").build());
        products.add(Product.builder().name("Product B").description("Description B").price(200).photoLink("PhotoLink B").build());
        products.add(Product.builder().name("Special Product").description("Description").price(300).photoLink("PhotoLink").build());
        productRepository.insert(products);
        Sort sort = Sort.by(Sort.Direction.ASC, "price");
        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, 3, sort);

        assertEquals(products, productService.search(pageable, "description", Optional.empty()));
        assertEquals(products, productService.search(pageable, "ipt", Optional.empty()));
        assertEquals(products, productService.search(pageable, "product", Optional.empty()));
        assertEquals(Collections.singletonList(products.get(2)), productService.search(pageable, "spec", Optional.empty()));
    }

    @Test
    void getDataFromFirstPageWithDefaultOrderAndSortWithFilters() {
        List<Product> products = new ArrayList<>();
        products.add(Product.builder().name("Product C").description("Description C").price(100).photoLink("PhotoLink C").build());
        products.add(Product.builder().name("Product A").description("Description A").price(200).photoLink("PhotoLink A").build());
        products.add(Product.builder().name("Product B").description("Description B").price(300).photoLink("PhotoLink B").build());
        products.add(Product.builder().name("Product G").description("Description G").price(400).photoLink("PhotoLink G").build());
        products.add(Product.builder().name("Product D").description("Description D").price(500).photoLink("PhotoLink D").build());
        products.add(Product.builder().name("Product Z").description("Description Z").price(600).photoLink("PhotoLink Z").build());
        productRepository.insert(products);
        Sort sort = Sort.by(Sort.Direction.ASC, "price");
        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, 6, sort);
        List<String> filtersFirst = new ArrayList<>();
        filtersFirst.add("price,100-200");
        filtersFirst.add("name,Product A");

        assertEquals(Collections.singletonList(products.get(1)), productService.search(pageable, null, Optional.of(filtersFirst)));

        List<String> filtersSecond = new ArrayList<>();
        filtersSecond.add("price,300-500");

        assertEquals(List.of(products.get(2), products.get(3), products.get(4)), productService.search(pageable, null, Optional.of(filtersSecond)));

        List<String> filtersFourth = new ArrayList<>();
        filtersFourth.add("price,100-200");
        filtersFourth.add("price,300-600");

        assertEquals(products, productService.search(pageable, null, Optional.of(filtersFourth)));


        List<String> filtersThird = new ArrayList<>();
        filtersThird.add("name,Product B");
        filtersThird.add("name,Product A");

        assertEquals(List.of(products.get(1), products.get(2)), productService.search(pageable, null, Optional.of(filtersThird)));
    }

    @Test
    void getDataFromFirstPageWithDefaultOrderAndSortIncludeSearchAndFilter() {
        List<Product> products = new ArrayList<>();
        products.add(Product.builder().name("Special Product").description("Description A").price(100).photoLink("PhotoLink A").build());
        products.add(Product.builder().name("Product B").description("Description B").price(200).photoLink("PhotoLink B").build());
        products.add(Product.builder().name("Special Product").description("Description").price(200).photoLink("PhotoLink").build());
        productRepository.insert(products);
        Sort sort = Sort.by(Sort.Direction.ASC, "price");
        org.springframework.data.domain.Pageable pageable = PageRequest.of(0, 10, sort);
        List<String> filter = new ArrayList<>();
        filter.add("price,200-200");

        assertEquals(Collections.singletonList(products.get(2)), productService.search(pageable, "special", Optional.of(filter)));
        assertEquals(Collections.singletonList(products.get(2)), productService.search(pageable, "spec", Optional.of(filter)));
    }
}