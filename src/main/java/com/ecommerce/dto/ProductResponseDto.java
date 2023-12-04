package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {
    private String id;
    @NotBlank
    private String name;
    @NotNull
    private Integer price;
    @NotBlank
    private String description;
    @NotNull
    private Integer quantityForSale;
    private String photoLink;
}
