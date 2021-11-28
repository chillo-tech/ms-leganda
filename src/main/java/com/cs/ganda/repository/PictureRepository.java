package com.cs.ganda.repository;

import com.cs.ganda.document.Picture;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PictureRepository extends MongoRepository<Picture, String> {
}
