use storage;

db.files.insertMany([
  {
    "filename": "example1.txt",
    "contentType": "text/plain",
    "content": BinData(0, "VGhpcyBpcyB0aGUgY29udGVudCBvZiBleGFtcGxlMS50eHQu"), // "This is the content of example1.txt." in Base64
    "fileSize": 1024,
    "uploadDate": new Date("2024-06-01T12:00:00Z"),
    "visibility": "PUBLIC",
    "tags": ["tag1", "tag2"],
    "userId": "user1"
  },
  {
    "filename": "example2.pdf",
    "contentType": "application/pdf",
    "content": BinData(0, "QmluYXJ5IFBERiBjb250ZW50IGVuY29kZWQgYXMgQmFzZTY0Lg=="), // "Binary PDF content encoded as Base64." in Base64
    "fileSize": 2048,
    "uploadDate": new Date("2024-06-02T09:30:00Z"),
    "visibility": "PRIVATE",
    "tags": ["tag3"],
    "userId": "user2"
  }
]);

db.tags.insertMany([
  {
    "name": "tag1"
  },
  {
    "name": "tag2"
  },
  {
    "name": "tag3"
  }
]);
