package com.ecommerce.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@Getter
public class Customer {
    @NotBlank
    String name;
    @NotBlank
    String surname;
    //TODO must be regex here
    String phone;
}
