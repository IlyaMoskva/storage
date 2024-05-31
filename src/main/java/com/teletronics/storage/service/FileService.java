package com.teletronics.storage.service;

import org.apache.tika.Tika;
import com.teletronics.storage.model.FileDocument;
import com.teletronics.storage.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    private final Tika tika = new Tika();

    public FileDocument storeFile(MultipartFile file, String visibility, List<String> tags, String userId) throws IOException, NoSuchAlgorithmException {
        String hash = calculateHash(file.getBytes());

        if (fileRepository.existsByFilename(file.getOriginalFilename()) || fileRepository.existsByHash(hash)) {
            throw new IllegalArgumentException("File already exists");
        }

        FileDocument fileDocument = new FileDocument();
        fileDocument.setFilename(file.getOriginalFilename());
        fileDocument.setContentType(file.getContentType() != null ? file.getContentType() : tika.detect(new ByteArrayInputStream(file.getBytes())));
        fileDocument.setContent(file.getBytes());
        fileDocument.setFileSize(file.getSize());
        fileDocument.setUploadDate(new Date());
        fileDocument.setVisibility(visibility);
        fileDocument.setTags(tags);
        fileDocument.setUserId(userId);
        fileDocument.setHash(hash);

        return fileRepository.save(fileDocument);
    }

    public Optional<FileDocument> getFile(String id) {
        return fileRepository.findById(id);
    }

    public Page<FileDocument> listPublicFiles(Pageable pageable) {
        return fileRepository.findByVisibility("PUBLIC", pageable);
    }

    public Page<FileDocument> listUserFiles(String userId, Pageable pageable) {
        return fileRepository.findByUserId(userId, pageable);
    }

    public Page<FileDocument> listFilesByTag(String tag, Pageable pageable) {
        return fileRepository.findByTagsContaining(tag, pageable);
    }

    public FileDocument updateFilename(String id, String newFilename) {
        Optional<FileDocument> fileOptional = fileRepository.findById(id);
        if (fileOptional.isPresent()) {
            FileDocument fileDocument = fileOptional.get();
            fileDocument.setFilename(newFilename);
            return fileRepository.save(fileDocument);
        } else {
            throw new IllegalArgumentException("File not found");
        }
    }

    public void deleteFile(String id) {
        fileRepository.deleteById(id);
    }

    private String calculateHash(byte[] content) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(content);
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
