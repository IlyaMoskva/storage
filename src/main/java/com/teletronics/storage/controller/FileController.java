package com.teletronics.storage.controller;

import com.teletronics.storage.model.FileDocument;
import com.teletronics.storage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileDocument> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        FileDocument fileDocument = fileService.storeFile(file);
        return new ResponseEntity<>(fileDocument, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        return fileService.getFile(id)
                .map(fileDocument -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(org.springframework.http.MediaType.parseMediaType(fileDocument.getContentType()));
                    headers.setContentDispositionFormData("attachment", fileDocument.getFilename());
                    return new ResponseEntity<>(fileDocument.getContent(), headers, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
