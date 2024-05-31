package com.teletronics.storage.controller;

import com.teletronics.storage.model.FileDocument;
import com.teletronics.storage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("visibility") String visibility,
                                        @RequestParam("tags") List<String> tags,
                                        @RequestHeader("userId") String userId) {
        if (tags.size() > 5) {
            return new ResponseEntity<>("Cannot upload more than 5 tags per file", HttpStatus.BAD_REQUEST);
        }

        try {
            FileDocument fileDocument = fileService.storeFile(file, visibility, tags, userId);
            return new ResponseEntity<>(fileDocument, HttpStatus.OK);
        } catch (IOException | NoSuchAlgorithmException e) {
            return new ResponseEntity<>("Error uploading file", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        Optional<FileDocument> fileOptional = fileService.getFileById(id);
        if (fileOptional.isPresent()) {
            FileDocument fileDocument = fileOptional.get();
            ResponseEntity<byte[]> response = ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDocument.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType(fileDocument.getContentType()))
                    .body(fileDocument.getContent());

            System.out.println(response.getHeaders());  // Debug line

            return response;
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/public")
    public ResponseEntity<Page<FileDocument>> listPublicFiles(@RequestParam int page,
                                                              @RequestParam int size,
                                                              @RequestParam String sortBy,
                                                              @RequestParam String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return new ResponseEntity<>(fileService.listPublicFiles(pageable), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<FileDocument>> listUserFiles(@PathVariable String userId,
                                                            @RequestParam int page,
                                                            @RequestParam int size,
                                                            @RequestParam String sortBy,
                                                            @RequestParam String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return new ResponseEntity<>(fileService.listUserFiles(userId, pageable), HttpStatus.OK);
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<Page<FileDocument>> listFilesByTag(@PathVariable String tag,
                                                             @RequestParam int page,
                                                             @RequestParam int size,
                                                             @RequestParam String sortBy,
                                                             @RequestParam String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return new ResponseEntity<>(fileService.listFilesByTag(tag, pageable), HttpStatus.OK);
    }

    @PutMapping("/{id}/filename")
    public ResponseEntity<?> updateFilename(@PathVariable String id, @RequestParam("newFilename") String newFilename) {
        try {
            FileDocument updatedFile = fileService.updateFilename(id, newFilename);
            return new ResponseEntity<>(updatedFile, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable String id,
                                        @RequestHeader("userId") String userId) {
        fileService.deleteFile(id, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
