package com.ecommerce.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@AllArgsConstructor
@Getter
@Builder
public class Post {
    @NotNull
    Integer number;
    @NotBlank
    String city;
    @NotBlank
    String address;
}
