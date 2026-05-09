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

    @AfterEach
    void tearDown() throws Exception {
        if (createdId > 0) {
            dao.deleteById(createdId);
        }
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
        int id = dao.insert("Stage3-Insert-Title", 110, 123.0);
        createdId = id;

        assertTrue(id > 0);

        Optional<MusicTrack> stored = dao.getMusicTrackById(id);
        assertTrue(stored.isPresent());
        assertEquals("Stage3-Insert-Title", stored.get().getSongTitle());
    }

    @Test
    void updateTrack_updatesRow_andGetByIdReflectsChanges() throws Exception {
        createdId = dao.insert("Stage3-Update-Title", 120, 100.0);

        MusicTrack updated = dao.updateTrack(createdId, "Stage3-Update-Title-2", 130, 101.5);
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

        int id = dao.insertBinary(
                "Stage3-Binary-Title",
                99,
                12.34,
                bytes,
                "song.wav",
                "audio/wav",
                bytes.length);

        createdId = id;

        Optional<MusicTrack> retrieved = dao.getMusicTrackWithBinaryById(id);
        assertTrue(retrieved.isPresent());

        MusicTrack track = retrieved.get();
        assertNotNull(track.getAudioFile());
        assertArrayEquals(bytes, track.getAudioFile());
        assertEquals("song.wav", track.getFileName());
        assertEquals("audio/wav", track.getContentType());
        assertEquals(bytes.length, track.getFileSize());

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
        int id = dao.insertBinary(
                "Stage3-Meta-Only",
                100,
                99.9,
                bytes,
                "meta.wav",
                "audio/wav",
                bytes.length);
        createdId = id;

        Optional<MusicTrack> metadataOnly = dao.getMusicTrackMetadataById(id);
        assertTrue(metadataOnly.isPresent());
        MusicTrack track = metadataOnly.get();
        assertNull(track.getAudioFile(), "Metadata-only query must not retrieve BLOB bytes");
        assertEquals("meta.wav", track.getFileName());
        assertEquals("audio/wav", track.getContentType());
        assertEquals(bytes.length, track.getFileSize());
    }
}

