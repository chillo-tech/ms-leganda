package com.cs.ganda.repository;


import com.cs.ganda.document.ConfirmationToken;
import com.cs.ganda.enums.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ConfirmationTokenRepository extends MongoRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByTokenAndStatus(String token, Status status);
}
