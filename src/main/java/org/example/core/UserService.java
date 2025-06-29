package org.example.core;

import org.example.api.RegisterDTO;
import org.example.auth.JwtUtil;
import org.example.auth.PasswordUtil;
import org.example.db.Role;
import org.example.db.User;
import org.example.db.UserDAO;
import org.example.exception.CredentialsInvalidException;

import java.time.LocalDate;
import java.util.Optional;

public class UserService {
    private final UserDAO userDAO;

    public UserService(final UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User registerCustomer(RegisterDTO registerDTO) {
        Role role = new Role();
        role.setRoleName(RoleName.ROLE_CUSTOMER);
        User user = new User();
        user.setUsername(registerDTO.getUserName());
        user.setEmail(registerDTO.getEmail());
        user.setPasswordHash(PasswordUtil.hashPassword(registerDTO.getPassword()));
        user.setCreatedDate(LocalDate.now());
        user.setRole(role);
        user = userDAO.saveOrUpdate(user);
        return user;
    }

    public User registerAdmin(RegisterDTO registerDTO) {
        Role role = new Role();
        role.setRoleName(RoleName.ROLE_ADMIN);
        User user = new User();
        user.setUsername(registerDTO.getUserName());
        user.setEmail(registerDTO.getEmail());
        user.setPasswordHash(PasswordUtil.hashPassword(registerDTO.getPassword()));
        user.setCreatedDate(LocalDate.now());
        user.setRole(role);
        user = userDAO.saveOrUpdate(user);
        return user;
    }

    public String userLogin(String email, String password) {
        Optional<User> userByEmail = userDAO.getUserByEmail(email);
        if(userByEmail.isEmpty()) {
            throw new CredentialsInvalidException("Invalid email or password");
        }
        User user = userByEmail.get();
        if(!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            throw new CredentialsInvalidException("Invalid password");
        }
        return JwtUtil.generateJWTToken(user);
    }

    public Optional<User> findById(Long id) {
        return userDAO.getUserById(id);
    }


}
