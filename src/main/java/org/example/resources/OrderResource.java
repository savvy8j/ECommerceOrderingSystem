package org.example.resources;


import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.example.api.PlaceOrderRequestDTO;
import org.example.auth.UserPrincipal;
import org.example.core.OrderService;
import org.example.db.Order;

import java.util.List;

@Path("/api/orders")
@Slf4j
@Tag(name ="OrderResource")
public class OrderResource {
    private final OrderService orderService;

    public OrderResource(OrderService orderService) {
        this.orderService = orderService;
    }

    @UnitOfWork
    @POST
    @RolesAllowed("ROLE_CUSTOMER")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response placeOrder(PlaceOrderRequestDTO placeOrderRequestDTO, @Auth UserPrincipal user) {
        placeOrderRequestDTO.setUserId(user.getUserId());
        Order order = orderService.placeOrder(placeOrderRequestDTO);
        return Response.status(Response.Status.CREATED)
                .entity(order)
                .build();
    }


    @UnitOfWork
    @Path("/admin")
    @RolesAllowed("ROLE_ADMIN")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return Response.ok(orders)
                .build();
    }

    @UnitOfWork
    @RolesAllowed("ROLE_CUSTOMER")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdersByUserId(@Auth UserPrincipal user) {
        List<Order> orders = orderService.getOrdersByUserId(user.getUserId());
        if (orders.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK)
                .entity(orders)
                .build();
    }

    @UnitOfWork
    @Path("/{orderId}")
    @DELETE
    @RolesAllowed("ROLE_CUSTOMER")
    public Response cancelOwnOrder(@PathParam("orderId") Long orderId, @Auth UserPrincipal user) {
        orderService.deleteUserOrderById(orderId, user.getUserId());
        return Response.status(Response.Status.NO_CONTENT)
                .build();
    }

    @UnitOfWork
    @Path("/admin/{orderId}")
    @DELETE
    @RolesAllowed("ROLE_ADMIN")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelAnyOrder(
            @PathParam("orderId") Long orderId,
            @Auth UserPrincipal user
    ) {
        orderService.deleteOrderById(orderId, user.getUserId(), true);
        return Response.status(Response.Status.NO_CONTENT)
                .build();
    }

}
