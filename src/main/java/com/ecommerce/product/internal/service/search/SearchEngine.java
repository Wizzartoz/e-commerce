package com.ecommerce.product.internal.service.search;

import com.ecommerce.entity.Filter;
import com.ecommerce.entity.FilterType;
import com.ecommerce.entity.Product;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@AllArgsConstructor
public class SearchEngine {

    private MongoTemplate mongoTemplate;
    private FilterParser filterParser;

    /**
     * This regex divides range filter parameter
     * Example: price=100-300
     */
    private static final String RANGE_SEPARATION = "-";

    public List<Product> search(Pageable pageable, String search, Optional<List<String>> filters) {
        return searchProductByMultipleCriteria(pageable, search, filterParser.parse(filters));
    }

    private List<Product> searchProductByMultipleCriteria(Pageable pageable, String search, Optional<List<Filter>> filters) {
        if (!Objects.isNull(search)) {
            List<Product> products = performTextSearch(pageable, search, filters);
            if (products.isEmpty()) {
                return performRegexSearch(pageable, search, filters);
            }
            return products;
        } else {
            return performGeneralSearch(pageable, filters);
        }
    }

    private List<Product> performTextSearch(Pageable pageable, String search, Optional<List<Filter>> filters) {
        TextCriteria textCriteria = TextCriteria.forDefaultLanguage().matching(search);
        TextQuery textQuery = TextQuery.queryText(textCriteria);
        textQuery.with(pageable);
        filters.ifPresent(filterArr -> textQuery.addCriteria(new Criteria().andOperator(addFilter(filterArr).toArray(new Criteria[0]))));
        return mongoTemplate.find(textQuery, Product.class);
    }

    private List<Product> performRegexSearch(Pageable pageable, String search, Optional<List<Filter>> filters) {
        List<Criteria> criteria = new ArrayList<>();
        //TODO Ensure logical retrieval of fields via reflection from @TextIndex and cache them.
        criteria.add(new Criteria().orOperator(
                Criteria.where("name").regex(search, "i"),
                Criteria.where("description").regex(search, "i")
        ));
        filters.ifPresent(filterArr -> criteria.addAll(addFilter(filterArr)));
        Query regexQuery = new Query(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        regexQuery.with(pageable);
        return mongoTemplate.find(regexQuery, Product.class);
    }

    private List<Product> performGeneralSearch(Pageable pageable, Optional<List<Filter>> filters) {
        Query query = new Query();
        query.with(pageable);
        filters.ifPresent(filterArr -> query.addCriteria(new Criteria().andOperator(addFilter(filterArr).toArray(new Criteria[0]))));
        return mongoTemplate.find(query, Product.class);
    }

    private List<Criteria> addFilter(List<Filter> filters) {
        Map<String, List<Criteria>> criteriaMap = new HashMap<>();
        for (Filter filter : filters) {
            switch (filter.getOperator()) {
                case EQUALS -> criteriaMap.computeIfAbsent(filter.getName(), k
                        -> new ArrayList<>()).add(createEqualsCriteria(filter));
                case BETWEEN -> criteriaMap.computeIfAbsent(filter.getName(), k
                        -> new ArrayList<>()).addAll(createBetweenCriteria(filter));
            }
        }
        return divideCriteriaByLogicOperator(criteriaMap);
    }

    private Criteria createEqualsCriteria(Filter filter) {
        if (filter.getType().equals(FilterType.INTEGER)) {
            //this piece of code convert the string to an integer because otherwise MongoDB will search by text
            List<Integer> values = filter.getValue()
                    .stream()
                    .map(Integer::parseInt)
                    .toList();
            return Criteria.where(filter.getName()).in(values);
        } else {
            return Criteria.where(filter.getName()).in(filter.getValue());
        }
    }

    private List<Criteria> createBetweenCriteria(Filter filter) {
        return filter.getValue()
                .stream()
                .map(value -> value.split(RANGE_SEPARATION))
                .map(range -> Criteria.where(filter.getName()).gte(Integer.parseInt(range[0])).lte(Integer.parseInt(range[1])))
                .toList();
    }

    private List<Criteria> divideCriteriaByLogicOperator(Map<String, List<Criteria>> criteriaMap) {
        List<Criteria> overallCriteria = new ArrayList<>();
        for (Map.Entry<String, List<Criteria>> entry : criteriaMap.entrySet()) {
            List<Criteria> localCriteria = entry.getValue();
            if (isOrOperator(localCriteria)) {
                overallCriteria.add(new Criteria().orOperator(localCriteria.toArray(new Criteria[0])));
            } else {
                overallCriteria.addAll(localCriteria);
            }
        }
        return overallCriteria;
    }

    private boolean isOrOperator(List<Criteria> criteria) {
        return criteria.size() > 1;
    }
}
