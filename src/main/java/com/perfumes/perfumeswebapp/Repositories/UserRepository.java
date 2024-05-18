package com.perfumes.perfumeswebapp.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.perfumes.perfumeswebapp.model.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    Boolean existsByNameAndSurname(String name, String surname);
}
