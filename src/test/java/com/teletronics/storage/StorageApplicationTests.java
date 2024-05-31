package com.teletronics.storage;

import com.teletronics.storage.model.FileDocument;
import com.teletronics.storage.service.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.containers.MongoDBContainer;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class StorageApplicationTests {

	private static MongoDBContainer mongoDBContainer;

	@Autowired
	private FileService fileService;

	@Autowired
	private MongoTemplate mongoTemplate;

	@BeforeAll
	public static void setUp() {
		mongoDBContainer = new MongoDBContainer("mongo:4.4.6");
		mongoDBContainer.start();

		System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());
	}

	@BeforeEach
	public void cleanDatabase() {
		mongoTemplate.getDb().drop();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void testStoreAndRetrieveFile() throws IOException, NoSuchAlgorithmException {
		// Create a mock MultipartFile
		MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, MongoDB!".getBytes());

		// Store the file using the service
		FileDocument savedDoc = fileService.storeFile(file, "PUBLIC", List.of("tag1", "tag2"), "user1");

		// Retrieve the document by its ID using the service
		Optional<FileDocument> retrievedDocOptional = fileService.getFileById(savedDoc.getId());

		// Verify the document was saved and retrieved successfully
		assertThat(retrievedDocOptional).isPresent();
		FileDocument retrievedDoc = retrievedDocOptional.get();
		assertThat(retrievedDoc.getFilename()).isEqualTo("test.txt");
		assertThat(retrievedDoc.getContentType()).isEqualTo("text/plain");
		assertThat(new String(retrievedDoc.getContent())).isEqualTo("Hello, MongoDB!");
		assertThat(retrievedDoc.getVisibility()).isEqualTo("PUBLIC");
		assertThat(retrievedDoc.getTags()).containsExactly("tag1", "tag2");
		assertThat(retrievedDoc.getUserId()).isEqualTo("user1");
	}

	@Test
	void testStoreDuplicateFileByName() throws IOException, NoSuchAlgorithmException {
		// Create a mock MultipartFile
		MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, MongoDB!".getBytes());

		// Store the file using the service
		fileService.storeFile(file, "PUBLIC", List.of("tag1", "tag2"), "user1");

		// Try to store the same file again and expect an exception
		assertThatThrownBy(() -> fileService.storeFile(file, "PUBLIC", List.of("tag1", "tag2"), "user1"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("File already exists");
	}

	@Test
	void testStoreDuplicateFileByContent() throws IOException, NoSuchAlgorithmException {
		// Create two mock MultipartFiles with the same content but different names
		MockMultipartFile file1 = new MockMultipartFile("file", "test1.txt", "text/plain", "Hello, MongoDB!".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("file", "test2.txt", "text/plain", "Hello, MongoDB!".getBytes());

		// Store the first file using the service
		fileService.storeFile(file1, "PUBLIC", List.of("tag1", "tag2"), "user1");

		// Try to store the second file with the same content and expect an exception
		assertThatThrownBy(() -> fileService.storeFile(file2, "PUBLIC", List.of("tag1", "tag2"), "user1"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("File already exists");
	}

	@Test
	void testDeleteFileByOwner() throws IOException, NoSuchAlgorithmException {
		// Create a mock MultipartFile
		MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, MongoDB!".getBytes());

		// Store the file using the service
		FileDocument savedDoc = fileService.storeFile(file, "PUBLIC", List.of("tag1", "tag2"), "user1");

		// Delete the file using the correct userId
		fileService.deleteFile(savedDoc.getId(), "user1");

		// Verify the file is deleted
		Optional<FileDocument> retrievedDocOptional = fileService.getFileById(savedDoc.getId());
		assertThat(retrievedDocOptional).isNotPresent();
	}

	@Test
	void testDeleteFileByNonOwner() throws IOException, NoSuchAlgorithmException {
		// Create a mock MultipartFile
		MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, MongoDB!".getBytes());

		// Store the file using the service
		FileDocument savedDoc = fileService.storeFile(file, "PUBLIC", List.of("tag1", "tag2"), "user1");

		// Try to delete the file using a different userId and expect an exception
		assertThatThrownBy(() -> fileService.deleteFile(savedDoc.getId(), "user2"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("User not authorized to delete this file");
	}
}
