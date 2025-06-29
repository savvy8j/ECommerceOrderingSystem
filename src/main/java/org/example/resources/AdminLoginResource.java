//package org.example.resources;
//
//import jakarta.ws.rs.Consumes;
//import jakarta.ws.rs.POST;
//import jakarta.ws.rs.Path;
//import jakarta.ws.rs.Produces;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//import org.example.api.LoginDTO;
//import org.example.api.LoginResponse;
//import org.example.auth.JwtUtil;
//
//@Path("/admin/login")
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
//public class AdminLoginResource {
//
//    @POST
//    public Response login(LoginDTO loginDto) {
//        if ("admin".equals(loginDto.getUsername()) && "admin".equals(loginDto.getPassword())) {
//            String token = JwtUtil.generateJWTToken(loginDto.getUsername(), "ADMIN");
//
//            LoginResponse loginResponse = LoginResponse.builder()
//                    .token(token)
//                    .build();
//
//            return Response.ok(loginResponse).build();
//        }
//
//        return Response.status(Response.Status.UNAUTHORIZED).build();
//    }
//}
