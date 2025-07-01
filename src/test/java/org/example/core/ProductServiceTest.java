package org.example.core;

import org.example.db.Product;
import org.example.db.ProductDAO;
import org.example.exception.ProductNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductDAO productDAO;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        this.productService = new ProductService(productDAO);
    }

    @Test
    public void testAddProduct() {
        when(productDAO.saveOrUpdate(any(Product.class))).thenReturn(Product.builder()
                .productId(1L)
                .name("Product 1")
                .description("Good phone")
                .price(10000.00)
                .quantityAvailable(10)
                .build());

        Product product = productService.saveOrUpdate(Product.builder()
                .productId(1L)
                .name("Product 1")
                .description("Good phone")
                .price(10000.00)
                .quantityAvailable(10)
                .build());
        verify(productDAO).saveOrUpdate(any(Product.class));
        Assertions.assertEquals("Product 1", product.getName());

    }

    @Test
    public void testGetAllProducts() {
        when(productDAO.findAll()).thenReturn(List.of(Product.builder()
                .productId(1L)
                .name("Product 1")
                .description("Good phone")
                .price(10000.00)
                .build(), Product.builder()
                .productId(2L)
                .name("Product 2")
                .description("Good Laptop")
                .price(20000.00)
                .build()));
        List<Product> allProducts = productService.findAll();
        System.out.println(allProducts);
        assertEquals(2, allProducts.size());
        assertEquals("Product 1", allProducts.get(0).getName());
        assertEquals("Product 2", allProducts.get(1).getName());
    }

    @Test
    public void testGetProductById() {
        when(productDAO.findById(1L)).thenReturn(Optional.of(Product.builder()
                .productId(1L)
                .name("Product 1")
                .description("Good phone")
                .price(10000.00)
                .build()));
        Product productById = productService.findById(1L);
        assertEquals("Product 1", productById.getName());
        verify(productDAO).findById(eq(1L));
    }

    @Test
    public void testProductUpdateStock() {
        Product product = Product.builder()
                .productId(1L)
                .name("Product 1")
                .description("Good phone")
                .quantityAvailable(10)
                .price(10000.00)
                .build();
        when(productDAO.findById(1L)).thenReturn(Optional.of(product));
        when(productDAO.saveOrUpdate(any(Product.class))).thenReturn(product);
        productService.updateStock(1L, 20);
        assertEquals(20, product.getQuantityAvailable());
        verify(productDAO).saveOrUpdate(product);

    }

    @Test
    public void testProductDelete() {
        Product product = Product.builder()
                .productId(1L)
                .name("Product 1")
                .description("Good phone")
                .quantityAvailable(10)
                .price(10000.00)
                .build();
        when(productDAO.findById(1L)).thenReturn(Optional.of(product));
        productService.delete(1L);

        verify(productDAO).findById(anyLong());
        verify(productDAO).delete(anyLong());
    }


    @Test
    public void deleteProductWhenEmployeeNotFound() {
        Assertions.assertThrows(ProductNotFoundException.class, () -> productService.delete(1L));
        verify(productDAO).findById(anyLong());
        verify(productDAO,never()).delete(anyLong());


    }


}