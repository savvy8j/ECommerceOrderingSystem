package org.example.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class CredentialsInvalidExceptionMapper implements ExceptionMapper<CredentialsInvalidException> {
    @Override
    public Response toResponse(final CredentialsInvalidException e) {
        return Response.status(Response.Status.UNAUTHORIZED).
                entity(new ErrorResponse("Invalid credentials"))
                .build();


    }
}

