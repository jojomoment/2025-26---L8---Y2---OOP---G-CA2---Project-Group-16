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
    public MusicTrack insert(MusicTrack track) {
        int newId = tracks.size() + 1;
        track.setSongId(newId);
        tracks.add(track);
        return track;
    }

    @Override
    public boolean deleteById(int songId) {
        return tracks.removeIf(t -> t.getSongId() == songId);
    }

    @Override
    public MusicTrack updateMusicTrack(int songId, MusicTrack entity) {
        for (MusicTrack t : tracks) {
            if (t.getSongId() == songId) {
                t.setSongTitle(entity.getSongTitle());
                t.setBpm(entity.getBpm());
                t.setDurationInSeconds(entity.getDurationInSeconds());
                return t;
            }
        }
        return null;
    }

    @Override
    public List<MusicTrack> findByFilter(Predicate<MusicTrack> filter) {
        return tracks.stream().filter(filter).toList();
    }

    @Override
    public MusicTrack insertBinary(MusicTrack track) throws Exception {
        int newId = tracks.size() + 1;
        track.setSongId(newId);
        tracks.add(track);
        return track;
    }

    @Override
    public Optional<MusicTrack> getMusicTrackWithBinaryById(int songId) throws Exception {
        return tracks.stream()
                .filter(t -> t.getSongId() == songId)
                .findFirst();
    }

    @Override
    public Optional<MusicTrack> getMusicTrackMetadataById(int songId) throws Exception {
        return tracks.stream()
                .filter(t -> t.getSongId() == songId)
                .map(t -> {
                    MusicTrack meta = new MusicTrack(t.getSongId(), t.getSongTitle(), t.getBpm(), t.getDurationInSeconds());
                    meta.setFileName(t.getFileName());
                    meta.setContentType(t.getContentType());
                    meta.setFileSize(t.getFileSize());
                    return meta;
                })
                .findFirst();
    }
}