package com.jrpbjr.order.api.service;

import com.jrpbjr.order.api.entity.OrderEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks do Mockito.

        // Usa Reflexão para acessar e modificar o campo privado 'exchangeName'.
        Field field = OrderService.class.getDeclaredField("exchangeName");
        field.setAccessible(true); // Torna o campo acessível.
        field.set(orderService, "test-exchange"); // Define o valor do 'exchangeName'.
    }

    @Test
    void testEnfileirarPedido_Success() {
        // Arrange (Preparação)
        OrderEntity order = new OrderEntity();
        order.setOrderId(1L);
        order.setCustomerId(123L);
        order.setTotal(BigDecimal.valueOf(100.50));

        // Act (Execução)
        OrderEntity result = orderService.enfileirarPedido(order);

        // Assert (Verificações)
        verify(rabbitTemplate, times(1)).convertAndSend("test-exchange", "", order); // Verifica o envio correto para o RabbitTemplate.
        assertEquals(order, result);

    }
}

