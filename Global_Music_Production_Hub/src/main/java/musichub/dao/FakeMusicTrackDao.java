package musichub.dao;

import musichub.domain.MusicTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class FakeMusicTrackDao implements MusicTrackDao {

    private final List<MusicTrack> tracks = new ArrayList<>();

    public FakeMusicTrackDao() {
      
        tracks.add(new MusicTrack(1, "Track One", 120, 180, null, "track_one.mp3", "audio/mpeg", 3600000));
        tracks.add(new MusicTrack(2, "Track Two", 130, 200, null, "track_two.wav", "audio/wav", 4200000));
        tracks.add(new MusicTrack(3, "Track Three", 110, 150, null, "track_three.mp3", "audio/mpeg", 2800000));
        

    }

    @Override
    public List<MusicTrack> getAll() {
        return tracks;
    }

    @Override
    public Optional<MusicTrack> getMusicTrackById(int songId) {
        return tracks.stream()
                .filter(t -> t.getSongId() == songId)
                .findFirst();
    }

    // F20 — File Metadata Query (no BLOB fetch)
    @Override
    public Optional<MusicTrack> getMetadataById(int songId) {
        return tracks.stream()
                .filter(t -> t.getSongId() == songId)
                .map(t -> new MusicTrack(t.getSongId(), t.getSongTitle(), t.getBPM(),
                        t.getDurationInSeconds(), null, t.getFileName(),
                        t.getContentType(), t.getFileSize()))
                .findFirst();
    }

    @Override
    public int insert(String songTitle, int BPM, double durationInSeconds,
                      byte[] audioFile, String fileName, String contentType, int fileSize) {
        int newId = tracks.size() + 1;
        tracks.add(new MusicTrack(newId, songTitle, BPM, durationInSeconds,
                                  audioFile, fileName, contentType, fileSize));
        return newId;
    }

    @Override
    public boolean deleteById(int songId) {
        return tracks.removeIf(t -> t.getSongId() == songId);
    }

    @Override
    public MusicTrack updateTrack(int songId, String newTitle, int newBPM, double newDuration,
                                  byte[] audioFile, String fileName, String contentType, int fileSize) {
        for (MusicTrack t : tracks) {
            if (t.getSongId() == songId) {
                t.setSongTitle(newTitle);
                t.setBPM(newBPM);
                t.setDurationInSeconds(newDuration);
                t.setAudioFile(audioFile);
                t.setFileName(fileName);
                t.setContentType(contentType);
                t.setFileSize(fileSize);
                return t;
            }
        }
        return null;
    }

    @Override
    public List<MusicTrack> findByFilter(Predicate<MusicTrack> filter) {
        return tracks.stream().filter(filter).toList();
    }
}

