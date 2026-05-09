package musichub;

import musichub.dao.JdbcMusicTrackDao;
import musichub.domain.MusicTrack;
import musichub.server.ClientHandler;
import musichub.util.MusicTrackJsonUtil;
import musichub.shared.ServerResponse;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Stage 4 extended suite.
 */
class Stage4ExtendedTest {

    private JdbcMusicTrackDao dao;
    private int createdId;

    @BeforeEach
    void setUp() throws Exception {
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
    void getAll_getById_insert_update_delete_filter_endToEnd() throws Exception {
        MusicTrack track1 = new MusicTrack(0, "S4-Track-1", 120, 180.0);
        MusicTrack inserted1 = dao.insert(track1);
        int id1 = inserted1.getSongId();
        MusicTrack track2 = new MusicTrack(0, "S4-Track-2", 130, 200.5);
        MusicTrack inserted2 = dao.insert(track2);
        int id2 = inserted2.getSongId();
        createdId = id1;

        try {
            List<MusicTrack> all = dao.getAll();
            assertNotNull(all);
            assertTrue(all.stream().anyMatch(t -> t.getSongId() == id1));
            assertTrue(all.stream().anyMatch(t -> t.getSongId() == id2));

            Optional<MusicTrack> by1 = dao.getMusicTrackById(id1);
            assertTrue(by1.isPresent());
            assertEquals("S4-Track-1", by1.get().getSongTitle());

            MusicTrack updateEntity = new MusicTrack(id1, "S4-Track-1-Updated", 140, 190.25);
            MusicTrack updated = dao.updateMusicTrack(id1, updateEntity);
            assertNotNull(updated);
            assertEquals(id1, updated.getSongId());
            assertEquals("S4-Track-1-Updated", updated.getSongTitle());
            assertEquals(140, updated.getBpm());
            assertEquals(190.25, updated.getDurationInSeconds());

            List<MusicTrack> filtered = dao.findByFilter(t -> t.getBpm() >= 140);
            assertNotNull(filtered);
            assertTrue(filtered.stream().anyMatch(t -> t.getSongId() == id1));

            assertTrue(dao.deleteById(id2));
            assertTrue(dao.getMusicTrackById(id2).isEmpty());
        } finally {
            dao.deleteById(id2);
        }
    }

    @Test
    void jsonRoundTrip_withBinaryPreservesFields() {
        byte[] audio = new byte[]{0, 1, 2, 3, 4, 5, 6, 7};
        MusicTrack original = new MusicTrack(0, "Binary JSON", 120, 180.0, audio, "bin.wav", "audio/wav", audio.length);

        String json = MusicTrackJsonUtil.toJson(original);
        MusicTrack copy = MusicTrackJsonUtil.fromJson(json, MusicTrack.class);

        assertNotNull(copy);
        assertEquals(0, copy.getSongId());
        assertEquals("Binary JSON", copy.getSongTitle());
        assertEquals(120, copy.getBpm());
        assertEquals(180.0, copy.getDurationInSeconds());
        assertNotNull(copy.getAudioFile());
        assertArrayEquals(audio, copy.getAudioFile());
        assertEquals("bin.wav", copy.getFileName());
        assertEquals("audio/wav", copy.getContentType());
        assertEquals(audio.length, copy.getFileSize());
    }

    @Test
    void server_getAll_returnsServerResponseWrappedJson() throws Exception {
        // Start server handler on ephemeral port with real socket client.
        // We do not start full Server (which loops forever). Instead we directly use ClientHandler with one socket.

        int port;
        try (java.net.ServerSocket serverSocket = new java.net.ServerSocket(0)) {
            port = serverSocket.getLocalPort();

            ExecutorService exec = Executors.newSingleThreadExecutor();
            Future<?> f = exec.submit(() -> {
                try (Socket s = serverSocket.accept()) {
                    new ClientHandler(s, dao).run();
                } catch (Exception ignored) {
                }
            });

            try (Socket client = new Socket("localhost", port);
                 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                 PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {

                out.println("GET_ALL");
                String responseJson = in.readLine();
                assertNotNull(responseJson);

                @SuppressWarnings("unchecked")
                ServerResponse<List> resp = MusicTrackJsonUtil.fromJson(responseJson, ServerResponse.class);
                assertNotNull(resp);
                assertNotNull(resp.getMessage());
            } finally {
                // Trigger handler loop end
                try (Socket client2 = new Socket("localhost", port)) {
                    // connect and immediately close
                } catch (Exception ignored) {
                }
            }

            f.get(3, TimeUnit.SECONDS);
            exec.shutdownNow();
        }
    }

    @Test
    void server_getMetadata_returnsMetadataOnly_withoutBlob() throws Exception {
        byte[] originalBytes = new byte[]{5, 6, 7, 8};
        MusicTrack track = new MusicTrack(0, "Metadata Server Test", 111, 77.7, originalBytes, "meta-retrieve.wav", "audio/wav", originalBytes.length);
        MusicTrack inserted = dao.insertBinary(track);
        int id = inserted.getSongId();
        createdId = id;

        try (java.net.ServerSocket serverSocket = new java.net.ServerSocket(0)) {
            int port = serverSocket.getLocalPort();

            ExecutorService exec = Executors.newSingleThreadExecutor();
            Future<?> f = exec.submit(() -> {
                try (Socket s = serverSocket.accept()) {
                    new ClientHandler(s, dao).run();
                } catch (Exception ignored) {
                }
            });

            try (Socket client = new Socket("localhost", port);
                 BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                 PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {

                out.println("GET_METADATA:" + id);
                String responseJson = in.readLine();
                assertNotNull(responseJson);

                ServerResponse<?> resp = MusicTrackJsonUtil.fromJson(responseJson, ServerResponse.class);
                assertNotNull(resp);
                assertTrue(resp.isSuccess());
                assertNotNull(resp.getData());
                assertTrue(resp.getData() instanceof MusicTrack);
                MusicTrack metadata = (MusicTrack) resp.getData();
                assertNull(metadata.getAudioFile());
                assertEquals("meta-retrieve.wav", metadata.getFileName());
                assertEquals("audio/wav", metadata.getContentType());
                assertEquals(originalBytes.length, metadata.getFileSize());
            } finally {
                try (Socket client2 = new Socket("localhost", port)) {
                } catch (Exception ignored) {
                }
            }

            f.get(3, TimeUnit.SECONDS);
            exec.shutdownNow();
        }
    }

    @Test
    void binaryUploadAndRetrieve_bytesMatch() throws Exception {
        // Prepare a known file (bytes) on disk.
        byte[] originalBytes = new byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90};
        File tmp = File.createTempFile("musichub-bin-test", ".wav");
        Files.write(tmp.toPath(), originalBytes);

        try {
            // For now we exercise the server protocol paths using TestClient helpers would be preferable,
            // but TestClient writes output to console and does not provide an assertion API.
            // We’ll call the ClientHandler directly via sockets.

            int port;
            try (java.net.ServerSocket serverSocket = new java.net.ServerSocket(0)) {
                port = serverSocket.getLocalPort();

                ExecutorService exec = Executors.newSingleThreadExecutor();
                Future<?> f = exec.submit(() -> {
                    try (Socket s = serverSocket.accept()) {
                        new ClientHandler(s, dao).run();
                    } catch (Exception ignored) {
                    }
                });

                try (Socket client = new Socket("localhost", port);
                     BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                     PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {

                    MusicTrack uploadMeta = new MusicTrack(
                            0,
                            "BinaryUploadTitle",
                            120,
                            180.0);
                    uploadMeta.setAudioFile(originalBytes);
                    uploadMeta.setFileName(tmp.getName());
                    uploadMeta.setContentType("audio/wav");
                    uploadMeta.setFileSize(originalBytes.length);

                    String uploadJson = MusicTrackJsonUtil.toJson(uploadMeta);
                    out.println("UPLOAD_BINARY:" + uploadJson);
                    String uploadResp = in.readLine();
                    assertNotNull(uploadResp);

                    ServerResponse<?> uploadObj = MusicTrackJsonUtil.fromJson(uploadResp, ServerResponse.class);
                    assertNotNull(uploadObj);
                    assertNotNull(uploadObj.getData());
                    // If the DB schema wasn't recreated with binary+metadata columns, the DAO may return empty strings/nulls.
                    // Fail only if data isn't a MusicTrack; bytes/metadata assertions below remain strict when data is present.
                    assertTrue(uploadObj.getData() instanceof MusicTrack, "upload should return MusicTrack as data");

                    MusicTrack uploadedTrack = (MusicTrack) uploadObj.getData();
                    createdId = uploadedTrack.getSongId();
                    assertTrue(createdId > 0);

                    assertNotNull(uploadedTrack.getAudioFile());
                    assertArrayEquals(originalBytes, uploadedTrack.getAudioFile());
                    assertEquals(tmp.getName(), uploadedTrack.getFileName());
                    assertEquals("audio/wav", uploadedTrack.getContentType());
                    assertEquals(originalBytes.length, uploadedTrack.getFileSize());

                    out.println("RETRIEVE_BINARY:" + createdId);
                    // Structured disconnect so the server thread exits cleanly
                    out.println("DISCONNECT");
                    String retrieveResp = in.readLine();
                    assertNotNull(retrieveResp);

                    ServerResponse<?> retrieveObj = MusicTrackJsonUtil.fromJson(retrieveResp, ServerResponse.class);
                    if (retrieveObj.getData() instanceof MusicTrack mt2) {
                        assertNotNull(mt2.getAudioFile());
                        assertArrayEquals(originalBytes, mt2.getAudioFile());
                    } else {
                        fail("Binary retrieval did not return a MusicTrack with audio bytes");
                    }
                }

                f.get(3, TimeUnit.SECONDS);
                exec.shutdownNow();
            }
        } finally {
            tmp.delete();
        }
    }

    @Test
    void jsonListRoundTrip_withDAOCompatibleData() throws Exception {
        MusicTrack a = new MusicTrack(0, "L1", 100, 10.0);
        MusicTrack b = new MusicTrack(0, "L2", 110, 20.0);
        List<MusicTrack> list = List.of(a, b);

        String json = MusicTrackJsonUtil.listToJson(list);
        List<MusicTrack> copy = MusicTrackJsonUtil.listFromJson(json, MusicTrack.class);
        assertEquals(2, copy.size());
        assertEquals("L1", copy.get(0).getSongTitle());
        assertEquals("L2", copy.get(1).getSongTitle());

        // Also ensure DAO accepts those core fields.
        MusicTrack inserted = dao.insert(copy.get(0));
        int id1 = inserted.getSongId();
        createdId = id1;
        MusicTrack stored = dao.getMusicTrackById(id1).orElseThrow();
        assertEquals("L1", stored.getSongTitle());
    }
}

