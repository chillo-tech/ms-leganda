package com.cs.ganda.repository;

import com.cs.ganda.document.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProfileRepository extends MongoRepository<Profile, String> {
    Optional<Profile> findByEmail(String email);

    Optional<Profile> findByEmailOrPhone(String email, String phone);

    Optional<Profile> findByFullPhone(String phone);

    Optional<Profile> findByPhone(String phone);

    Optional<Profile> findByPhoneAndPhoneIndex(String phone, String phoneIndex);


}
