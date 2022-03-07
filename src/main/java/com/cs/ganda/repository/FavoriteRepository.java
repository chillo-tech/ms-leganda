package com.cs.ganda.repository;

import com.cs.ganda.document.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.stream.Stream;

public interface FavoriteRepository extends MongoRepository<Favorite, String> {
    Favorite findByProviderIdAndProfileId(String addId, String profileId);

    void deleteByProviderIdAndProfileId(String addId, String profileId);

    Stream<Favorite> findByProfileId(String profileId);
}
