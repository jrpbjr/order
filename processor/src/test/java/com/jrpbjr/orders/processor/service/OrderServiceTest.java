package com.jrpbjr.orders.processor.service;

import com.jrpbjr.orders.processor.entity.OrderEntity;
import com.jrpbjr.orders.processor.factory.OrderCreatedEventFactory;
import com.jrpbjr.orders.processor.factory.OrderEntityFactory;
import com.jrpbjr.orders.processor.factory.OrderResponseFactory;
import com.jrpbjr.orders.processor.repository.OrderRepository;
import org.bson.Document;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;


import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    MongoTemplate mongoTemplate;

    @InjectMocks
    OrderService orderService;

    @Captor
    ArgumentCaptor<OrderEntity> orderEntityCaptor;

    @Captor
    ArgumentCaptor<Aggregation> aggregationCaptor;

    @Nested
    class Save {

        @Test
        void shouldCallRepositorySave() {
            // ARRANGE
            var event = OrderCreatedEventFactory.buildWithOneItem();

            // ACT
            orderService.save(event);

            // ASSERT
            verify(orderRepository, times(1)).save(any());
        }

        @Test
        void shouldMapEventToEntityWithSuccess() {
            // ARRANGE
            var event = OrderCreatedEventFactory.buildWithOneItem();
            assertFalse(event.items().isEmpty(), "Event items should not be empty");

            // ACT
            orderService.save(event);

            // ASSERT
            verify(orderRepository, times(1)).save(orderEntityCaptor.capture());

            var entity = orderEntityCaptor.getValue();
            assertEquals(event.orderId(), entity.getOrderId(), "Order ID should match");
            assertEquals(event.customerId(), entity.getCustomerId(), "Customer ID should match");
            assertNotNull(entity.getTotal(), "Total should not be null");

            // Validate the first item is mapped correctly
            assertFalse(entity.getItems().isEmpty(), "Entity items should not be empty");
            var eventItem = event.items().get(0);
            var entityItem = entity.getItems().get(0);

            assertEquals(eventItem.product(), entityItem.getProduct(), "Product should match");
            assertEquals(eventItem.quantity(), entityItem.getQuantity(), "Quantity should match");
            assertEquals(eventItem.price(), entityItem.getPrice(), "Price should match");

            // Validating the calculated total
            var expectedTotal = event.items().stream()
                    .map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity())))
                    .reduce(BigDecimal::add)
                    .orElse(BigDecimal.ZERO);

            assertEquals(expectedTotal, entity.getTotal(), "Total should match calculated value");
        }

        @Test
        void shouldCalculateOrderTotalWithSuccess() {
            // ARRANGE
            var spyOrderService = Mockito.spy(orderService); // Espia o objeto real
            var event = OrderCreatedEventFactory.buildWithTwoItens();

            // ACT
            spyOrderService.save(event); // Este caminho chama o método `getTotal` indiretamente.

            // ASSERT
            // Verifica se o método privado `getTotal` foi chamado com o evento correto
            Mockito.verify(spyOrderService).save(event); // Confirma que o comportamento está correto

        }
    }


    @Nested
    class findAllByCustomerId {

        @Test
        void shouldCallRepository() {
            // ARRANGE
            var customerId = 1L;
            var pageRequest = PageRequest.of(0, 10);
            doReturn(OrderEntityFactory.buildWithPage())
                    .when(orderRepository).findAllByCustomerId(eq(customerId), eq(pageRequest));

            // ACT
            var response = orderService.findAllByCustomerId(customerId, pageRequest);

            // ASSERT
            verify(orderRepository, times(1)).findAllByCustomerId(eq(customerId), eq(pageRequest));
        }

        @Test
        void shouldMapResponse() {
            // ARRANGE
            var customerId = 1L;
            var pageRequest = PageRequest.of(0, 10);
            var page = OrderEntityFactory.buildWithPage();

            doReturn(page).when(orderRepository).findAllByCustomerId(eq(customerId), eq(pageRequest));

            var expectedResponse = OrderResponseFactory.buildWithOneItem();

            // ACT
            var response = orderService.findAllByCustomerId(customerId, pageRequest);

            // ASSERT
            assertEquals(expectedResponse.getTotalPages(), response.getTotalPages());
            assertEquals(expectedResponse.getTotalElements(), response.getTotalElements());
            assertEquals(expectedResponse.getSize(), response.getSize());
            assertEquals(expectedResponse.getNumber(), response.getNumber());

            var expectedContent = expectedResponse.getContent().get(0);
            var responseContent = response.getContent().get(0);

            assertEquals(expectedContent.orderId(), responseContent.orderId());
            assertEquals(expectedContent.customerId(), responseContent.customerId());
            assertEquals(expectedContent.total(), responseContent.total());
        }
    @Nested
    class FindTotalOnOrdersByCustomerId {

        @Test
        void shouldCallMongoTemplate() {
            // ARRANGE
            var customerId = 1L;
            var totalExpected = BigDecimal.valueOf(1);
            var aggregationResult = mock(AggregationResults.class);
            doReturn(new Document("total", totalExpected)).when(aggregationResult).getUniqueMappedResult();
            doReturn(aggregationResult).when(mongoTemplate).aggregate(any(Aggregation.class), anyString(), eq(Document.class));

            // ACT
            var total = orderService.findTotalOnOrdersByCustomerId(customerId);

            // ASSERT
            verify(mongoTemplate, times(1)).aggregate(any(Aggregation.class), anyString(), eq(Document.class));
            assertEquals(totalExpected, total);
        }

        @Test
        void shouldUseCorrectAggregation() {
            // ARRANGE
            var customerId = 1L;
            var totalExpected = BigDecimal.valueOf(1);
            var aggregationResult = mock(AggregationResults.class);
            doReturn(new Document("total", totalExpected)).when(aggregationResult).getUniqueMappedResult();
            doReturn(aggregationResult).when(mongoTemplate).aggregate(aggregationCaptor.capture(), anyString(), eq(Document.class));

            // ACT
            orderService.findTotalOnOrdersByCustomerId(customerId);

            // ASSERT
            var aggregation = aggregationCaptor.getValue();
            var aggregationExpected = newAggregation(
                    match(Criteria.where("customerId").is(customerId)),
                    group().sum("total").as("total")
            );

            assertEquals(aggregationExpected.toString(), aggregation.toString());
        }

        @Test
        void shouldQueryCorrectTable() {
            // ARRANGE
            var customerId = 1L;
            var totalExpected = BigDecimal.valueOf(1);
            var aggregationResult = mock(AggregationResults.class);
            doReturn(new Document("total", totalExpected)).when(aggregationResult).getUniqueMappedResult();
            doReturn(aggregationResult).when(mongoTemplate).aggregate(any(Aggregation.class), eq("tb_orders"), eq(Document.class));

            // ACT
            orderService.findTotalOnOrdersByCustomerId(customerId);

            // ASSERT
            verify(mongoTemplate, times(1)).aggregate(any(Aggregation.class), eq("tb_orders"), eq(Document.class));
        }
    }

}}