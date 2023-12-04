package com.ecommerce.order.internal.service;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.exception.OrderNotFoundException;
import com.ecommerce.exception.ProductsNotAvailableException;
import com.ecommerce.order.internal.repository.OrderRepository;
import com.ecommerce.product.ProductApi;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class OrderService {

    private OrderRepository orderRepository;
    private MongoTemplate mongoTemplate;
    private ProductApi productApi;

    @Transactional
    public Order checkout(Order order) {
        if (!productApi.isProductAvailable(order.getProducts())) {
            throw new ProductsNotAvailableException();
        }
        return orderRepository.save(order);
    }

    public Order getOrderStatus(String id) {
        return orderRepository.findById(id).orElseThrow(OrderNotFoundException::new);
    }

    @Transactional
    public void changeOrderStatus(String id, OrderStatus orderStatus) {
        Query query = Query.query(Criteria.where("id").is(id));
        Update update = Update.update("status", orderStatus);
        mongoTemplate.updateFirst(query, update, Order.class);
    }
}
