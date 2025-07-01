package org.example.resources;

import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.ECommerceApplication;
import org.example.ECommerceConfiguration;
import org.example.api.LoginDTO;
import org.example.api.LoginResponse;
import org.example.api.OrderItemDTO;
import org.example.api.PlaceOrderRequestDTO;
import org.example.core.Status;
import org.example.db.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class OrderResourceTest {
    private static DropwizardAppExtension<ECommerceConfiguration> EXT = new DropwizardAppExtension<>(
            ECommerceApplication.class, "config.yml");


    @Test
    public void testPlaceOrder(){
        Client client =EXT.client();
        LoginResponse response = loginAsCustomer(client);
        assertNotNull(response);
        PlaceOrderRequestDTO req = new PlaceOrderRequestDTO();
        req.setOrderItems(List.of(OrderItemDTO.builder()
                .productId(2L)
                .quantityOrdered(10)
                .build()));

        Response postResponse = client.target(String.format("http://localhost:%d/api/orders", EXT.getLocalPort()))
                .request()
                .header("Accept", MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + response.getToken())
                .post(Entity.entity(req, MediaType.APPLICATION_JSON));

        Assertions.assertEquals(201, postResponse.getStatus());

        Order order = postResponse.readEntity(Order.class);
        assertNotNull(order);
        assertNotNull(order.getOrderItems());
        Assertions.assertEquals(1, order.getOrderItems().size());
        Assertions.assertEquals(Status.PLACED, order.getStatus());

//        Response response1 = client.target(String.format("http://localhost:%d/api/order/9", EXT.getLocalPort()))
//                .request()
//                .header("Accept", MediaType.APPLICATION_JSON)
//                .header("Authorization", "Bearer " + response.getToken())
//                .get();
//
//        Assertions.assertEquals(200, response1.getStatus());
//
//        Order order1 = response1.readEntity(Order.class);
//        assertNotNull(order1);
//        Assertions.assertEquals(Status.PLACED, order1.getStatus());

    }




    private static LoginResponse loginAsCustomer(Client client) {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("zw00@gmail.com");
        loginDTO.setPassword("zwy00");

        LoginResponse loginResponse = client.target(String.format("http://localhost:%d/api/users/login", EXT.getLocalPort()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(loginDTO, MediaType.APPLICATION_JSON_TYPE), LoginResponse.class);
        return loginResponse;
    }

    private static LoginResponse loginAsAdmin(Client client) {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("admin");
        loginDTO.setPassword("admin");

        LoginResponse loginResponse = client.target(String.format("http://localhost:%d/api/users/login", EXT.getLocalPort()))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(loginDTO, MediaType.APPLICATION_JSON_TYPE), LoginResponse.class);
        return loginResponse;
    }


}