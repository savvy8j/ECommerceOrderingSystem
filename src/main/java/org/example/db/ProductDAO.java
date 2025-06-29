package org.example.db;

import io.dropwizard.hibernate.AbstractDAO;
import org.example.db.Product;
import org.example.exception.ProductNotFoundException;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class ProductDAO extends AbstractDAO<Product> {

    public ProductDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Product saveOrUpdate(Product product) {
        return persist(product);
    }

    public Optional<Product> findById(Long productId) {
        return Optional.ofNullable(get(productId));
    }

    public List<Product> findAll() {
        return query("from Product").list();
    }



    public void delete(Long productId) {
        findById(productId).ifPresent(product -> currentSession().remove(product));
    }




}
