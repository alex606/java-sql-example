package com.example.demo.controller;

import com.example.demo.models.Person;
import com.example.demo.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableJpaRepositories("com.example.demo.repositories")
@EntityScan("com.example.demo.models")
public class PersonController {

    @Autowired
    PersonRepository personRepository;

    @GetMapping(value = "/person")
    public void addPerson() {
        Person p = new Person("nameOfPerson");
        personRepository.save(p);
    }
}
