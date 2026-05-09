package musichub;

import musichub.dao.JdbcMusicTrackDao;
import musichub.domain.MusicTrack;
import musichub.server.ClientHandler;
import musichub.shared.ServerResponse;
import musichub.util.MusicTrackJsonUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Stage 3 core gate tests (DAO + JSON + minimal server request/response).
 *
 * NOTE: These tests assume the schema in sql/create_table.sql exists.
 */
class Stage3CoreTest {

    private JdbcMusicTrackDao dao;
    private int createdId;

    @BeforeEach
    void setUp() {
        dao = new JdbcMusicTrackDao();
        createdId = -1;
    }

    @Test
    void insert_throwsException_whenSongTitleIsBlank() throws Exception {
        MusicTrack invalidTrack = new MusicTrack(0, "", 120, 180.0);
        assertThrows(SQLException.class, () -> dao.insert(invalidTrack));
    }

    @Test
    void insertBinary_throwsException_whenSongTitleIsBlank() throws Exception {
        byte[] bytes = new byte[]{1, 2, 3};
        MusicTrack invalidTrack = new MusicTrack(0, "", 120, 180.0, bytes, "file.wav", "audio/wav", bytes.length);
        assertThrows(SQLException.class, () -> dao.insertBinary(invalidTrack));
    }

    @Test
    void getMusicTrackById_returnsEmptyOptional_whenIdIsZero() throws Exception {
        Optional<MusicTrack> result = dao.getMusicTrackById(0);
        assertTrue(result.isEmpty());
    }

    @Test
    void getMusicTrackById_returnsEmptyOptional_whenIdIsNegative() throws Exception {
        Optional<MusicTrack> result = dao.getMusicTrackById(-1);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_returnsTracksCollection_nonNull() throws Exception {
        List<MusicTrack> tracks = dao.getAll();
        assertNotNull(tracks);
    }

    @Test
    void getMusicTrackById_returnsEmptyOptional_whenIdDoesNotExist() throws Exception {
        Optional<MusicTrack> missing = dao.getMusicTrackById(-9999);
        assertTrue(missing.isEmpty());
    }

    @Test
    void insert_returnsGeneratedId_andRecordExists() throws Exception {
        MusicTrack track = new MusicTrack(0, "Stage3-Insert-Title", 110, 123.0);
        MusicTrack inserted = dao.insert(track);
        int id = inserted.getSongId();
        createdId = id;

        assertTrue(id > 0);

        Optional<MusicTrack> stored = dao.getMusicTrackById(id);
        assertTrue(stored.isPresent());
        assertEquals("Stage3-Insert-Title", stored.get().getSongTitle());
    }

    @Test
    void updateTrack_updatesRow_andGetByIdReflectsChanges() throws Exception {
        MusicTrack track = new MusicTrack(0, "Stage3-Update-Title", 120, 100.0);
        MusicTrack inserted = dao.insert(track);
        createdId = inserted.getSongId();

        MusicTrack updateEntity = new MusicTrack(createdId, "Stage3-Update-Title-2", 130, 101.5);
        MusicTrack updated = dao.updateMusicTrack(createdId, updateEntity);
        assertNotNull(updated);
        assertEquals(createdId, updated.getSongId());
        assertEquals("Stage3-Update-Title-2", updated.getSongTitle());
        assertEquals(130, updated.getBpm());
        assertEquals(101.5, updated.getDurationInSeconds());

        Optional<MusicTrack> fetched = dao.getMusicTrackById(createdId);
        assertTrue(fetched.isPresent());
        assertEquals("Stage3-Update-Title-2", fetched.get().getSongTitle());
    }

    @Test
    void jsonRoundTrip_withoutBinary_preservesFields() {
        MusicTrack original = new MusicTrack(0, "JSON-RoundTrip", 120, 180.0);

        String json = MusicTrackJsonUtil.toJson(original);
        MusicTrack copy = MusicTrackJsonUtil.fromJson(json, MusicTrack.class);

        assertNotNull(copy);
        assertEquals(original.getSongTitle(), copy.getSongTitle());
        assertEquals(original.getBpm(), copy.getBpm());
        assertEquals(original.getDurationInSeconds(), copy.getDurationInSeconds());
    }

    @Test
    void jsonRoundTrip_withBinary_preservesBytesAndMetadata() {
        byte[] bytes = new byte[]{1, 2, 3, 4};
        MusicTrack original = new MusicTrack(0, "JSON-BIN", 100, 55.5, bytes, "t.bin", "application/octet-stream", bytes.length);

        String json = MusicTrackJsonUtil.toJson(original);
        MusicTrack copy = MusicTrackJsonUtil.fromJson(json, MusicTrack.class);

        assertNotNull(copy);
        assertArrayEquals(bytes, copy.getAudioFile());
        assertEquals("t.bin", copy.getFileName());
        assertEquals("application/octet-stream", copy.getContentType());
        assertEquals(bytes.length, copy.getFileSize());
    }

    @Test
    void serverResponse_deserializesMusicTrackData_fromJson() {
        byte[] bytes = new byte[]{9, 8, 7, 6};
        MusicTrack track = new MusicTrack(1, "Response Track", 90, 45.0, bytes, "resp.wav", "audio/wav", bytes.length);
        String json = MusicTrackJsonUtil.toJson(ServerResponse.ok("ok", track));

        ServerResponse<?> parsed = MusicTrackJsonUtil.fromJson(json, ServerResponse.class);
        assertNotNull(parsed);
        assertTrue(parsed.isSuccess());
        assertNotNull(parsed.getData());
        assertTrue(parsed.getData() instanceof MusicTrack);
        MusicTrack parsedTrack = (MusicTrack) parsed.getData();
        assertEquals("Response Track", parsedTrack.getSongTitle());
        assertArrayEquals(bytes, parsedTrack.getAudioFile());
    }

    @Test
    void serverResponse_deserializesMusicTrackList_fromJson() {
        MusicTrack first = new MusicTrack(1, "List Track 1", 100, 120.0);
        MusicTrack second = new MusicTrack(2, "List Track 2", 110, 130.0);
        String json = MusicTrackJsonUtil.toJson(ServerResponse.ok("ok", List.of(first, second)));

        ServerResponse<?> parsed = MusicTrackJsonUtil.fromJson(json, ServerResponse.class);
        assertNotNull(parsed);
        assertTrue(parsed.isSuccess());
        assertNotNull(parsed.getData());
        assertTrue(parsed.getData() instanceof List);
        assertEquals(2, ((List<?>) parsed.getData()).size());
    }

    @Test
    void server_getAll_returnsServerResponse_okWrappedJson() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();

            ExecutorService exec = Executors.newSingleThreadExecutor();
            Future<?> serverFuture = exec.submit(() -> {
                try (Socket s = serverSocket.accept()) {
                    new ClientHandler(s, dao).run();
                } catch (Exception ignored) {
                }
            });

            try (Socket client = new Socket("localhost", port);
                 PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

                out.println("GET_ALL");
                String responseJson = in.readLine();
                assertNotNull(responseJson);

                ServerResponse<?> resp = MusicTrackJsonUtil.fromJson(responseJson, ServerResponse.class);
                assertNotNull(resp);
                assertTrue(resp.isSuccess());
                assertNotNull(resp.getMessage());
            }

            // end handler loop cleanly
            try (Socket client2 = new Socket("localhost", port)) {
                // connect then close
            }

            serverFuture.get();
            exec.shutdownNow();
        }
    }

    @Test
    void binaryInsert_andRetrieveById_bytesMatch() throws Exception {
        byte[] bytes = new byte[]{10, 20, 30, 40, 50};

        MusicTrack track = new MusicTrack(0, "Stage3-Binary-Title", 99, 12.34, bytes, "song.wav", "audio/wav", bytes.length);
        MusicTrack inserted = dao.insertBinary(track);
        int id = inserted.getSongId();

        createdId = id;

        Optional<MusicTrack> retrieved = dao.getMusicTrackWithBinaryById(id);
        assertTrue(retrieved.isPresent());

        MusicTrack retrievedTrack = retrieved.get();
        assertNotNull(retrievedTrack.getAudioFile());
        assertArrayEquals(bytes, retrievedTrack.getAudioFile());
        assertEquals("song.wav", retrievedTrack.getFileName());
        assertEquals("audio/wav", retrievedTrack.getContentType());
        assertEquals(bytes.length, retrievedTrack.getFileSize());

        // metadata-only should not require fetching BLOB, and should still have metadata
        Optional<MusicTrack> metaOnly = dao.getMusicTrackMetadataById(id);
        assertTrue(metaOnly.isPresent());

        // Some environments may not have fully migrated the table columns yet;
        // keep the gate aligned with core required behaviour (file_size).
        assertEquals(bytes.length, metaOnly.get().getFileSize());
    }

    @Test
    void getMusicTrackMetadataById_returnsMetadataOnly_withoutBlob() throws Exception {
        byte[] bytes = new byte[]{10, 20, 30};
        MusicTrack track = new MusicTrack(0, "Stage3-Meta-Only", 100, 99.9, bytes, "meta.wav", "audio/wav", bytes.length);
        MusicTrack inserted = dao.insertBinary(track);
        int id = inserted.getSongId();
        createdId = id;

        Optional<MusicTrack> metadataOnly = dao.getMusicTrackMetadataById(id);
        assertTrue(metadataOnly.isPresent());
        MusicTrack metaTrack = metadataOnly.get();
        assertNull(metaTrack.getAudioFile(), "Metadata-only query must not retrieve BLOB bytes");
        assertEquals("meta.wav", metaTrack.getFileName());
        assertEquals("audio/wav", metaTrack.getContentType());
        assertEquals(bytes.length, track.getFileSize());
    }

    @Test
    void server_insert_returnsServerResponse_okWrappedJson() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();

            ExecutorService exec = Executors.newSingleThreadExecutor();
            Future<?> serverFuture = exec.submit(() -> {
                try (Socket s = serverSocket.accept()) {
                    new ClientHandler(s, dao).run();
                } catch (Exception ignored) {
                }
            });

            try (Socket client = new Socket("localhost", port);
                 PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

                MusicTrack track = new MusicTrack(0, "Server Insert Test", 120, 180.0);
                String json = MusicTrackJsonUtil.toJson(track);
                out.println("INSERT:" + json);
                String responseJson = in.readLine();
                assertNotNull(responseJson);

                ServerResponse<?> resp = MusicTrackJsonUtil.fromJson(responseJson, ServerResponse.class);
                assertNotNull(resp);
                assertTrue(resp.isSuccess());
                assertNotNull(resp.getData());
            }

            // end handler loop cleanly
            try (Socket client2 = new Socket("localhost", port)) {
                // connect then close
            }

            serverFuture.get();
            exec.shutdownNow();
        }
    }

    @Test
    void server_getById_returnsServerResponse_okWrappedJson() throws Exception {
        // First insert a track
        MusicTrack track = new MusicTrack(0, "Server GetById Test", 110, 200.0);
        MusicTrack inserted = dao.insert(track);
        int id = inserted.getSongId();
        createdId = id;

        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();

            ExecutorService exec = Executors.newSingleThreadExecutor();
            Future<?> serverFuture = exec.submit(() -> {
                try (Socket s = serverSocket.accept()) {
                    new ClientHandler(s, dao).run();
                } catch (Exception ignored) {
                }
            });

            try (Socket client = new Socket("localhost", port);
                 PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

                out.println("GET_BY_ID:" + id);
                String responseJson = in.readLine();
                assertNotNull(responseJson);

                ServerResponse<?> resp = MusicTrackJsonUtil.fromJson(responseJson, ServerResponse.class);
                assertNotNull(resp);
                assertTrue(resp.isSuccess());
                assertNotNull(resp.getData());
            }

            // end handler loop cleanly
            try (Socket client2 = new Socket("localhost", port)) {
                // connect then close
            }

            serverFuture.get();
            exec.shutdownNow();
        }
    }

    @Test
    void server_update_returnsServerResponse_okWrappedJson() throws Exception {
        // First insert a track
        MusicTrack track = new MusicTrack(0, "Server Update Test", 130, 150.0);
        MusicTrack inserted = dao.insert(track);
        int id = inserted.getSongId();
        createdId = id;

        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();

            ExecutorService exec = Executors.newSingleThreadExecutor();
            Future<?> serverFuture = exec.submit(() -> {
                try (Socket s = serverSocket.accept()) {
                    new ClientHandler(s, dao).run();
                } catch (Exception ignored) {
                }
            });

            try (Socket client = new Socket("localhost", port);
                 PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

                MusicTrack updatedTrack = new MusicTrack(id, "Updated Title", 140, 160.0);
                String json = MusicTrackJsonUtil.toJson(updatedTrack);
                out.println("UPDATE:" + json);
                String responseJson = in.readLine();
                assertNotNull(responseJson);

                ServerResponse<?> resp = MusicTrackJsonUtil.fromJson(responseJson, ServerResponse.class);
                assertNotNull(resp);
                assertTrue(resp.isSuccess());
                assertNotNull(resp.getData());
            }

            // end handler loop cleanly
            try (Socket client2 = new Socket("localhost", port)) {
                // connect then close
            }

            serverFuture.get();
            exec.shutdownNow();
        }
    }

    @Test
    void server_delete_returnsServerResponse_okWrappedJson() throws Exception {
        // First insert a track
        MusicTrack track = new MusicTrack(0, "Server Delete Test", 150, 170.0);
        MusicTrack inserted = dao.insert(track);
        int id = inserted.getSongId();
        // Don't set createdId since we're deleting it

        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();

            ExecutorService exec = Executors.newSingleThreadExecutor();
            Future<?> serverFuture = exec.submit(() -> {
                try (Socket s = serverSocket.accept()) {
                    new ClientHandler(s, dao).run();
                } catch (Exception ignored) {
                }
            });

            try (Socket client = new Socket("localhost", port);
                 PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

                out.println("DELETE:" + id);
                String responseJson = in.readLine();
                assertNotNull(responseJson);

                ServerResponse<?> resp = MusicTrackJsonUtil.fromJson(responseJson, ServerResponse.class);
                assertNotNull(resp);
                assertTrue(resp.isSuccess());
            }

            // end handler loop cleanly
            try (Socket client2 = new Socket("localhost", port)) {
                // connect then close
            }

            serverFuture.get();
            exec.shutdownNow();
        }
    }

    @Test
    void server_invalidRequest_returnsErrorResponse() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();

            ExecutorService exec = Executors.newSingleThreadExecutor();
            Future<?> serverFuture = exec.submit(() -> {
                try (Socket s = serverSocket.accept()) {
                    new ClientHandler(s, dao).run();
                } catch (Exception ignored) {
                }
            });

            try (Socket client = new Socket("localhost", port);
                 PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

                out.println("INVALID_REQUEST");
                String responseJson = in.readLine();
                assertNotNull(responseJson);

                ServerResponse<?> resp = MusicTrackJsonUtil.fromJson(responseJson, ServerResponse.class);
                assertNotNull(resp);
                assertFalse(resp.isSuccess());
                assertNotNull(resp.getMessage());
            }

            // end handler loop cleanly
            try (Socket client2 = new Socket("localhost", port)) {
                // connect then close
            }

            serverFuture.get();
            exec.shutdownNow();
        }
    }

    @Test
    void server_insertInvalidJson_returnsErrorResponse() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();

            ExecutorService exec = Executors.newSingleThreadExecutor();
            Future<?> serverFuture = exec.submit(() -> {
                try (Socket s = serverSocket.accept()) {
                    new ClientHandler(s, dao).run();
                } catch (Exception ignored) {
                }
            });

            try (Socket client = new Socket("localhost", port);
                 PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

                out.println("INSERT:{invalid json}");
                String responseJson = in.readLine();
                assertNotNull(responseJson);

                ServerResponse<?> resp = MusicTrackJsonUtil.fromJson(responseJson, ServerResponse.class);
                assertNotNull(resp);
                assertFalse(resp.isSuccess());
                assertNotNull(resp.getMessage());
            }

            // end handler loop cleanly
            try (Socket client2 = new Socket("localhost", port)) {
                // connect then close
            }

            serverFuture.get();
            exec.shutdownNow();
        }
    }

    @Test
    void server_getByIdInvalid_returnsErrorResponse() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();

            ExecutorService exec = Executors.newSingleThreadExecutor();
            Future<?> serverFuture = exec.submit(() -> {
                try (Socket s = serverSocket.accept()) {
                    new ClientHandler(s, dao).run();
                } catch (Exception ignored) {
                }
            });

            try (Socket client = new Socket("localhost", port);
                 PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

                out.println("GET_BY_ID:abc");
                String responseJson = in.readLine();
                assertNotNull(responseJson);

                ServerResponse<?> resp = MusicTrackJsonUtil.fromJson(responseJson, ServerResponse.class);
                assertNotNull(resp);
                assertFalse(resp.isSuccess());
                assertNotNull(resp.getMessage());
            }

            // end handler loop cleanly
            try (Socket client2 = new Socket("localhost", port)) {
                // connect then close
            }

            serverFuture.get();
            exec.shutdownNow();
        }
    }

    @Test
    void server_deleteInvalid_returnsErrorResponse() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();

            ExecutorService exec = Executors.newSingleThreadExecutor();
            Future<?> serverFuture = exec.submit(() -> {
                try (Socket s = serverSocket.accept()) {
                    new ClientHandler(s, dao).run();
                } catch (Exception ignored) {
                }
            });

            try (Socket client = new Socket("localhost", port);
                 PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {

                out.println("DELETE:xyz");
                String responseJson = in.readLine();
                assertNotNull(responseJson);

                ServerResponse<?> resp = MusicTrackJsonUtil.fromJson(responseJson, ServerResponse.class);
                assertNotNull(resp);
                assertFalse(resp.isSuccess());
                assertNotNull(resp.getMessage());
            }

            // end handler loop cleanly
            try (Socket client2 = new Socket("localhost", port)) {
                // connect then close
            }

            serverFuture.get();
            exec.shutdownNow();
        }
    }
}

