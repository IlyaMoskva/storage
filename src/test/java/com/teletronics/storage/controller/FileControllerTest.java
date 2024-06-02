package com.teletronics.storage.controller;

import com.teletronics.storage.model.FileDocument;
import com.teletronics.storage.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @Test
    void uploadFile_shouldUploadFileSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        FileDocument fileDocument = new FileDocument();
        fileDocument.setId("1");
        fileDocument.setFilename("test.txt");

        when(fileService.storeFile(any(MultipartFile.class), anyString(), anyList(), anyString())).thenReturn(fileDocument);

        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .param("visibility", "PUBLIC")
                        .param("tags", "tag1", "tag2")
                        .header("userId", "user1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.filename", is("test.txt")));

        verify(fileService, times(1)).storeFile(any(MultipartFile.class), anyString(), anyList(), anyString());
    }

    @Test
    void uploadFile_shouldReturnBadRequestWhenUserIdIsMissing() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Test content".getBytes());

        // Act & Assert
        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .param("visibility", "PUBLIC")
                        .param("tags", "tag1,tag2")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

        verify(fileService, times(0)).storeFile(
                any(MultipartFile.class), anyString(), any(List.class), anyString());
    }

    @Test
    void uploadFile_shouldReturnErrorWhenTagsExceedLimit() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());

        mockMvc.perform(multipart("/api/files/upload")
                        .file(file)
                        .param("visibility", "PUBLIC")
                        .param("tags", "tag1", "tag2", "tag3", "tag4", "tag5", "tag6")
                        .header("userId", "user1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot upload more than 5 tags per file"));

        verify(fileService, times(0)).storeFile(any(MultipartFile.class), anyString(), anyList(), anyString());
    }

    @Test
    void getFile_shouldReturnFileSuccessfully() throws Exception {
        FileDocument fileDocument = new FileDocument();
        fileDocument.setId("1");
        fileDocument.setFilename("test.txt");
        fileDocument.setContentType("text/plain");
        fileDocument.setContent("Hello World".getBytes());
        fileDocument.setUserId("user1");

        when(fileService.getFileById("1")).thenReturn(Optional.of(fileDocument));

        mockMvc.perform(get("/api/files/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.txt\""))
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().bytes("Hello World".getBytes()));

        verify(fileService, times(1)).getFileById("1");
    }

    @Test
    void getFile_shouldReturnNotFoundWhenFileDoesNotExist() throws Exception {
        when(fileService.getFileById("1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/files/1"))
                .andExpect(status().isNotFound());

        verify(fileService, times(1)).getFileById("1");
    }

    @Test
    void listPublicFiles_shouldReturnPublicFiles() throws Exception {
        FileDocument fileDocument = new FileDocument();
        fileDocument.setId("1");
        fileDocument.setFilename("test.txt");
        Page<FileDocument> page = new PageImpl<>(Collections.singletonList(fileDocument));

        when(fileService.listPublicFiles(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/files/public")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "filename")
                        .param("sortOrder", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is("1")));

        verify(fileService, times(1)).listPublicFiles(any(PageRequest.class));
    }

    @Test
    void listUserFiles_shouldReturnUserFiles() throws Exception {
        FileDocument fileDocument = new FileDocument();
        fileDocument.setId("1");
        fileDocument.setFilename("test.txt");
        Page<FileDocument> page = new PageImpl<>(Collections.singletonList(fileDocument));

        when(fileService.listUserFiles(anyString(), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/files/user/user1")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "filename")
                        .param("sortOrder", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is("1")));

        verify(fileService, times(1)).listUserFiles(anyString(), any(PageRequest.class));
    }

    @Test
    void listFilesByTag_shouldReturnFilesByTag() throws Exception {
        FileDocument fileDocument = new FileDocument();
        fileDocument.setId("1");
        fileDocument.setFilename("test.txt");
        Page<FileDocument> page = new PageImpl<>(Collections.singletonList(fileDocument));

        when(fileService.listFilesByTag(anyString(), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/files/tag/tag1")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "filename")
                        .param("sortOrder", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is("1")));

        verify(fileService, times(1)).listFilesByTag(anyString(), any(PageRequest.class));
    }

    @Test
    void updateFilename_shouldUpdateFilenameSuccessfully() throws Exception {
        FileDocument fileDocument = new FileDocument();
        fileDocument.setId("1");
        fileDocument.setFilename("new_test.txt");

        when(fileService.updateFilename(anyString(), anyString())).thenReturn(fileDocument);

        mockMvc.perform(put("/api/files/1/filename")
                        .param("newFilename", "new_test.txt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.filename", is("new_test.txt")));

        verify(fileService, times(1)).updateFilename(anyString(), anyString());
    }

    @Test
    void deleteFile_shouldDeleteFileSuccessfully() throws Exception {
        doNothing().when(fileService).deleteFile(anyString(), anyString());

        mockMvc.perform(delete("/api/files/1")
                        .header("userId", "user1"))
                .andExpect(status().isNoContent());

        verify(fileService, times(1)).deleteFile(anyString(), anyString());
    }

    @Test
    void deleteFile_shouldReturnBadRequestWhenUserIdIsMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/files/1"))
                .andExpect(status().isBadRequest());

        verify(fileService, times(0)).deleteFile(anyString(), anyString());
    }
}
