package musichub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MusicTrackTest {

    @Test
    void defaultConstructor_initializesWithDefaults() {
        MusicTrack track = new MusicTrack();
        
        assertEquals(0, track.getSongId());
        assertEquals("", track.getSongTitle());
        assertEquals(1, track.getBpm());
        assertEquals(1.0, track.getDurationInSeconds());
        assertNull(track.getAudioFile());
        assertEquals("", track.getFileName());
        assertEquals("", track.getContentType());
        assertEquals(0, track.getFileSize());
    }

    @Test
    void basicConstructor_initializesMetadataOnly() {
        MusicTrack track = new MusicTrack(1, "Test Track", 120, 180.0);
        
        assertEquals(1, track.getSongId());
        assertEquals("Test Track", track.getSongTitle());
        assertEquals(120, track.getBpm());
        assertEquals(180.0, track.getDurationInSeconds());
        assertNull(track.getAudioFile());
    }

    @Test
    void fullConstructor_initializesAllFields() {
        byte[] audio = new byte[]{1, 2, 3};
        MusicTrack track = new MusicTrack(5, "Full Track", 100, 200.0, audio, "file.wav", "audio/wav", 3);
        
        assertEquals(5, track.getSongId());
        assertEquals("Full Track", track.getSongTitle());
        assertEquals(100, track.getBpm());
        assertEquals(200.0, track.getDurationInSeconds());
        assertArrayEquals(audio, track.getAudioFile());
        assertEquals("file.wav", track.getFileName());
        assertEquals("audio/wav", track.getContentType());
        assertEquals(3, track.getFileSize());
    }

    @Test
    void setSongId_throwsException_whenNegative() {
        MusicTrack track = new MusicTrack(1, "Test", 120, 180.0);
        assertThrows(IllegalArgumentException.class, () -> track.setSongId(-1));
    }

    @Test
    void setSongTitle_throwsException_whenNull() {
        MusicTrack track = new MusicTrack(1, "Test", 120, 180.0);
        assertThrows(IllegalArgumentException.class, () -> track.setSongTitle(null));
    }

    @Test
    void setSongTitle_throwsException_whenBlank() {
        MusicTrack track = new MusicTrack(1, "Test", 120, 180.0);
        assertThrows(IllegalArgumentException.class, () -> track.setSongTitle("   "));
    }

    @Test
    void setBpm_throwsException_whenZero() {
        MusicTrack track = new MusicTrack(1, "Test", 120, 180.0);
        assertThrows(IllegalArgumentException.class, () -> track.setBpm(0));
    }

    @Test
    void setBpm_throwsException_whenNegative() {
        MusicTrack track = new MusicTrack(1, "Test", 120, 180.0);
        assertThrows(IllegalArgumentException.class, () -> track.setBpm(-50));
    }

    @Test
    void setDurationInSeconds_throwsException_whenZero() {
        MusicTrack track = new MusicTrack(1, "Test", 120, 180.0);
        assertThrows(IllegalArgumentException.class, () -> track.setDurationInSeconds(0.0));
    }

    @Test
    void setDurationInSeconds_throwsException_whenNegative() {
        MusicTrack track = new MusicTrack(1, "Test", 120, 180.0);
        assertThrows(IllegalArgumentException.class, () -> track.setDurationInSeconds(-10.5));
    }

    @Test
    void setFileSize_throwsException_whenNegative() {
        MusicTrack track = new MusicTrack(1, "Test", 120, 180.0);
        assertThrows(IllegalArgumentException.class, () -> track.setFileSize(-5));
    }

    @Test
    void hasAudioFile_returnsTrueWhenBytesPresent() {
        byte[] audio = new byte[]{1, 2, 3};
        MusicTrack track = new MusicTrack(1, "Test", 120, 180.0, audio, "file.wav", "audio/wav", 3);
        assertTrue(track.hasAudioFile());
    }

    @Test
    void hasAudioFile_returnsFalseWhenNull() {
        MusicTrack track = new MusicTrack(1, "Test", 120, 180.0, null, "file.wav", "audio/wav", 0);
        assertFalse(track.hasAudioFile());
    }

    @Test
    void hasAudioFile_returnsFalseWhenEmpty() {
        MusicTrack track = new MusicTrack(1, "Test", 120, 180.0, new byte[]{}, "file.wav", "audio/wav", 0);
        assertFalse(track.hasAudioFile());
    }

    @Test
    void hasValidFileMetadata_returnsTrueWhenAllPresent() {
        MusicTrack track = new MusicTrack(1, "Test", 120, 180.0, null, "file.wav", "audio/wav", 100);
        assertTrue(track.hasValidFileMetadata());
    }

    @Test
    void hasValidFileMetadata_returnsFalseWhenFileNameBlank() {
        MusicTrack track = new MusicTrack(1, "Test", 120, 180.0, null, "", "audio/wav", 100);
        assertFalse(track.hasValidFileMetadata());
    }

    @Test
    void hasValidFileMetadata_returnsFalseWhenContentTypeBlank() {
        MusicTrack track = new MusicTrack(1, "Test", 120, 180.0, null, "file.wav", "", 100);
        assertFalse(track.hasValidFileMetadata());
    }

    @Test
    void hasValidFileMetadata_returnsFalseWhenFileSizeZero() {
        MusicTrack track = new MusicTrack(1, "Test", 120, 180.0, null, "file.wav", "audio/wav", 0);
        assertFalse(track.hasValidFileMetadata());
    }

    @Test
    void clearAudioFile_removesAllBinaryFields() {
        byte[] audio = new byte[]{1, 2, 3};
        MusicTrack track = new MusicTrack(1, "Test", 120, 180.0, audio, "file.wav", "audio/wav", 3);
        
        track.clearAudioFile();
        
        assertNull(track.getAudioFile());
        assertEquals("", track.getFileName());
        assertEquals("", track.getContentType());
        assertEquals(0, track.getFileSize());
    }

    @Test
    void equals_returnsTrueForSameSongId() {
        MusicTrack track1 = new MusicTrack(1, "Track A", 120, 180.0);
        MusicTrack track2 = new MusicTrack(1, "Track B", 110, 200.0);
        
        assertEquals(track1, track2);
    }

    @Test
    void equals_returnsFalseForDifferentSongIds() {
        MusicTrack track1 = new MusicTrack(1, "Track A", 120, 180.0);
        MusicTrack track2 = new MusicTrack(2, "Track A", 120, 180.0);
        
        assertNotEquals(track1, track2);
    }

    @Test
    void equals_returnsTrueForSelfReference() {
        MusicTrack track = new MusicTrack(1, "Track A", 120, 180.0);
        assertEquals(track, track);
    }

    @Test
    void equals_returnsFalseForNonMusicTrackObject() {
        MusicTrack track = new MusicTrack(1, "Track A", 120, 180.0);
        assertNotEquals(track, "not a track");
    }

    @Test
    void toString_containsTrackInfo() {
        MusicTrack track = new MusicTrack(1, "Track A", 120, 180.0);
        String str = track.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("Track A") || str.contains("120"));
    }

    @Test
    void hashCode_sameForSameSongId() {
        MusicTrack track1 = new MusicTrack(1, "Track A", 120, 180.0);
        MusicTrack track2 = new MusicTrack(1, "Track B", 110, 200.0);
        
        assertEquals(track1.hashCode(), track2.hashCode());
    }
}
