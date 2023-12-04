package com.ecommerce.product.internal.service.search;

import com.ecommerce.entity.Filter;
import com.ecommerce.entity.FilterOperator;
import com.ecommerce.entity.FilterType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FilterParser {
    /**
     * This pattern is used to define a range filter
     */
    private static final String BETWEEN_REGEX = "\\d+-\\d+";
    private static final String KEY_VALUE_DIVIDE_REGEX = ",";
    private FieldType fieldType;

    public Optional<List<Filter>> parse(Optional<List<String>> filters) {
        return filters.flatMap(filtersArr -> filtersArr.isEmpty() ? Optional.empty() : Optional.of(filtersArr.stream()
                .map(filter -> filter.split(KEY_VALUE_DIVIDE_REGEX))
                .collect(Collectors.groupingBy(parts -> parts[0], Collectors.mapping(parts -> parts[1],
                        Collectors.toList()))).entrySet().stream()
                .map(this::createFilter)
                .toList())
        );
    }

    private Filter createFilter(Map.Entry<String, List<String>> entry) {
        FilterType type = fieldType.getFieldTypeByName(entry.getKey());
        FilterOperator operator = getOperator(entry.getValue().get(0));
        return Filter.builder().name(entry.getKey()).value(entry.getValue()).type(type).operator(operator).build();
    }

    private FilterOperator getOperator(String value) {
        if (value.matches(BETWEEN_REGEX)) {
            return FilterOperator.BETWEEN;
        } else {
            return FilterOperator.EQUALS;
        }
    }
}
