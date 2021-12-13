package com.cs.ganda.repository;


import com.cs.ganda.document.ConfirmationToken;
import com.cs.ganda.enums.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ConfirmationTokenRepository extends MongoRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByToken(String token);

    Optional<ConfirmationToken> findTopByProfilePhoneAndProfilePhoneIndexOrderByCreationDesc(String phone, String phoneIndex);

    Optional<ConfirmationToken> findTopByItemIdAndPhoneAndPhoneIndexAndStatus(String itemId, String phone, String phoneIndex, Status status);
}
