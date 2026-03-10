package musichub.dao;

import musichub.domain.MusicTrack;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MusicTrackDao
{
    private Connection connection;

    public MusicTrackDao(Connection connection)
    {
        this.connection = connection;
    }

    public boolean deleteMusicTrackById(int trackId)
    {
        String sqlQuery = "DELETE FROM music_tracks WHERE song_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, trackId);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException error) {
            error.printStackTrace();
            return false;
        }
    }
}