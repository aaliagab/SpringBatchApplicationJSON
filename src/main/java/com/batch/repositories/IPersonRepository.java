package com.batch.repositories;

import com.batch.entities.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


public interface IPersonRepository extends CrudRepository<Person,Long> {
}
