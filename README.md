2025-26 - L8 - Y2 - OOP - G-CA2 - Project-Group-16

1. Project Overview
Domain summary (150–200 words)

The Music Production Hub is a digital platform designed for music producers, artists, and recording studios to manage music tracks and related production data. The system provides a centralized database for storing music tracks, producer information, and binary audio files with metadata. Users can upload, retrieve, update, and delete music tracks while maintaining structured records of music production activity.

The platform supports binary file handling through MySQL BLOB storage, allowing audio files such as MP3 or WAV files to be uploaded and retrieved between the client and server. Associated metadata including filename, content type, and file size is stored alongside the binary data to improve file management and retrieval efficiency.

The application uses a client-server architecture with JSON-based communication over sockets. Requests are processed through a multithreaded server using ExecutorService, while DAO classes provide database access through JDBC and PreparedStatements. The project demonstrates object-oriented programming concepts including interfaces, generics, collections, functional interfaces, and JSON serialization/deserialization. Comprehensive JUnit 5 testing ensures reliability across DAO operations, JSON conversion, server request handling, and binary file functionality.

Team
Group ID: 2025-26---L8---Y2---OOP---G-CA2---Project-Group-16
Members
- Student A
- Student B

Key Features
- JDBC DAO layer with CRUD operations
- Client-server architecture using sockets
- JSON protocol with ServerResponse<T>
- Multithreaded server using ExecutorService
- Binary file upload and retrieval using MySQL BLOB storage
- JUnit 5 test suite with coverage validation

2. How to Run
Prerequisites
- Java 21+
- IntelliJ IDEA or VS Code
- MySQL Server 8.0+
- Maven 3.9+

2.1 Database Setup
Create the database:

CREATE DATABASE musichub;

Run the setup script:

mysql -u root -p musichub < sql/create_table.sql

Verify tables:
- music_tracks
- music_producers

2.2 Configure Credentials
Create:

src/main/resources/db.properties

Add:

db.url=jdbc:mysql://localhost:3306/musichub
db.user=your_username
db.password=your_password

2.3 Run the Server
Main class:

musichub.server.Server

Command:

mvn exec:java -Dexec.mainClass="musichub.server.Server"

Expected output:
- Server listening on port 5000

2.4 Run the Client
Main class:

musichub.client.TestClient

Command:

mvn exec:java -Dexec.mainClass="musichub.client.TestClient"

3. Architecture Summary
3.1 N-tier Overview
- Client Layer: Sends requests and displays responses
- Server Layer: Handles socket connections and request routing
- DAO Layer: JDBC persistence layer
- Database Layer: MySQL relational database with BLOB storage

3.2 Architecture Diagram

4. JSON Protocol Documentation
4.1 Envelope Format

Response structure:

{
  "status": "SUCCESS",
  "message": "Tracks fetched",
  "data": {}
}

4.2 Supported Request Types
Request Type	Description
GET_ALL	Fetch all tracks
GET_BY_ID:id	Fetch track by ID
INSERT:json	Insert new track
UPDATE:json	Update track
DELETE:id	Delete track
UPLOAD:json	Upload binary file
GET_METADATA:id	Retrieve metadata only
DISCONNECT	Close connection cleanly

5. Binary File Handling (Stage 3+)
5.1 Binary Data in the Domain

Binary data represents uploaded music/audio files stored in the database.

5.2 Database Storage
The music_tracks table contains:
- audioFile BLOB
- file_name VARCHAR
- content_type VARCHAR
- file_size INT

5.3 Binary Operations
Operation	Description
Upload	Client Base64-encodes file and sends JSON
Retrieve	Server returns Base64-encoded file
Metadata query	Retrieves filename/type/size only

6. Testing & Coverage
6.1 Running Tests
Command:

mvn test

6.2 Test Categories
- DAO tests
- JSON serialization tests
- ClientHandler/server tests
- Binary upload/retrieval tests
- Integration tests

6.3 Coverage Evidence


Coverage target achieved:
- >=70% line coverage

7. Design Patterns, Generics, Lambdas
7.1 Patterns Used
- DAO Pattern
- Factory-style database connection management
- Template Method style JDBC workflow

7.2 Generics
- ServerResponse<T>
- Optional<T>
- List<T>

7.3 Lambdas / Functional Interfaces
- Predicate<MusicTrack>
- Java Stream API filtering

8. Screencast (Stage 4)
Duration: 8–10 minutes
Demonstrates:
- CRUD operations
- Binary upload/retrieval
- Tests running
- Coverage report
- Architecture explanation

9. Contribution Matrix
Task	Primary Author	Contributor
Database schema	Student A	Student B
DAO layer	Student B	Student A
JSON utilities	Student A	Student B
Client-server communication	Student B	Student A
Binary file handling	Student A	Student B
Testing suite	Student B	Student A
README documentation	Student A	Student B
Final debugging/integration	Student A	Student B

ChatGPT, 2026. Response to a question about BLOB in Java. OpenAI, 8 March. [online] Available at: <https://chat.openai.com> [Accessed 8 March 2026].

