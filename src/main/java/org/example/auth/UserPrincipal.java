package org.example.auth;


import lombok.*;

import java.security.Principal;
import java.util.Set;

@Data
@Builder

public class UserPrincipal implements Principal {
    private final Long userId;
    private final String name;
    private final Set<String> roles;

    public UserPrincipal(Long userId, String name, Set<String> roles) {
        this.userId = userId;
        this.name = name;
        this.roles = roles;
    }



    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    @Override
    public String getName() {
        return name;
    }
}