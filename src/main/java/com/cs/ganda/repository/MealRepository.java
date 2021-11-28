package com.cs.ganda.repository;

import com.cs.ganda.document.Meal;
import com.cs.ganda.enums.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public interface MealRepository extends MongoRepository<Meal, String> {
    Stream<Meal> findAllByStatusInOrderByCreation(Set<Status> status);

    Optional<Meal> findByProfilePhoneAndProfilePhoneIndex(String phone, String phoneIndex);
}
