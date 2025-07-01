package org.example.core;

import org.example.api.OrderItemDTO;
import org.example.api.PlaceOrderRequestDTO;
import org.example.db.Order;
import org.example.db.OrderDAO;
import org.example.db.OrderItem;
import org.example.db.Product;
import org.example.exception.OrderFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderDAO orderDAO;

    @Mock
    private ProductService productService;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderDAO, productService);
    }


    @Test
    public void testGetAllOrders() {
        Order order = Order.builder()
                .orderId(1L)
                .userId(8L)
                .totalAmount(200.0)
                .status(Status.PLACED)
                .orderDate(LocalDate.now())
                .message("Placed")
                .orderItems(List.of(
                        OrderItem.builder()
                                .id(1L)
                                .productId(1L)
                                .quantityOrdered(2)
                                .priceAtOrderTime(100.0)
                                .build()
                ))
                .build();
        when(orderDAO.getAllOrders()).thenReturn(List.of(order));

        List<Order> orders = orderService.getAllOrders();

        assertEquals(1, orders.size());
        verify(orderDAO).getAllOrders();
    }

    @Test
    public void testGetOrdersByUserId() {
        Order order = Order.builder()
                .orderId(1L)
                .userId(8L)
                .totalAmount(200.0)
                .status(Status.PLACED)
                .orderDate(LocalDate.now())
                .message("Placed")
                .orderItems(List.of(
                        OrderItem.builder()
                                .id(1L)
                                .productId(1L)
                                .quantityOrdered(2)
                                .priceAtOrderTime(100.0)
                                .build()
                ))
                .build();
        when(orderDAO.findByUserId(8L)).thenReturn(List.of(order));

        List<Order> orders = orderService.getOrdersByUserId(8L);

        assertEquals(1, orders.size());
        verify(orderDAO).findByUserId(8L);
    }

    @Test
    public void testPlaceOrderSuccess() {
        Product product = Product.builder()
                .productId(1L)
                .price(100.0)
                .quantityAvailable(10)
                .build();

        when(productService.findById(anyLong())).thenReturn(product);
        when(productService.saveOrUpdate(any(Product.class))).thenReturn(product);

        when(orderDAO.saveOrUpdate(any(Order.class))).thenReturn(
                Order.builder()
                        .orderId(1L)
                        .status(Status.PLACED)
                        .message("Placed Order")
                        .totalAmount(400.0)
                        .build()
        );

        PlaceOrderRequestDTO request = new PlaceOrderRequestDTO();
        request.setUserId(123L);
        request.setOrderItems(List.of(
                OrderItemDTO.builder()
                        .productId(1L)
                        .quantityOrdered(4)
                        .build()
        ));

        Order result = orderService.placeOrder(request);
        assertEquals(Status.PLACED, result.getStatus());
        assertEquals("Placed Order", result.getMessage());
        assertEquals(400.0, result.getTotalAmount());
        verify(orderDAO).saveOrUpdate(any(Order.class));
    }


    @Test
    void testPlaceOrderQuantityExceeded() {
        Product product = Product.builder()
                .productId(6L)
                .name("Phone")
                .price(500.0)
                .quantityAvailable(10)
                .build();

        when(productService.findById(6L)).thenReturn(product);
        PlaceOrderRequestDTO request = new PlaceOrderRequestDTO();


        request.setUserId(123L);
        request.setOrderItems(List.of(
                OrderItemDTO.builder()
                        .productId(6L)
                        .quantityOrdered(14)
                        .build()
        ));

        assertThrows(OrderFailedException.class, () -> orderService.placeOrder(request));

        verify(orderDAO).saveOrUpdate(any(Order.class));
    }

    @Test
    void testDeleteUserOrderByIdSuccess() {
        Order order = Order.builder()
                .orderId(1L)
                .userId(8L)
                .status(Status.PLACED)
                .message("Placed Order")
                .totalAmount(200.0)
                .orderDate(LocalDate.now())
                .orderItems(List.of(
                        OrderItem.builder()
                                .id(1L)
                                .productId(1L)
                                .quantityOrdered(2)
                                .build()))
                .build();

        OrderItem orderItem = order.getOrderItems().get(0);
        Product product = Product.builder()
                .productId(orderItem.getProductId())
                .quantityAvailable(10)
                .build();

        when(orderDAO.findByOrderId(1L)).thenReturn(Optional.of(order));
        when(productService.findById(orderItem.getProductId())).thenReturn(product);

        orderService.deleteUserOrderById(1L, 8L);

        verify(productService).saveOrUpdate(any(Product.class));
        verify(orderDAO).saveOrUpdate(any(Order.class));
    }

    @Test
    void testDeleteUserOrderByIdAlreadyCancelled() {
        Order order=Order.builder()
                .orderId(1L)
                .userId(8L)
                .status(Status.CANCELLED)
                .message("Cancelled Order")
                .totalAmount(200.0)
                .orderDate(LocalDate.now())
                .orderItems(List.of(
                        OrderItem.builder()
                                .id(1L)
                                .productId(1L)
                                .quantityOrdered(2)
                                .build()))
                .build();
        order.setStatus(Status.CANCELLED);

        when(orderDAO.findByOrderId(1L)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class, () -> orderService.deleteUserOrderById(1L, 8L));
    }

    @Test
    void testDeleteByAdminSuccess() {
        Order order = Order.builder()
                .orderId(1L)
                .userId(8L)
                .status(Status.CANCELLED)
                .message("Cancelled Order")
                .totalAmount(200.0)
                .orderDate(LocalDate.now())
                .orderItems(List.of(
                        OrderItem.builder()
                                .id(1L)
                                .productId(1L)
                                .quantityOrdered(2)
                                .build()))
                .build();
        order.setStatus(Status.PLACED);
        OrderItem orderItem = order.getOrderItems().get(0);
        Product product = Product.builder()
                .productId(orderItem.getProductId())
                .quantityAvailable(10)
                .build();

        when(orderDAO.findByOrderId(1L)).thenReturn(Optional.of(order));
        when(productService.findById(orderItem.getProductId())).thenReturn(product);

        orderService.deleteOrderById(1L, 8L, true);

        verify(productService).saveOrUpdate(any(Product.class));
        verify(orderDAO).saveOrUpdate(any(Order.class));
    }

    @Test
    void testDeleteOrderByIdWrongUser() {
        Order order = Order.builder()
                .orderId(1L)
                .userId(8L)
                .status(Status.PLACED)
                .message("Placed Order")
                .totalAmount(200.0)
                .orderDate(LocalDate.now())
                .orderItems(List.of(
                        OrderItem.builder()
                                .id(1L)
                                .productId(1L)
                                .quantityOrdered(2)
                                .build()))
                .build();


        when(orderDAO.findByOrderId(1L)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class, () -> orderService.deleteOrderById(1L, 99L, false));
    }


}