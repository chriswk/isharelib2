package com.chriswk.isharelib.repository;

import com.chriswk.isharelib.data.User;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.neo4j.repository.RelationshipOperationsRepository;

public interface UserRepository extends GraphRepository<User>,
        RelationshipOperationsRepository<User>,
        IsharelibUserDetailsService {
    User findByLogin(String login);
}
