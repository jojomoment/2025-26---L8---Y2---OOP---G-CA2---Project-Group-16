package musichub.dao;

import musichub.domain.MusicTrack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class FakeMusicTrackDao implements MusicTrackDao {

    private final List<MusicTrack> tracks = new ArrayList<>();

    public FakeMusicTrackDao() {
      
        tracks.add(new MusicTrack(1, "Track One", 120, 180));
        tracks.add(new MusicTrack(2, "Track Two", 130, 200));
        tracks.add(new MusicTrack(3, "Track Three", 110, 150));

        

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

    @Override
    public int insert(String songTitle, int BPM, double durationInSeconds) {
        int newId = tracks.size() + 1;
        tracks.add(new MusicTrack(newId, songTitle, BPM, durationInSeconds));
        return newId;
    }

    @Override
    public boolean deleteById(int songId) {
        return tracks.removeIf(t -> t.getSongId() == songId);
    }

    @Override
    public MusicTrack updateTrack(int songId, String newTitle, int newBPM, double newDuration) {
        for (MusicTrack t : tracks) {
            if (t.getSongId() == songId) {
                t.setSongTitle(newTitle);
                t.setBPM(newBPM);
                t.setDurationInSeconds(newDuration);
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