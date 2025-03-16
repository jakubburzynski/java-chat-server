package org.example;

import jakarta.persistence.EntityManager;
import java.util.UUID;

public class UserRepository extends Repository {
    public User findById(UUID id) {
        EntityManager em = FACTORY.createEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public User create(String name) {
        EntityManager em = FACTORY.createEntityManager();
        User user = new User(name);
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return user;
        } finally {
            em.close();
        }
    }
}
