package org.example.core;

import org.example.api.ApplyPromoCodeDTO;
import org.example.api.PlaceOrderRequestDTO;
import org.example.api.PromoUsageDTO;
import org.example.db.*;
import org.example.exception.OrderFailedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderService {
    private final OrderDAO orderDAO;


    private final ProductService productService;
    private final PromoService promoService;

    public OrderService(OrderDAO orderDAO, ProductService productService, PromoService promoService) {
        this.orderDAO = orderDAO;
        this.productService = productService;
        this.promoService = promoService;
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderDAO.findByUserId(userId);

    }

    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }

    public Order placeOrder(PlaceOrderRequestDTO placeOrderRequestDTO) {

        List<OrderItem> orderItems = new ArrayList<>();
        final double[] totalPrice = {0};

        Order order = new Order();
        placeOrderRequestDTO.getOrderItems().forEach(requestedOrderItem -> {
            Product product = productService.findById(requestedOrderItem.getProductId());
            if (product.getQuantityAvailable() < requestedOrderItem.getQuantityOrdered()) {
//                Order order = new Order();
                order.setStatus(Status.FAILED);
                order.setUserId(placeOrderRequestDTO.getUserId());
                order.setMessage("Quantity Exceeded");
                orderDAO.saveOrUpdate(order);

                throw new OrderFailedException("Quantity Exceeded");
            }

            //valid case
            product.setQuantityAvailable(product.getQuantityAvailable() - requestedOrderItem.getQuantityOrdered());
            productService.saveOrUpdate(product);

            double price = product.getPrice() * requestedOrderItem.getQuantityOrdered();
            OrderItem orderItemDb = new OrderItem();

            orderItemDb.setQuantityOrdered(requestedOrderItem.getQuantityOrdered());
            orderItemDb.setPriceAtOrderTime(price);
            orderItemDb.setProductId(product.getProductId());

            orderItemDb.setOrder(order);
            orderItems.add(orderItemDb);
            totalPrice[0] += price;
        });

        ApplyPromoCodeDTO applyPromoCodeDTO = new ApplyPromoCodeDTO();
        applyPromoCodeDTO.setPromoCode(placeOrderRequestDTO.getPromoCode());
        applyPromoCodeDTO.setUserId(placeOrderRequestDTO.getUserId());
        applyPromoCodeDTO.setTotal(totalPrice[0]);

         applyPromoCodeDTO = promoService.applyPromoCode(applyPromoCodeDTO);

        order.setStatus(Status.PLACED);
        order.setUserId(placeOrderRequestDTO.getUserId());

        order.setOrderDate(LocalDate.now());
        order.setOrderItems(orderItems);
        double totalAmount = applyPromoCodeDTO.getDiscountedTotal()!=null?applyPromoCodeDTO.getDiscountedTotal():totalPrice[0];
        order.setTotalAmount(totalAmount);



        return orderDAO.saveOrUpdate(order);


    }

    public void deleteUserOrderById(Long orderId, Long userId) {
        Order order = orderDAO.findByOrderId(orderId)
                .orElseThrow(() -> new OrderFailedException("Order not found"));

        if (order.getStatus() == Status.CANCELLED) {
            throw new RuntimeException("Order can't be cancelled");
        }

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("You are not allowed to cancel this order.");
        }

        order.getOrderItems().forEach(requestedOrderItem -> {
            Product product = productService.findById(requestedOrderItem.getProductId());
            product.setQuantityAvailable(product.getQuantityAvailable() + requestedOrderItem.getQuantityOrdered());
            productService.saveOrUpdate(product);
        });

        order.setStatus(Status.CANCELLED);
        order.setMessage("Order cancelled successfully");

        orderDAO.saveOrUpdate(order);
    }


    public void deleteOrderById(Long orderId,Long userId,boolean isAdmin) {
        Order order = orderDAO.findByOrderId(orderId)
                .orElseThrow(() -> new OrderFailedException("Order not found"));

        if(order.getStatus() == Status.CANCELLED) {
            throw new RuntimeException("Order cant be cancelled");
        }

        if (!isAdmin && !order.getUserId().equals(userId)) {
            throw new RuntimeException("You are not allowed to cancel this order.");
        }

        order.getOrderItems().forEach(requestedOrderItem -> {
            Product product = productService.findById(requestedOrderItem.getProductId());
            product.setQuantityAvailable(product.getQuantityAvailable() + requestedOrderItem.getQuantityOrdered());
            productService.saveOrUpdate(product);
        });
        order.setStatus(Status.CANCELLED);
        order.setMessage("Order cancelled successfully");

        orderDAO.saveOrUpdate(order);


    }

}

