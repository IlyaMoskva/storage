package com.teletronics.storage.repository;

import com.teletronics.storage.model.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TagRepository extends MongoRepository<Tag, String> {
    Optional<Tag> findByNameIgnoreCase(String name);
}
