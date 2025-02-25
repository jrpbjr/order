package com.jrpbjr.order.api.controller;

import com.jrpbjr.order.api.entity.OrderEntity;
import com.jrpbjr.order.api.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock  // Simula o comportamento do serviço
    private OrderService orderService;

    @InjectMocks  // Injeta o mock (simulação) no controlador
    private OrderController orderController;

    @BeforeEach  // Inicializa os mocks antes de cada teste
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatedOrder_Success() {
        // Arrange (Preparação)
        OrderEntity inputOrder = new OrderEntity();
        inputOrder.setOrderId(1L);
        inputOrder.setCustomerId(26L);

        OrderEntity processedOrder = new OrderEntity();
        processedOrder.setOrderId(1L);
        processedOrder.setCustomerId(26L);

        // Simula o comportamento do service
        when(orderService.enfileirarPedido(inputOrder)).thenReturn(processedOrder);

        // Act (Execução)
        ResponseEntity<OrderEntity> response = orderController.createdOrder(inputOrder);

        // Assert (Verificação)
        assertEquals(201, response.getStatusCodeValue()); // Verifica o status HTTP
        assertEquals(processedOrder, response.getBody()); // Verifica o corpo da resposta
        verify(orderService, times(1)).enfileirarPedido(inputOrder); // Verifica se o serviço foi chamado uma vez
    }

    @Test
    void testCreatedOrder_EmptyOrder() {
        // Arrange
        OrderEntity emptyOrder = new OrderEntity(); // Pedido vazio

        when(orderService.enfileirarPedido(emptyOrder)).thenReturn(emptyOrder);

        // Act
        ResponseEntity<OrderEntity> response = orderController.createdOrder(emptyOrder);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(emptyOrder, response.getBody());
        verify(orderService, times(1)).enfileirarPedido(emptyOrder); // Deve ser chamado uma vez
    }


}
