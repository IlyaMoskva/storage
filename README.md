# Storage Application
[![CI](https://github.com/IlyaMoskva/storage/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/IlyaMoskva/storage/actions/workflows/ci.yml)

## Purpose
The Storage application is a Spring Boot REST API that allows users to upload, manage, and download files. The application uses MongoDB to store files and metadata. It supports features such as file visibility (PUBLIC/PRIVATE), tagging, and ensures no duplicate files are uploaded.

## How to Build and Run with Docker
To build and run the application with Docker, follow these steps:

1. Ensure Docker is installed and running on your machine.
2. Build the Docker image:
    ```bash
    docker-compose build
    ```
3. Run the application using Docker Compose:
    ```bash
    docker-compose up
    ```
   This will start both the MongoDB and the Storage application containers.

## API Description

### File Management API

#### Upload a File
- **Endpoint**: `POST /api/files`
- **Request Parameters**:
    - `file`: The file to be uploaded.
    - `visibility`: Visibility setting (`PUBLIC` or `PRIVATE`).
    - `tags`: A list of up to 5 tags.
- **Response**: A download link for the uploaded file.

#### Get a File
- **Endpoint**: `GET /api/files/{id}`
- **Request Parameters**:
    - `id`: The ID of the file.
- **Response**: The file content.

#### Delete a File
- **Endpoint**: `DELETE /api/files/{id}`
- **Request Parameters**:
    - `id`: The ID of the file.
    - `userId`: The ID of the user (passed as a header).
- **Response**: No content.

### Tag Management API

#### Get All Tags
- **Endpoint**: `GET /api/tags`
- **Response**: A list of all available tags.

#### Create a Tag
- **Endpoint**: `POST /api/tags`
- **Request Parameters**:
    - `name`: The name of the tag.
- **Response**: The created tag.

#### Delete a Tag
- **Endpoint**: `DELETE /api/tags/{id}`
- **Request Parameters**:
    - `id`: The ID of the tag.
- **Response**: No content.

## Example Postman Requests
To test the API endpoints with Postman, you can create the following requests:

1. **Upload a File**:
    - Method: `POST`
    - URL: `http://localhost:8080/api/files`
    - Body: `form-data`
        - `file`: (Select a file)
        - `visibility`: `PUBLIC`
        - `tags`: `["tag1", "tag2"]`

2. **Get a File**:
    - Method: `GET`
    - URL: `http://localhost:8080/api/files/{id}`

3. **Delete a File**:
    - Method: `DELETE`
    - URL: `http://localhost:8080/api/files/{id}`
    - Headers:
        - `userId`: `your_user_id`

4. **Get All Tags**:
    - Method: `GET`
    - URL: `http://localhost:8080/api/tags`

5. **Create a Tag**:
    - Method: `POST`
    - URL: `http://localhost:8080/api/tags`
    - Body: `raw`
        - `name`: `your_tag_name`

6. **Delete a Tag**:
    - Method: `DELETE`
    - URL: `http://localhost:8080/api/tags/{id}`
