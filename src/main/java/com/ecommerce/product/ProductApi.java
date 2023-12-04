package com.ecommerce.product;

import com.ecommerce.dto.ProductRequestDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * This interface is used for interaction with the product module
 */
@Validated
public interface ProductApi {
    boolean isProductAvailable(@NotNull @NotEmpty List<ProductRequestDto> products);
}
