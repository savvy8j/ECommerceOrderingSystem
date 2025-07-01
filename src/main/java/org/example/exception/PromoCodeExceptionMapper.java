package org.example.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class PromoCodeExceptionMapper implements ExceptionMapper<PromoCodeException> {

    @Override
    public Response toResponse(PromoCodeException e) {
        return Response.status(Response.Status.NOT_FOUND).
                entity(new ErrorResponse("Promo Code Not Found"))
                .build();
    }
}
