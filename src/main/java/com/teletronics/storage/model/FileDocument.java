package com.teletronics.storage.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "files")
public class FileDocument {

    @Id
    private String id;
    private String filename;
    private String contentType;
    private byte[] content;
    private long fileSize;
    private Date uploadDate;
    private String visibility; // PUBLIC or PRIVATE
    private List<String> tags;
    private String userId; // Assuming each file is associated with a user
    private String hash; // Hash of the file content
}
