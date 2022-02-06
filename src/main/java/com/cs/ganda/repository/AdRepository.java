package com.cs.ganda.repository;

import com.cs.ganda.document.Ad;
import com.cs.ganda.enums.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface AdRepository extends MongoRepository<Ad, String> {
    Stream<Ad> findAllByStatusInOrderByCreation(Set<Status> status);

    Stream<Ad> findAllByActive(Boolean status);

    Stream<Ad> findByValidityDateAfter(Instant date);

    Optional<Ad> findByProfilePhoneAndProfilePhoneIndex(String phone, String phoneIndex);
}
