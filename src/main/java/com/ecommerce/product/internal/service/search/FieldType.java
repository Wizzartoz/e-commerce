package com.ecommerce.product.internal.service.search;

import com.ecommerce.entity.FilterType;
import com.ecommerce.entity.Product;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class FieldType {
    @Cacheable("fieldType")
    public FilterType getFieldTypeByName(String name) {
        try {
            Field field = Product.class.getDeclaredField(name);
            field.setAccessible(true);
            return FilterType.valueOf(field.getType().getSimpleName().toUpperCase());
        } catch (NoSuchFieldException e) {
            //TODO make a log here
            throw new IllegalArgumentException("Invalid field name");
        }
    }
}
