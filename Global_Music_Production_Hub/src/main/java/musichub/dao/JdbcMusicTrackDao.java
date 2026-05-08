package musichub.dao;

import musichub.domain.MusicTrack;
import musichub.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class JdbcMusicTrackDao implements MusicTrackDao {

    public JdbcMusicTrackDao() {
    }

    @Override
    public boolean deleteById(int song_id) {
        String sql = "DELETE FROM music_tracks WHERE song_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement statement = c.prepareStatement(sql)) {

            statement.setInt(1, song_id);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public MusicTrack updateTrack(int song_id, String newTitle, int newBPM, double newDuration) throws SQLException {

        String sql = "UPDATE music_tracks SET song_title = ?, bpm = ?, duration_in_seconds = ? WHERE song_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newTitle);
            ps.setInt(2, newBPM);
            ps.setDouble(3, newDuration);
            ps.setInt(4, song_id);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                return getMusicTrackById(song_id).orElse(null);
            } else {
                return null;
            }
        }
    }

    @Override
    public int insert(String song_title, int bpm, double duration_in_seconds) throws SQLException {

        if (song_title == null || song_title.isBlank())
            throw new IllegalArgumentException("song_title is required");

        String sql = "INSERT INTO music_tracks(song_title, bpm, duration_in_seconds) VALUES (?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, song_title.trim());
            ps.setInt(2, bpm);
            ps.setDouble(3, duration_in_seconds);

            int rows = ps.executeUpdate();
            if (rows != 1)
                throw new IllegalStateException("Insert failed, rows=" + rows);

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next())
                    throw new IllegalStateException("No generated key returned");

                return keys.getInt(1);
            }
        }
    }

    @Override
    public List<MusicTrack> getAll() throws SQLException {

        String sql = "SELECT song_id, song_title, bpm, duration_in_seconds FROM music_tracks";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<MusicTrack> tracks = new ArrayList<>();
            while (rs.next()) {
                tracks.add(mapRow(rs));
            }
            return tracks;
        }
    }

    @Override
    public Optional<MusicTrack> getMusicTrackById(int song_id) throws SQLException {

        if (song_id <= 0)
            return Optional.empty();

        String sql = "SELECT song_id, song_title, bpm, duration_in_seconds FROM music_tracks WHERE song_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, song_id);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next())
                    return Optional.empty();

                return Optional.of(mapRow(rs));
            }
        }
    }

    @Override
    public List<MusicTrack> findByFilter(Predicate<MusicTrack> filter) throws SQLException {

        List<MusicTrack> allTracks = getAll();

        return allTracks.stream()
                .filter(filter)
                .toList();
    }

    private static MusicTrack mapRow(ResultSet rs) throws SQLException {

        int id = rs.getInt("song_id");
        String title = rs.getString("song_title");
        int bpm = rs.getInt("bpm");
        double duration = rs.getDouble("duration_in_seconds");

        return new MusicTrack(id, title, bpm, duration);
    }
}