package org.example.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class ProductNotFoundExceptionMappper implements ExceptionMapper<ProductNotFoundException> {

    @Override
    public Response toResponse(final ProductNotFoundException e)
    {
        return Response.status(Response.Status.NOT_FOUND).
                entity(new ErrorResponse(e.getMessage()))
                .build();

    }
}
