package org.example;

import jakarta.persistence.*;

public class Repository {
    protected static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("MyUnit");
}
