package org.example.resources;

import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.ECommerceApplication;
import org.example.ECommerceConfiguration;
import org.example.api.LoginDTO;
import org.example.api.LoginResponse;
import org.example.api.StockUpdateDTO;
import org.example.db.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class ProductResourceTest {
    private static DropwizardAppExtension<ECommerceConfiguration> EXT = new DropwizardAppExtension<>(
            ECommerceApplication.class, "config.yml"
    );


    @Test
    public void getAllProducts() {
        Client client = EXT.client();
        Response response = client.target(String.format("http://localhost:%d/api/products", EXT.getLocalPort()))
                .request()
                .header("Accept", "application/json")
                .get();
        assertEquals(200, response.getStatus());
        List<Product> products = response.readEntity(new GenericType<List<Product>>() {
        });
        Assertions.assertEquals(4, products.size());
    }

    @Test
    public void getProduct() {
        Client client = EXT.client();
        Response response = client.target(String.format("http://localhost:%d/api/products/1", EXT.getLocalPort()))
                .request()
                .header("Accept", "application/json")
                .get();
        assertEquals(200, response.getStatus());
        Product product = response.readEntity(Product.class);
        Assertions.assertEquals(1, product.getProductId());
    }

//    @Test
//    public void getProductByIdWhenIdNotFound() {
//        Client client = EXT.client();
////        LoginResponse loginResponse = getLoginResponse(client);
//        Response response = client.target(String.format("http://localhost:%d/api/employees/99", EXT.getLocalPort()))
//                .request()
//                .header("Accept", "application/json")

    /// /                .header("Authorization", "Bearer " + loginResponse.getToken())
//                .get();
//        Assertions.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
//    }
    @Test
    public void testGetProductByIdNotFound() {
        Client client = EXT.client();

        Response response = client.target(String.format("http://localhost:%d/api/products/9999", EXT.getLocalPort()))
                .request()
                .header("Accept", "application/json")
                .get();

        Assertions.assertEquals(404, response.getStatus());
    }


    @Test
    public void testCreateProductByAdmin() {
        Client client = EXT.client();
        LoginResponse loginResponse = getAdminLogin(client);

        Product newProduct = Product.builder()
                .name("Laptop")
                .description("High-end gaming laptop")
                .price(1500.0)
                .quantityAvailable(10)
                .build();

        Response response = client.target(String.format("http://localhost:%d/api/products/admin", EXT.getLocalPort()))
                .request()
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + loginResponse.getToken())
                .post(Entity.entity(newProduct, MediaType.APPLICATION_JSON_TYPE));

        Assertions.assertEquals(201, response.getStatus());

        Product createdProduct = response.readEntity(Product.class);
        Assertions.assertNotNull(createdProduct.getProductId());
        Assertions.assertEquals("Laptop", createdProduct.getName());
    }

    @Test
    public void testUpdateProductByAdmin() {
        Client client = EXT.client();
        LoginResponse loginResponse = getAdminLogin(client);

        Product updatedProduct = Product.builder()
                .name("Updated Laptop")
                .description("Updated memory")
                .price(1800.0)
                .quantityAvailable(5)
                .build();

        Response response = client.target(String.format("http://localhost:%d/api/products/admin/1", EXT.getLocalPort()))
                .request()
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + loginResponse.getToken())
                .put(Entity.entity(updatedProduct, MediaType.APPLICATION_JSON_TYPE));

        Assertions.assertEquals(200, response.getStatus());

        Product product = response.readEntity(Product.class);
        Assertions.assertEquals("Updated Laptop", product.getName());
    }


    @Test
    public void testDeleteProduct() {
        Client client = EXT.client();
        LoginResponse loginResponse = getAdminLogin(client);

        Response deleteResponse = client.target(String.format("http://localhost:%d/api/products/admin/1", EXT.getLocalPort()))
                .request()
                .header("Authorization", "Bearer " + loginResponse.getToken())
                .delete();

        Assertions.assertEquals(204, deleteResponse.getStatus());

        Response getResponse = client.target(String.format("http://localhost:%d/api/products/admin/1", EXT.getLocalPort()))
                .request()
                .header("Accept", "application/json")
                .get();
        Assertions.assertEquals(404, getResponse.getStatus());
    }


    private static LoginResponse getAdminLogin(Client client) {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("admin@gmail.com");
        loginDTO.setPassword("admin");

        return client.target(String.format("http://localhost:%d/api/users/login", EXT.getLocalPort()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(loginDTO, MediaType.APPLICATION_JSON_TYPE), LoginResponse.class);
    }
}



