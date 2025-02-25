package com.jrpbjr.orders.processor.service;

import com.jrpbjr.orders.processor.controller.dto.OrderResponse;
import com.jrpbjr.orders.processor.entity.OrderEntity;
import com.jrpbjr.orders.processor.entity.OrderItem;
import com.jrpbjr.orders.processor.listener.dto.OrderCreatedEvent;
import com.jrpbjr.orders.processor.repository.OrderRepository;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);


    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;

    public OrderService(OrderRepository orderRepository,
                        MongoTemplate mongoTemplate) {
        this.orderRepository = orderRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public void save(OrderCreatedEvent event) {
        // Verifica se o orderId já existe no banco
        if (orderRepository.existsById(event.orderId())) {
            // Registrar no log e descartar o evento
            logger.info("Order with ID {} already exists. Event will be discarded.", event.orderId());
            return;
        }

        // Se não existir, processa e salva o pedido
        var entity = new OrderEntity();

        entity.setOrderId(event.orderId());
        entity.setCustomerId(event.customerId());
        entity.setItems(getOrderItems(event));
        entity.setTotal(getTotal(event));

        orderRepository.save(entity);
        logger.info("Order with ID {} has been successfully saved.", event.orderId());
    }


    public Page<OrderResponse> findAllByCustomerId(Long customerId, PageRequest pageRequest) {
        var orders = orderRepository.findAllByCustomerId(customerId, pageRequest);

        return orders.map(OrderResponse::fromEntity);
    }

    public BigDecimal findTotalOnOrdersByCustomerId(Long customerId) {
        var aggregations = newAggregation(
                match(Criteria.where("customerId").is(customerId)),
                group().sum("total").as("total")
        );

        var response = mongoTemplate.aggregate(aggregations, "tb_orders", Document.class);

        return new BigDecimal(response.getUniqueMappedResult().get("total").toString());
    }

    private BigDecimal getTotal(OrderCreatedEvent event) {
        return event.items()
                .stream()
                .map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private static List<OrderItem> getOrderItems(OrderCreatedEvent event) {
        return event.items().stream()
                .map(i -> new OrderItem(i.product(), i.quantity(), i.price()))
                .toList();
    }
}
