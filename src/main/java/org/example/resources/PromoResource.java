package org.example.resources;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.example.api.PromoCodeDTO;
import org.example.core.PromoService;
import org.example.db.PromoCode;

@Path("/api/promocodes")
@Slf4j
public class PromoResource {
    private final PromoService promoService;

    public PromoResource(PromoService promoService) {
        this.promoService = promoService;
    }



    @UnitOfWork
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPromoCode(PromoCodeDTO promoCodeDTO) {
        return Response.status(Response.Status.CREATED)
                .entity(promoService.createPromocode(promoCodeDTO))
                .build();

    }
}
