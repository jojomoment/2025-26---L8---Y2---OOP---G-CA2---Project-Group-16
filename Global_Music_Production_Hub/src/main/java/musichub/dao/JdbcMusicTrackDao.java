package musichub.dao;

import musichub.domain.MusicTrack;
import musichub.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;




public class JdbcMusicTrackDao implements MusicTrackDao
{

    private Connection connection;

    public JdbcMusicTrackDao(Connection connection) {
        this.connection = connection;
    }



//    @Override
//    public boolean deleteById(int trackId) {
//        String sqlQuery = "DELETE FROM music_tracks WHERE song_id = ?";
//        try (PreparedStatement statement = connection.prepareStatement(sqlQuery))
//        {
//            statement.setInt(1, trackId);
//            int rowsDeleted = statement.executeUpdate();
//            return rowsDeleted > 0;
//        }
//        catch (SQLException error)
//        {
//            error.printStackTrace();
//            return false;
//        }
//    }



    @Override
    // inserts new data into the table
    public int insert(String songTitle, int BPM, double durationInSeconds) throws Exception
    {
        //making sure invalid data isnt entered
        if (songTitle == null || songTitle.isBlank())
            throw new IllegalArgumentException("songTitle is required");

        //adding row to music tracks table with specified parameters and placeholders
        String sql = "INSERT INTO music_tracks(songTitle, BPM, durationInSeconds) VALUES (?, ?, ?)"; // sql query in a java string


        // closes when finished
        try (Connection c = DatabaseConnection.getConnection();

             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) // sanitises inputs
        {

            // populate ? placeholders
            // numbers ref positon
            ps.setString(1, songTitle.trim());
            ps.setInt(2, BPM);
            ps.setDouble(3, durationInSeconds);


            // excutes and checks if a row was affected or not
            int rows = ps.executeUpdate();
            if (rows != 1)
                throw new IllegalStateException("Insert failed, rows=" + rows);


            // retriviing the key from the the row
            try (ResultSet keys = ps.getGeneratedKeys())
            {
                if (!keys.next())
                    throw new IllegalStateException("No generated key returned");
                return keys.getInt(1);
            }
        }
    }

    @Override
    public List<MusicTrack> getAll() throws Exception
    {
        String sql = "SELECT songId, songTitle, BPM, durationInSeconds FROM music_tracks ORDER BY songId"; //sql query in string java code

        // closes when code finishes
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
        {

            List<MusicTrack> tracks = new ArrayList<>();
            while (rs.next()) tracks.add(mapRow(rs));
            return tracks;
        }
    }

    @Override
    public Optional<MusicTrack> getMusicTrackById(int songId) throws Exception {
        if (songId <= 0)
            return Optional.empty();

        String sql = "SELECT songId, songTitle, BPM, durationInSeconds FROM music_tracks WHERE songId = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, songId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapRow(rs));
            }
        }
    }

    private static MusicTrack mapRow(ResultSet rs) throws Exception
    {
        int id = rs.getInt("songId");
        String title = rs.getString("songTitle");
        int bpm = rs.getInt("BPM");
        double duration = rs.getDouble("durationInSeconds");
        return new MusicTrack(id, title, bpm, duration);
    }
}
