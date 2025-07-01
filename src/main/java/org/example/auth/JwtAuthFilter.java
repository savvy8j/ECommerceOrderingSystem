package org.example.auth;


import io.dropwizard.auth.AuthFilter;
import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.security.Principal;

@Priority(1)//first should go in authfilter
public class JwtAuthFilter<P extends Principal> extends AuthFilter<String, P> {

    @Override
    public void filter(final ContainerRequestContext containerRequestContext) throws IOException {
        //expected format :- Authorization: Bearer <JWT_TOKEN>
        String headerString = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);


        //Rejected Request with 401:Unauthorized
        if (headerString == null || !headerString.startsWith("Bearer ")) {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        String token = headerString.substring(7); // Bearer token
        boolean authenticate = authenticate(containerRequestContext, token, "");
        if (!authenticate) {
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    public static class Builder<P extends Principal> extends AuthFilterBuilder<String, P, JwtAuthFilter<P>> {
        @Override
        protected JwtAuthFilter<P> newInstance() {
            return new JwtAuthFilter<>();
        }
    }
}
