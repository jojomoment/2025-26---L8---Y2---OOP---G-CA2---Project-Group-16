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
import java.util.function.Predicate;




public class JdbcMusicTrackDao implements MusicTrackDao {

    public JdbcMusicTrackDao() {
    }


    @Override
    public boolean deleteById(int songId) {
        String sql = "DELETE FROM music_tracks WHERE songId = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement statement = c.prepareStatement(sql)) {

            statement.setInt(1, songId);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public MusicTrack updateTrack(int songId, String newTitle, int newBPM, double newDuration,
                                  byte[] audioFile, String fileName, String contentType, int fileSize) throws Exception {
        String sql = "UPDATE music_tracks SET songTitle = ?, BPM = ?, durationInSeconds = ?, " +
                     "audioFile = ?, file_name = ?, content_type = ?, file_size = ? WHERE songId = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql))
        {

            ps.setString(1, newTitle);
            ps.setInt(2, newBPM);
            ps.setDouble(3, newDuration);
            ps.setBytes(4, audioFile);
            ps.setString(5, fileName);
            ps.setString(6, contentType);
            ps.setInt(7, fileSize);
            ps.setInt(8, songId);

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0)
            {
                return new MusicTrack(songId, newTitle, newBPM, newDuration,
                                      audioFile, fileName, contentType, fileSize);
            } else
            {
                return null;
            }
        }
    }

    @Override
    // inserts new data into the table
    public int insert(String songTitle, int BPM, double durationInSeconds,
                      byte[] audioFile, String fileName, String contentType, int fileSize) throws Exception
    {
        //making sure invalid data isnt entered
        if (songTitle == null || songTitle.isBlank())
            throw new IllegalArgumentException("songTitle is required");

        //adding row to music tracks table with specified parameters and placeholders
        String sql = "INSERT INTO music_tracks(songTitle, BPM, durationInSeconds, audioFile, file_name, content_type, file_size) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)"; // sql query in a java string


        // closes when finished
        try (Connection c = DatabaseConnection.getConnection();

             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) // sanitises inputs
        {

            // populate ? placeholders
            // numbers ref positon
            ps.setString(1, songTitle.trim());
            ps.setInt(2, BPM);
            ps.setDouble(3, durationInSeconds);
            ps.setBytes(4, audioFile);
            ps.setString(5, fileName);
            ps.setString(6, contentType);
            ps.setInt(7, fileSize);


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
        String sql = "SELECT songId, songTitle, BPM, durationInSeconds, audioFile, file_name, content_type, file_size " +
                     "FROM music_tracks ORDER BY songId"; //sql query in string java code

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

        String sql = "SELECT songId, songTitle, BPM, durationInSeconds, audioFile, file_name, content_type, file_size " +
                     "FROM music_tracks WHERE songId = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, songId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapRow(rs));
            }
        }
    }

    
    @Override
public List<MusicTrack> findByFilter(Predicate<MusicTrack> filter) throws Exception {
    List<MusicTrack> allTracks = getAll(); // fetch everything from the Database
    return allTracks.stream()
                    .filter(filter)  // apply the predicate
                    .toList();       // return the filtered list
}


    private static MusicTrack mapRow(ResultSet rs) throws Exception
    {
        int id = rs.getInt("songId");
        String title = rs.getString("songTitle");
        int bpm = rs.getInt("BPM");
        double duration = rs.getDouble("durationInSeconds");
        byte[] audioFile = rs.getBytes("audioFile");
        String fileName = rs.getString("file_name");
        String contentType = rs.getString("content_type");
        int fileSize = rs.getInt("file_size");
        return new MusicTrack(id, title, bpm, duration, audioFile, fileName, contentType, fileSize);
    }
}

