package org.example.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.db.Role;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long userId;

    private String username;

    private String email;

    private String passwordHash;

    private LocalDate createdDate;

    private Role role;
}
