package com.teletronics.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.testcontainers.containers.MongoDBContainer;

@SpringBootTest
class StorageApplicationTests {

	private static MongoDBContainer mongoDBContainer;

	//@Autowired
	//private MongoTemplate mongoTemplate;

	@BeforeAll
	public static void setUp() {
		mongoDBContainer = new MongoDBContainer("mongo:4.4.6");
		mongoDBContainer.start();

		System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());
	}

	@BeforeEach
	public void cleanDatabase() {
		//mongoTemplate.getDb().drop();
	}

	@Test
	void contextLoads() {
	}
}
