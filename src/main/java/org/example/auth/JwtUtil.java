package org.example.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.example.db.User;

import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "SECRET";
    private static final String issuer = "ecs-app";
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET);

    public static final String generateJWTToken(User user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuer(issuer)
                .withClaim("userId",user.getUserId())
                .withClaim("role", user.getRole().getRoleName().name())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .sign(algorithm);
    }

    public static DecodedJWT validateJWTToken(String token) {
        JWTVerifier jwtVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();

        return jwtVerifier.verify(token);
    }

    public static String getUsernameFromJWT(String token) {
        return validateJWTToken(token).getSubject();
    }

    public static Long getUserIdFromJWT(String token) {
        return validateJWTToken(token).getClaim("userId").asLong();
    }


    public static String getRoleFromJWT(String token) {
        return validateJWTToken(token).getClaim("role").asString();
    }



}
