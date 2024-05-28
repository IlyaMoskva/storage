package com.teletronics.storage.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "files")
public class FileDocument {

    @Id
    private String id;
    private String filename;
    private String contentType;
    private byte[] content;

}
