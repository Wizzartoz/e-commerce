package com.ecommerce.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("product")
public class Product {
    @Id
    private String id;
    @TextIndexed(weight = 2)
    @NotBlank
    private String name;
    @NotNull
    private Integer price;
    @TextIndexed
    @NotBlank
    private String description;
    @NotNull
    private Integer quantityForSale;
    private String photoLink;
}
