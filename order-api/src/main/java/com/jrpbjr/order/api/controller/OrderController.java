package com.jrpbjr.order.api.controller;

import com.jrpbjr.order.api.entity.OrderEntity;
import com.jrpbjr.order.api.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Orders", description = "Contém a operação para realização de pedidos")
@RestController
@RequestMapping("/api/v1/Orders")
public class OrderController {

    private final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @Operation(summary = "Cria um novo pedido", description = "Recurso para criar um novo pedido",
            responses = {@ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderEntity.class)))
            })
    @PostMapping
    public ResponseEntity<OrderEntity> createdOrder(@RequestBody OrderEntity orderEntity) {
        logger.info("Pedido recebido: {}", orderEntity.toString());
        orderEntity = service.enfileirarPedido(orderEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderEntity);
    }

}
