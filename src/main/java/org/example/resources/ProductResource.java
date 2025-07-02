package org.example.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.example.api.StockUpdateDTO;
import org.example.auth.UserPrincipal;
import org.example.core.ProductService;
import org.example.core.RoleName;
import org.example.db.Product;
import org.example.db.User;

import java.util.List;

@Slf4j
@Path("/api/products")
@Tag(name ="ProductResource")
public class ProductResource {

    private final ProductService productService;

    public ProductResource(ProductService productService) {
        this.productService = productService;
    }


    @UnitOfWork
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @UnitOfWork
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Product getProductById(@PathParam("id") Long id) {
        return productService.findById(id);
    }

    @UnitOfWork
    @Path("/admin")
    @RolesAllowed("ROLE_ADMIN")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProduct(Product product) {
        return Response.status(Response.Status.CREATED)
                .entity(productService.saveOrUpdate(product))
                .build();
    }


    @UnitOfWork
    @RolesAllowed("ROLE_ADMIN")
    @Path("/admin/{id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Product updateProduct(Product product, @PathParam("id") Long id) {
        product.setProductId(id);
        return productService.saveOrUpdate(product);
    }

    @UnitOfWork
    @RolesAllowed("ROLE_ADMIN")
    @PATCH
    @Path("/admin/{id}/stock")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Product updateProductStock(@PathParam("id") Long id, StockUpdateDTO request) {
        return productService.updateStock(id, request.getQuantityAvailable());
    }


    @UnitOfWork
    @RolesAllowed("ROLE_ADMIN")
    @Path("/admin/{id}")
    @DELETE
    public void deleteProduct(@PathParam("id") Long id) {
        productService.delete(id);
    }


}
