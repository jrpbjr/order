package com.jrpbjr.order.api.service;


import com.jrpbjr.order.api.entity.OrderEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    public OrderService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    public OrderEntity enfileirarPedido(OrderEntity orderEntity) {
        rabbitTemplate.convertAndSend(exchangeName, "", orderEntity);
        logger.info("Pedido enfileirado: {}", orderEntity.toString());
        return orderEntity;
    }

}
