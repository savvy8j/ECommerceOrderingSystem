package org.example.resources;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.example.api.LoginDTO;
import org.example.api.LoginResponse;
import org.example.api.RegisterDTO;
import org.example.core.RoleName;
import org.example.core.UserService;
import org.example.db.User;

@Path("/api/users")
@Slf4j

public class UserResource {
    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }



    @UnitOfWork
    @POST
    @Path("/register/customer")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerCustomer(RegisterDTO registerDTO) {
        User user = userService.registerCustomer(registerDTO);
        return Response.status(Response.Status.CREATED)
                .entity(user)
                .build();
    }

    @UnitOfWork
    @POST
    @Path("/register/admin")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerAdmin(RegisterDTO registerDTO) {
        User user = userService.registerAdmin(registerDTO);
        return Response.status(Response.Status.CREATED)
                .entity(user)
                .build();
    }


    @UnitOfWork
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginDTO loginDTO) {
        String jwtToken = userService.userLogin(loginDTO.getEmail(), loginDTO.getPassword());
        LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken)
                .build();

        return Response.ok(loginResponse)
                .build();

    }
}
