package org.example;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.UUID;

public class MessageRepository extends Repository {
    public List<Message> findAll() {
        EntityManager em = FACTORY.createEntityManager();
        try {
            return em.createQuery("SELECT m FROM Message m JOIN FETCH m.sender ORDER BY m.createdAt ASC", Message.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Message create(String text, UUID senderId) {
        EntityManager em = FACTORY.createEntityManager();
        User sender = em.getReference(User.class, senderId);
        Message message = new Message(text, sender);
        try {
            em.getTransaction().begin();
            em.persist(message);
            em.getTransaction().commit();
            return message;
        } finally {
            em.close();
        }
    }
}
