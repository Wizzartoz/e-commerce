package com.ecommerce.order.internal.service;

import com.ecommerce.MongoTestContainer;
import com.ecommerce.entity.Customer;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.Post;
import com.ecommerce.entity.Product;
import com.ecommerce.exception.ProductsNotAvailableException;
import com.ecommerce.order.internal.repository.OrderRepository;
import com.ecommerce.product.internal.repository.ProductRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceTest extends MongoTestContainer {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;

    @AfterEach
    void clearStorage() {
        orderRepository.deleteAll();
    }

    @Test
    void createOrderIfProductsEmpty() {
        Customer customer = Customer.builder().name("Mykhailo").surname("Maznychko").phone("0968743212").build();
        Post post = Post.builder().number(13).address("some address").city("town").build();
        Order orderFirst = Order.builder().customer(customer).post(post).products(Collections.emptyList()).build();

        assertThrows(ConstraintViolationException.class, () -> orderService.checkout(orderFirst));

        Order orderSecond = Order.builder().customer(customer).post(post).products(null).build();

        assertThrows(ConstraintViolationException.class, () -> orderService.checkout(orderSecond));
    }

    @Test
    void createOrderIfAllProductNotAvailable() {
        Customer customer = Customer.builder().name("Mykhailo").surname("Maznychko").phone("0968743212").build();
        Post post = Post.builder().number(13).address("some address").city("town").build();
        Product product = Product.builder().name("Product A").description("Description A").price(100).quantityForSale(1).photoLink("PhotoLink A").build();
        Order order = Order.builder().customer(customer).post(post).products(Collections.singletonList(product)).build();

        assertThrows(ProductsNotAvailableException.class, () -> orderService.checkout(order));
    }

    @Test
    void createOrderIfPartialProductNotAvailable() {
        Customer customer = Customer.builder().name("Mykhailo").surname("Maznychko").phone("0968743212").build();
        Post post = Post.builder().number(13).address("some address").city("town").build();
        Product productA = Product.builder().name("Product A").description("Description A").price(100).quantityForSale(1).photoLink("PhotoLink A").build();
        Product productB = Product.builder().name("Product B").description("Description B").price(200).quantityForSale(1).photoLink("PhotoLink B").build();
        Order order = Order.builder().customer(customer).post(post).products(List.of(productA, productB)).build();
        productRepository.save(productA);

        assertThrows(ProductsNotAvailableException.class, () -> orderService.checkout(order));
    }

    @Test
    void createOrderIfProductNotAvailableByCount() {
        Customer customer = Customer.builder().name("Mykhailo").surname("Maznychko").phone("0968743212").build();
        Post post = Post.builder().number(13).address("some address").city("town").build();
        Product productA = Product.builder().name("Product A").description("Description A").price(100).quantityForSale(1).photoLink("PhotoLink A").build();
        Product productB = Product.builder().name("Product B").description("Description B").price(200).quantityForSale(1).photoLink("PhotoLink B").build();
        List<Product> products = List.of(productA, productB);
        Order order = Order.builder().customer(customer).post(post).products(products).build();
        productA.setQuantityForSale(0);
        productRepository.save(productA);
        productRepository.save(productB);
        productA.setQuantityForSale(1);

        assertThrows(ProductsNotAvailableException.class, () -> orderService.checkout(order));
    }

    @Test
    void createOrderIfProductAllProductAvailable() {
        Customer customer = Customer.builder().name("Mykhailo").surname("Maznychko").phone("0968743212").build();
        Post post = Post.builder().number(13).address("some address").city("town").build();
        Product productA = Product.builder().name("Product A").description("Description A").price(100).quantityForSale(1).photoLink("PhotoLink A").build();
        Product productB = Product.builder().name("Product B").description("Description B").price(200).quantityForSale(1).photoLink("PhotoLink B").build();
        Order order = Order.builder().customer(customer).post(post).products(List.of(productA, productB)).build();
        productRepository.save(productA);
        productRepository.save(productB);

        assertDoesNotThrow(() -> orderService.checkout(order));
    }
}