package musichub.dao;

import musichub.domain.MusicTrack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;



public class JdbcMusicTrackDao implements MusicTrackDao
{

    private Connection connection;

    public JdbcMusicTrackDao(Connection connection) {
        this.connection = connection;
    }

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
        String sqlQuery = "DELETE FROM music_tracks WHERE song_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery))
        {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        }
        catch (SQLException error)
        {
            error.printStackTrace();
            return false;
        }
    }

    @Override
    public MusicTrack updateTrack(int id, MusicTrack track) {
        return null;
    }
}
