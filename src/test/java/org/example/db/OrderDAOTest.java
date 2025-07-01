package org.example.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDAOTest {


    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<Order> query;

    private OrderDAO orderDAO;

    @BeforeEach
    void setUp() {
        orderDAO = new OrderDAO(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    void testFindById() {
        Order order = Order.builder().orderId(1L).build();
        when(session.get(Order.class, 1L)).thenReturn(order);

        Optional<Order> result = orderDAO.findByOrderId(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getOrderId());
        verify(session).get(Order.class, 1L);
    }

    @Test
    void testFindAll() {
        when(session.createQuery(anyString(), eq(Order.class))).thenReturn(query);
        when(query.list()).thenReturn(List.of(Order.builder().orderId(1L).build()));

        List<Order> orders = orderDAO.getAllOrders();

        assertEquals(1, orders.size());
        verify(session).createQuery(anyString(), eq(Order.class));
    }

    @Test
    void testSaveOrUpdate() {
        Order order = Order.builder().orderId(1L).build();
        doNothing().when(session).saveOrUpdate(order);
        orderDAO.saveOrUpdate(order);
        verify(session).saveOrUpdate(order);
    }

}
