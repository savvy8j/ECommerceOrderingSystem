package org.example.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable throwable) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse("Internal Error Occured"))
                .build();
    }

}
