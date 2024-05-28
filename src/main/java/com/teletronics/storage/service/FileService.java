package com.teletronics.storage.service;

import com.teletronics.storage.model.FileDocument;
import com.teletronics.storage.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    public FileDocument storeFile(MultipartFile file) throws IOException {
        FileDocument fileDocument = new FileDocument();
        fileDocument.setFilename(file.getOriginalFilename());
        fileDocument.setContentType(file.getContentType());
        fileDocument.setContent(file.getBytes());
        return fileRepository.save(fileDocument);
    }

    public Optional<FileDocument> getFile(String id) {
        return fileRepository.findById(id);
    }
}
