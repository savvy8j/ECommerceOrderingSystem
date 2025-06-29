package org.example.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.dropwizard.auth.Authenticator;


import java.util.Optional;
import java.util.Set;

public class JwtAuthenticator implements Authenticator<String, UserPrincipal> {

    @Override
    public Optional<UserPrincipal> authenticate(final String token) {
        try {
            DecodedJWT decodedJWT = JwtUtil.validateJWTToken(token);
            String username = decodedJWT.getSubject();
            Long userId = decodedJWT.getClaim("userId").asLong();
            String role = decodedJWT.getClaim("role").asString();

            return Optional.of(new UserPrincipal(userId,username, Set.of(role)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
