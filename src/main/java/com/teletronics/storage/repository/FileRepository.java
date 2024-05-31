package com.teletronics.storage.repository;

import com.teletronics.storage.model.FileDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends MongoRepository<FileDocument, String> {

    List<FileDocument> findByVisibility(String visibility);

    List<FileDocument> findByUserId(String userId);

    List<FileDocument> findByTagsContaining(String tag);

    boolean existsByFilename(String filename);

    boolean existsByHash(String hash);

    Page<FileDocument> findByVisibility(String visibility, Pageable pageable);

    Page<FileDocument> findByUserId(String userId, Pageable pageable);

    Page<FileDocument> findByTagsContaining(String tag, Pageable pageable);
}
