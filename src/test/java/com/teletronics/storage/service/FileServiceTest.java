package com.teletronics.storage.service;

import com.teletronics.storage.model.FileDocument;
import com.teletronics.storage.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void storeFile_shouldStoreFileSuccessfully() throws IOException, NoSuchAlgorithmException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");
        when(multipartFile.getBytes()).thenReturn("Hello World".getBytes());
        when(multipartFile.getContentType()).thenReturn("text/plain");
        when(multipartFile.getSize()).thenReturn(11L);

        when(fileRepository.existsByFilename("test.txt")).thenReturn(false);
        when(fileRepository.existsByHash(anyString())).thenReturn(false);

        FileDocument savedFile = new FileDocument();
        savedFile.setId("1");
        when(fileRepository.save(any(FileDocument.class))).thenReturn(savedFile);

        FileDocument result = fileService.storeFile(multipartFile, "PUBLIC", Arrays.asList("tag1", "tag2"), "user1");

        assertNotNull(result);
        assertEquals("1", result.getId());

        ArgumentCaptor<FileDocument> fileDocumentArgumentCaptor = ArgumentCaptor.forClass(FileDocument.class);
        verify(fileRepository).save(fileDocumentArgumentCaptor.capture());

        FileDocument capturedFile = fileDocumentArgumentCaptor.getValue();
        assertEquals("test.txt", capturedFile.getFilename());
        assertEquals("text/plain", capturedFile.getContentType());
        assertEquals("user1", capturedFile.getUserId());
    }

    @Test
    void storeFile_shouldThrowExceptionIfFileExists() throws IOException, NoSuchAlgorithmException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");
        when(multipartFile.getBytes()).thenReturn("Hello World".getBytes());

        when(fileRepository.existsByFilename("test.txt")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            fileService.storeFile(multipartFile, "PUBLIC", Arrays.asList("tag1", "tag2"), "user1");
        });
    }

    @Test
    void getFile_shouldReturnFileById() {
        FileDocument fileDocument = new FileDocument();
        fileDocument.setId("1");
        fileDocument.setFilename("test.txt");
        fileDocument.setContentType("text/plain");
        fileDocument.setContent("Hello World".getBytes());
        fileDocument.setUserId("user1");

        when(fileRepository.findById("1")).thenReturn(Optional.of(fileDocument));

        Optional<FileDocument> result = fileService.getFileById("1");

        assertTrue(result.isPresent());
        assertEquals("test.txt", result.get().getFilename());
    }

    @Test
    void getFile_shouldReturnEmptyIfFileByIdNotFound() {
        when(fileRepository.findById("1")).thenReturn(Optional.empty());

        Optional<FileDocument> result = fileService.getFileById("1");

        assertFalse(result.isPresent());
    }
}
