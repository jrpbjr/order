package com.jrpbjr.orders.processor.listener.dto;

import java.math.BigDecimal;

public record OrderItemEvent(String product,
                             Integer quantity,
                             BigDecimal price) {
}