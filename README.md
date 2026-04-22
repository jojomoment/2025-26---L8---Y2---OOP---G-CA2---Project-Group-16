For our project we decided to make a global music production & remix hub.

**Domain Description:** A collaborative platform where music producers upload "stems" (individual instrument tracks) for others to remix.

**Entities:**
- **Track:** songTitle, BPM, durationInSeconds, audioFile (BLOB)
- **Producer:** stageName, tracksUploaded, averageRating
- **Studio:** locationName, roomCapacity, hourlyRate

---

## Stage 2: Multithreaded Server & Protocol

### Overview
- **Server:** Port 9001, multithreaded (ExecutorService), MySQL-backed
- **Communication:** JSON-based TCP sockets
- **Response Format:** All responses wrapped in `ServerResponse<T>` with `{success, message, data}`

### API Endpoints

#### 1. GET_ALL
**Request:** `GET_ALL`  
**Response:** `ServerResponse<List<MusicTrack>>`

```json
{
  "success": true,
  "message": "Tracks fetched",
  "data": [
    {"songId": 1, "songTitle": "Demo Song 1", "BPM": 120, "durationInSeconds": 180.0}
  ]
}
```

---

#### 2. GET_BY_ID
**Request:** `GET_BY_ID:<id>` (e.g., `GET_BY_ID:1`)  
**Response:** `ServerResponse<MusicTrack>`

```json
{"success": true, "message": "Track fetched", "data": {"songId": 1, "songTitle": "Demo Song 1", "BPM": 120, "durationInSeconds": 180.0}}
```

**Not Found:**
```json
{"success": false, "message": "Track not found", "data": null}
```

---

#### 3. INSERT
**Request:** `INSERT:<json>` (JSON without ID)  
**Response:** `ServerResponse<MusicTrack>` (includes auto-generated ID)

```json
{"success": true, "message": "Track created", "data": {"songId": 13, "songTitle": "New Song", "BPM": 110, "durationInSeconds": 160.0}}
```

---

#### 4. UPDATE
**Request:** `UPDATE:<json>` (full MusicTrack JSON with ID)  
**Response:** `ServerResponse<MusicTrack>`

```json
{"success": true, "message": "Track updated", "data": {"songId": 2, "songTitle": "Updated Title", "BPM": 130, "durationInSeconds": 210.0}}
```

---

#### 5. DELETE
**Request:** `DELETE:<id>` (e.g., `DELETE:5`)  
**Response:** `ServerResponse<Boolean>`

```json
{"success": true, "message": "Track deleted", "data": null}
```

---

### Error Handling
- All errors wrapped in `ServerResponse` (no raw exceptions to client)
- Invalid requests return: `{"success": false, "message": "Unknown request", "data": null}`

### Compile & Run

**From `Global_Music_Production_Hub/` directory:**

```bash
# Compile Server
javac -cp "../lib/*" -d bin -sourcepath src/main/java src/main/java/musichub/server/Server.java

# Compile Client
javac -cp "../lib/*" -d bin -sourcepath src/main/java src/main/java/musichub/client/TestClient.java

# Terminal 1: Start Server
java -cp "../lib/*;bin" musichub.server.Server

# Terminal 2: Run Client
java -cp "../lib/*;bin" musichub.client.TestClient
```

### Database Setup
```bash
mysql -u root < ../Global_Music_Production_Hub/sql/create_table.sql
```

### Dependencies
- **Gson 2.10.1** (JSON serialization)
- **MySQL Connector 8.0.33** (Database)
- **Java 25**

---

ChatGPT, 2026. Response to a question about BLOB in Java. OpenAI, 8 March. [online] Available at: <https://chat.openai.com> [Accessed 8 March 2026].
