package org.example.core;

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

        double discount = 0.0;

        if(placeOrderRequestDTO.getPromoCode() != null) {
            PromoCode promoCode = promoService.getPromocode(placeOrderRequestDTO.getPromoCode());
            if(!promoCode.isActive()) {
                throw new OrderFailedException("Promo code is inactive");
            }
            LocalDate todayDate = LocalDate.now();
            if(todayDate.isBefore(promoCode.getStartDate()) || todayDate.isAfter(promoCode.getEndDate())) {
                throw new OrderFailedException("Promo code is expired");
            }

            Long totalUsage = promoService.getTotalPromoUsage(promoCode.getId());
            if (totalUsage == null) {
                totalUsage = 0L;
            }
            if ( totalUsage >= promoCode.getMaxUsageLimit()) {
                throw new OrderFailedException("Max Promo Usage limit exceeded");
            }

            Integer userUsage = promoService.getUsageCountForCustomer(promoCode.getId(), placeOrderRequestDTO.getUserId());
            if (userUsage >= promoCode.getMaxUsagePerCustomer()) {
                throw new OrderFailedException("Promo code usage limit exceeded for this customer");
            }


            if(promoCode.getPromoType() == PromoType.PERCENTAGE)
            {
                discount = totalPrice[0] * promoCode.getDiscount()/100.0;
                if(discount >promoCode.getMaxDiscount())
                    discount = promoCode.getMaxDiscount();
            }
            else if(promoCode.getPromoType() == PromoType.FLAT)
            {
                discount = promoCode.getDiscount();
                if(discount >promoCode.getMaxDiscount())
                    discount = promoCode.getMaxDiscount();
            }
            totalPrice[0] -=discount;
            if(totalPrice[0] < 0){
                totalPrice[0] = 0;}
            Optional<PromoUsage> promoUsageOpt = promoService.getPromoUsageByUser(
                    promoCode.getId(),
                    placeOrderRequestDTO.getUserId()
            );

            PromoUsage promoUsage;


            if (promoUsageOpt.isPresent()) {
                promoUsage = promoUsageOpt.get();
                promoUsage.setUsageCount(promoUsage.getUsageCount() + 1);
            } else {
                promoUsage = new PromoUsage();
                promoUsage.setUsageCount(1);
                promoUsage.setUserId(placeOrderRequestDTO.getUserId());
                promoUsage.setPromocode(promoCode);
            }

            PromoUsageDTO promoUsageDTO = new PromoUsageDTO();
            promoUsageDTO.setPromoCodeId(promoCode.getId());
            promoUsageDTO.setUserId(promoUsage.getUserId());
            promoUsageDTO.setUsageCount(promoUsage.getUsageCount());
            promoService.saveOrUpdatePromoUsage(promoUsageDTO);
            order.setMessage("Placed Order with PromoCode"+ promoCode.getCode());



        }
        order.setStatus(Status.PLACED);
        order.setUserId(placeOrderRequestDTO.getUserId());

        order.setOrderDate(LocalDate.now());
        order.setOrderItems(orderItems);
        order.setTotalAmount(totalPrice[0]);


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

