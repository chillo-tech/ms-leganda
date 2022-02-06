package com.cs.ganda.repository;

import com.cs.ganda.document.AuthenticationData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AuthenticationDataRepository extends MongoRepository<AuthenticationData, String> {
    Optional<AuthenticationData> findByAccessToken(String token);
}
