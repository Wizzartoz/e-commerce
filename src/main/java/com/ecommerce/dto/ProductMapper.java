package com.ecommerce.dto;

import com.ecommerce.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "quantityForSale", target = "quantityToPurchase")
    ProductRequestDto productToProductRequestDto(Product product);

    ProductResponseDto productToProductResponseDto(Product product);
}
