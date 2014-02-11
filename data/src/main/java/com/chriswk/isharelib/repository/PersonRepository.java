package com.chriswk.isharelib.repository;

import com.chriswk.isharelib.data.Person;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

public interface PersonRepository extends
        GraphRepository<Person>, RelationshipOperationsRepository<Person> {
    Person findByTmdbId(Long id);
}
