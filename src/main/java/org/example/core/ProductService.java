package org.example.core;

import org.example.db.Product;
import org.example.db.ProductDAO;
import org.example.exception.ProductNotFoundException;

import java.util.List;

public class ProductService {
    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }


    public Product saveOrUpdate(Product product) {
        if (product == null || product.getName() == null || product.getPrice() < 0 || product.getQuantityAvailable() < 0) {
            throw new IllegalArgumentException("Invalid product details.");
        }
        return productDAO.saveOrUpdate(product);
    }

    public List<Product> findAll() {
        return productDAO.findAll();
    }


    public Product findById(Long id) {
        if (id == null || id <= 0) {
            throw new org.example.exception.IllegalArgumentException("Invalid ID " + id);
        }
        return productDAO.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));
    }

    public Product updateStock(Long productId, int stockQuantity) {
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }

        Product product = productDAO.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found"));

        product.setQuantityAvailable(stockQuantity);
        return productDAO.saveOrUpdate(product);
    }



    public void delete(Long id) {

        findById(id);
        productDAO.delete(id);
    }


}
