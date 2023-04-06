package com.example.ServerSocket.models;

import jakarta.persistence.*;

@Entity
@Table(name = "PERSON_TABLE")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;
    @Column(name = "name")
    String name;

    public Person(String name) {
        this.name = name;
    }

}
