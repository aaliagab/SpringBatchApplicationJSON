package com.batch.services;

import com.batch.entities.Person;
import com.batch.repositories.IPersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService implements IPersonService{
    @Autowired
    private IPersonRepository personRepository;
    @Override
    public Iterable<Person> saveAll(List<Person> personList) {
        return personRepository.saveAll(personList);
    }
}
