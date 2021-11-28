package com.cs.ganda.repository;

import com.cs.ganda.document.Meal;
import com.cs.ganda.document.Validity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ValidityRepository extends MongoRepository<Validity, String> {
}
