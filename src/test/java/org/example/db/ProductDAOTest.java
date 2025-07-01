package org.example.db;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class ProductDAOTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Query<Object> query;

    private ProductDAO productDAO;

    @BeforeEach
    void setUp() {
        this.productDAO = new ProductDAO(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    public void testFindById() {
        Product product = Product.builder().
                productId(1L)
                .name("Product 1")
                .description("Good phone")
                .price(10000.00)
                .build();
        when(session.get(Product.class, 1L)).thenReturn(product);

        Optional<Product> result = productDAO.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Product 1", result.get().getName());
        verify(session).get(Product.class, 1L);
    }

    @Test
    public void testFindAll() {
        Product product = Product.builder().
                productId(1L)
                .name("Product 1")
                .description("Good phone")
                .price(10000.00)
                .build();
        when(session.createQuery(anyString(), any())).thenReturn(query);
        when(query.list()).thenReturn(List.of(product));

        List<Product> allProducts = productDAO.findAll();

        assertNotNull(allProducts);
        assertEquals(1, allProducts.size());
        assertEquals("Product 1", allProducts.get(0).getName());
    }

    @Test
    public void testSaveOrUpdateProduct() {
        Product product = Product.builder()
                .productId(1L)
                .name("Product 1")
                .description("Good phone")
                .price(10000.00)
                .build();
        doNothing().when(session).saveOrUpdate(any(Product.class));
        productDAO.saveOrUpdate(product);
        assertNotNull(productDAO.findById(1L));
        verify(session).saveOrUpdate(product);
    }

    @Test
    public void testDeleteProduct() {
        doNothing().when(session).remove(any(Product.class));
        when(session.get(Product.class,1L)).thenReturn(Product.builder()
                .productId(1L)
                .name("Product 1")
                .description("Good phone")
                .price(10000.00)
                .build());
        productDAO.delete(1L);
        verify(session).get(Product.class,1L);
        verify(session).remove(any(Product.class));
    }


}
