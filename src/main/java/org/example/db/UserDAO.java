package org.example.db;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class UserDAO extends AbstractDAO<User> {
    public UserDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public User saveOrUpdate(User user) {
        return persist(user);
    }


    public Optional<User> getUserByUsername(String username) {
        return currentSession().createQuery("from User u where u.username  =: username", User.class).setParameter("username", username).uniqueResultOptional();
    }

    public Optional<User> getUserById(Long userId) {
        return Optional.ofNullable(get(userId));
    }

    public Optional<User> getUserByEmail(String email) {
        return currentSession().createQuery("from User u where u.email  =: email", User.class).setParameter("email", email).uniqueResultOptional();
    }
}
