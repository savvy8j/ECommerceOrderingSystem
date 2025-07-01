package org.example.db;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class OrderDAO extends AbstractDAO<Order> {
    public OrderDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<Order> getAllOrders() {
        return query("from Order").list();
    }

    public Order saveOrUpdate(Order order) {
        return persist(order);
    }

    public List<Order> findByUserId(Long userId) {
        return currentSession()
                .createQuery("FROM Order o WHERE o.userId = :userId", Order.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public Optional<Order> findByOrderId(Long orderId) {
        return Optional.ofNullable(get(orderId));
    }



}

