package musichub.util;

import musichub.domain.MusicTrack;
import musichub.shared.ServerResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MusicTrackJsonUtilTest {

    @Test
    void toJson_convertsMusicTrackToJson() {
        MusicTrack track = new MusicTrack(1, "Test Song", 120, 180.0);
        String json = MusicTrackJsonUtil.toJson(track);
        
        assertNotNull(json);
        assertTrue(json.contains("songTitle") || json.contains("Test Song"));
    }

    @Test
    void fromJson_deserializesMusicTrackFromJson() {
        MusicTrack original = new MusicTrack(1, "Test Song", 120, 180.0);
        String json = MusicTrackJsonUtil.toJson(original);
        
        MusicTrack deserialized = MusicTrackJsonUtil.fromJson(json, MusicTrack.class);
        
        assertNotNull(deserialized);
        assertEquals("Test Song", deserialized.getSongTitle());
        assertEquals(120, deserialized.getBpm());
        assertEquals(180.0, deserialized.getDurationInSeconds());
    }

    @Test
    void toJson_convertsServerResponseToJson() {
        ServerResponse<String> response = ServerResponse.ok("Success", "data");
        String json = MusicTrackJsonUtil.toJson(response);
        
        assertNotNull(json);
        assertTrue(json.contains("success"));
        assertTrue(json.contains("message"));
    }

    @Test
    void fromJson_deserializesServerResponseWithMusicTrack() {
        MusicTrack track = new MusicTrack(1, "Test Song", 120, 180.0);
        ServerResponse<MusicTrack> response = ServerResponse.ok("Success", track);
        String json = MusicTrackJsonUtil.toJson(response);
        
        ServerResponse<?> deserialized = MusicTrackJsonUtil.fromJson(json, ServerResponse.class);
        
        assertNotNull(deserialized);
        assertTrue(deserialized.isSuccess());
        assertNotNull(deserialized.getData());
        assertTrue(deserialized.getData() instanceof MusicTrack);
    }

    @Test
    void fromJson_deserializesServerResponseWithMusicTrackList() {
        MusicTrack track1 = new MusicTrack(1, "Track 1", 120, 180.0);
        MusicTrack track2 = new MusicTrack(2, "Track 2", 110, 200.0);
        ServerResponse<List<MusicTrack>> response = ServerResponse.ok("Success", List.of(track1, track2));
        String json = MusicTrackJsonUtil.toJson(response);
        
        ServerResponse<?> deserialized = MusicTrackJsonUtil.fromJson(json, ServerResponse.class);
        
        assertNotNull(deserialized);
        assertTrue(deserialized.isSuccess());
        assertTrue(deserialized.getData() instanceof List);
        assertEquals(2, ((List<?>) deserialized.getData()).size());
    }

    @Test
    void fromJson_deserializesErrorServerResponse() {
        ServerResponse<Object> response = ServerResponse.error("An error occurred");
        String json = MusicTrackJsonUtil.toJson(response);
        
        ServerResponse<?> deserialized = MusicTrackJsonUtil.fromJson(json, ServerResponse.class);
        
        assertNotNull(deserialized);
        assertFalse(deserialized.isSuccess());
        assertEquals("An error occurred", deserialized.getMessage());
    }

    @Test
    void listToJson_convertsListToJson() {
        MusicTrack track1 = new MusicTrack(1, "Track 1", 120, 180.0);
        MusicTrack track2 = new MusicTrack(2, "Track 2", 110, 200.0);
        List<MusicTrack> list = List.of(track1, track2);
        
        String json = MusicTrackJsonUtil.listToJson(list);
        
        assertNotNull(json);
        assertTrue(json.contains("Track 1") || json.contains("songTitle"));
    }

    @Test
    void listFromJson_deserializesListFromJson() {
        MusicTrack track1 = new MusicTrack(1, "Track 1", 120, 180.0);
        MusicTrack track2 = new MusicTrack(2, "Track 2", 110, 200.0);
        List<MusicTrack> original = List.of(track1, track2);
        String json = MusicTrackJsonUtil.listToJson(original);
        
        List<MusicTrack> deserialized = MusicTrackJsonUtil.listFromJson(json, MusicTrack.class);
        
        assertNotNull(deserialized);
        assertEquals(2, deserialized.size());
        assertEquals("Track 1", deserialized.get(0).getSongTitle());
        assertEquals("Track 2", deserialized.get(1).getSongTitle());
    }

    @Test
    void fromJson_deserializesMusicTrackWithBinaryData() {
        byte[] audioBytes = new byte[]{1, 2, 3, 4, 5};
        MusicTrack original = new MusicTrack(1, "Binary Track", 120, 180.0, audioBytes, "file.wav", "audio/wav", 5);
        String json = MusicTrackJsonUtil.toJson(original);
        
        MusicTrack deserialized = MusicTrackJsonUtil.fromJson(json, MusicTrack.class);
        
        assertNotNull(deserialized);
        assertNotNull(deserialized.getAudioFile());
        assertArrayEquals(audioBytes, deserialized.getAudioFile());
        assertEquals("file.wav", deserialized.getFileName());
        assertEquals("audio/wav", deserialized.getContentType());
        assertEquals(5, deserialized.getFileSize());
    }

    @Test
    void fromJson_handlesMusicTrackWithNullBinary() {
        MusicTrack original = new MusicTrack(1, "No Binary", 120, 180.0, null, "", "", 0);
        String json = MusicTrackJsonUtil.toJson(original);
        
        MusicTrack deserialized = MusicTrackJsonUtil.fromJson(json, MusicTrack.class);
        
        assertNotNull(deserialized);
        assertNull(deserialized.getAudioFile());
        assertEquals("No Binary", deserialized.getSongTitle());
    }

    @Test
    void serverResponse_withNullData_deserializesCorrectly() {
        ServerResponse<Object> response = ServerResponse.ok("Success", null);
        String json = MusicTrackJsonUtil.toJson(response);
        
        ServerResponse<?> deserialized = MusicTrackJsonUtil.fromJson(json, ServerResponse.class);
        
        assertNotNull(deserialized);
        assertTrue(deserialized.isSuccess());
        assertNull(deserialized.getData());
    }
}
