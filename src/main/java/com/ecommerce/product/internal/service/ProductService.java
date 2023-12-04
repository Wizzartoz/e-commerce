package com.ecommerce.product.internal.service;

import com.ecommerce.entity.Product;
import com.ecommerce.product.ProductApi;
import com.ecommerce.product.internal.repository.ProductRepository;
import com.ecommerce.product.internal.service.search.SearchEngine;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class ProductService implements ProductApi {

    private SearchEngine searchEngine;
    private ProductRepository productRepository;

    public List<Product> findAllProductByCriteria(Pageable pageable, String search, Optional<List<String>> filter) {
        return searchEngine.search(pageable, search, filter);
    }

    @Override
    @Transactional
    public boolean isProductAvailable(List<Product> products) {
        List<Product> actualProducts = productRepository.findByIdIn(products.stream().map(Product::getId).toList());
        return actualProducts.size() == products.size() && IntStream.range(0, products.size())
                .noneMatch(index -> products.get(index).getQuantityForSale() > actualProducts.get(index).getQuantityForSale());
    }
}
