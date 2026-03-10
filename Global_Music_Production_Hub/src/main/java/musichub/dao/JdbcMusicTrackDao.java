package musichub.dao;

import musichub.domain.MusicTrack;

import java.util.List;
import java.util.Optional;


public class JdbcMusicTrackDao implements MusicTrackDao
{

    @Override
    public MusicTrack insert(MusicTrack track)
    {
        return null;
    }

    @Override
    public Optional<MusicTrack> findById(int id) {
        return Optional.empty();
    }

    @Override
    public List<MusicTrack> findAll() {
        return List.of();
    }

    @Override
    public boolean deleteById(int id) {
        return false;
    }

    @Override
    public MusicTrack updateTrack(int id, MusicTrack track) {
        return null;
    }
}