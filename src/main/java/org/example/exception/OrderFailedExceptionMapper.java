package org.example.exception;


import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class OrderFailedExceptionMapper implements ExceptionMapper<OrderFailedException> {
    @Override
    public Response toResponse(final OrderFailedException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(e.getMessage()))
                .build();


    }
}