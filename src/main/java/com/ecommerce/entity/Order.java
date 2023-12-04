package com.ecommerce.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Unwrapped;

import java.util.List;

import static org.springframework.data.mongodb.core.mapping.Unwrapped.OnEmpty.USE_NULL;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document("order")
public class Order {
    @Id
    private String id;
    @NotNull
    private Customer customer;
    @NotNull
    @Unwrapped(onEmpty = USE_NULL)
    private Post post;
    @NotNull
    private List<Product> products;
    private OrderStatus status;
}

