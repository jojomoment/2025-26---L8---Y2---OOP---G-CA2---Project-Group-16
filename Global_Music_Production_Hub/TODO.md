# Feature Plan

## //F17 — Binary Schema Extension (DONE)
- Added `audioFile BLOB`, `file_name VARCHAR(255)`, `content_type VARCHAR(100)`, `file_size INT` to `music_tracks` table in `mysqlSetup.sql`.
- Added `private transient byte[] audioFile`, `fileName`, `contentType`, `fileSize` to `MusicTrack.java`. `transient` stops Gson from serialising raw bytes into JSON.
- Updated `MusicTrackDao` interface: `insert()`, `updateTrack()`, and `getMetadataById()` signatures now include the binary fields.
- `JdbcMusicTrackDao` uses `setBytes()` to insert/update BLOBs and `getBytes()` to read them back.
- `FakeMusicTrackDao` stores the new fields in-memory for testing.

## //F18 — Binary File Upload (DONE)
- Client reads file from disk with `Files.readAllBytes()`.
- Client extracts metadata: `fileName`, `contentType` via `Files.probeContentType()`, `fileSize` from array length.
- Client Base64-encodes the bytes with `Base64.getEncoder().encodeToString()` so binary data can travel inside JSON.
- Client sends `UPLOAD:{json}` containing song data, metadata, and `fileData` (Base64 string).
- Server parses JSON into `UploadRequest`, decodes Base64 back to `byte[]` with `Base64.getDecoder().decode()`.
- Server builds a `MusicTrack`, calls `dao.insert()`, which uses `PreparedStatement.setBytes()` to store the BLOB.
- Server returns `ServerResponse<MusicTrack>` with the auto-generated ID. Because `audioFile` is transient, the JSON response only contains metadata.

## //F19 — Binary File Retrieval (DONE)
- Client sends `GET_FILE_BY_ID:1`.
- Server extracts ID, calls `dao.getMusicTrackById(id)` to fetch the full record including BLOB.
- If not found, return `ServerResponse.error("Track not found")`.
- If found, server Base64-encodes `track.getAudioFile()` manually because `audioFile` is transient and Gson will ignore it.
- Server builds a `Map<String, Object>` payload with metadata plus a `base64Data` string field, wraps it in `ServerResponse`, and sends as JSON.
- Client parses the JSON response with `JsonParser`, extracts `base64Data` and `fileName`, decodes Base64 back to `byte[]`, then writes bytes to disk using `Files.write()` with the original filename prefixed by `retrieved_`.
- //F19 note: because `audioFile` is transient in `MusicTrack`, we cannot rely on Gson to serialise the binary data. Instead we manually Base64-encode it into a non-transient string field inside a Map payload. This keeps JSON small and text-safe while still allowing the full file to travel inside the response.

## //F20 — File Metadata Query (DONE)
- Added `getMetadataById(int songId)` to `MusicTrackDao`.
- `JdbcMusicTrackDao` runs a SELECT that excludes the `audioFile` column, builds a `MusicTrack` with `audioFile` set to `null`.
- `FakeMusicTrackDao` returns a copy with `audioFile` set to `null`.
- Server handles `GET_METADATA_BY_ID:` in `ClientHandler` and returns `ServerResponse<MusicTrack>`.
- Client calls `GET_METADATA_BY_ID:1` to test.

## //F21 — Disconnect / Exit (DONE)
- Client sends plain text `DISCONNECT` before closing the socket.
- Server detects it in `ClientHandler`, logs the client IP, sends `ServerResponse.ok("Disconnected", null)`.
- Server uses `break` to exit the while-loop, and try-with-resources closes socket and streams cleanly.

## //F22 — Core Unit Tests (NOT DONE)
- Need JUnit 5 JAR in `lib/` folder and on classpath.
- Need test classes in `src/test/java/musichub/`.
- Suggested tests:
  - DAO tests using `FakeMusicTrackDao`: insert, getById, delete, update, metadata query returns null audioFile, findByFilter.
  - JSON tests: round-trip serialisation, list serialisation, transient field excluded from JSON.
  - Domain tests: constructor rejects blank title, zero BPM, negative duration; setters validate input.
  - ServerResponse tests: ok response has success=true, error response has success=false.
- Requirement: at least 3 meaningful tests per team member.

## Quick Status
| Feature | Status |
|---------|--------|
| F17 | DONE |
| F18 | DONE |
| F19 | DONE |
| F20 | DONE |
| F21 | DONE |
| F22 | NOT DONE |

## Interview Notes
- `transient` prevents Gson from including `audioFile` in JSON because raw bytes would become a massive array of numbers.
- Base64 is used because JSON is text-only and cannot hold raw binary data.
- `PreparedStatement` prevents SQL injection and handles BLOBs via `setBytes()`.
- Try-with-resources guarantees connections and streams close automatically.
- `getMusicTrackById` fetches the BLOB; `getMetadataById` does not, saving bandwidth.
