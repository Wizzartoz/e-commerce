package com.ecommerce.product;

import com.ecommerce.dto.ProductResponseDto;
import com.ecommerce.product.internal.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private ProductService productService;

    @GetMapping
    public List<ProductResponseDto> getProductsByParameters(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false, defaultValue = "ASC") Sort.Direction order,
            @RequestParam(required = false, defaultValue = "price") String sort,
            @RequestParam(required = false) String search,
            @RequestParam Optional<List<String>> filter
    ) {
        return productService.findAllProductByCriteria(PageRequest.of(page, size, Sort.by(order, sort)), search, filter);
    }
}
