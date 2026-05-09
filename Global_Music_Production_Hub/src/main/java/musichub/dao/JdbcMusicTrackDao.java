package musichub.dao;

import musichub.domain.MusicTrack;
import musichub.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class JdbcMusicTrackDao implements MusicTrackDao {

    private static final String[] NEW_BINARY_COLUMNS = {"audio_file", "file_name", "content_type", "file_size"};
    private static final String[] LEGACY_BINARY_COLUMNS = {"audioFile", "fileName", "contentType", "fileSize"};

    public JdbcMusicTrackDao() {
    }

    @Override
    public boolean deleteById(int song_id) {
        String sql = "DELETE FROM music_tracks WHERE songId = ?";
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
    public MusicTrack updateMusicTrack(int song_id, MusicTrack entity) throws SQLException {

        String sql = "UPDATE music_tracks SET songTitle = ?, BPM = ?, durationInSeconds = ? WHERE songId = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, entity.getSongTitle());
            ps.setInt(2, entity.getBpm());
            ps.setDouble(3, entity.getDurationInSeconds());
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
    public MusicTrack insert(MusicTrack track) throws SQLException {

        if (track.getSongTitle() == null || track.getSongTitle().isBlank())
            throw new IllegalArgumentException("song_title is required");

        String sql = "INSERT INTO music_tracks(songTitle, BPM, durationInSeconds) VALUES (?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, track.getSongTitle().trim());
            ps.setInt(2, track.getBpm());
            ps.setDouble(3, track.getDurationInSeconds());

            int rows = ps.executeUpdate();
            if (rows != 1)
                throw new IllegalStateException("Insert failed, rows=" + rows);

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next())
                    throw new IllegalStateException("No generated key returned");

                int generatedId = keys.getInt(1);
                track.setSongId(generatedId);
                return track;
            }
        }
    }

    @Override
    public List<MusicTrack> getAll() throws SQLException {
        try {
            return selectAllWithColumns(NEW_BINARY_COLUMNS);
        } catch (SQLException e) {
            if (isUnknownColumnError(e)) {
                return selectAllWithColumns(LEGACY_BINARY_COLUMNS);
            }
            throw e;
        }
    }

    @Override
    public Optional<MusicTrack> getMusicTrackById(int song_id) throws SQLException {

        if (song_id <= 0)
            return Optional.empty();

        try {
            return selectByIdWithColumns(NEW_BINARY_COLUMNS, song_id, false);
        } catch (SQLException e) {
            if (isUnknownColumnError(e)) {
                return selectByIdWithColumns(LEGACY_BINARY_COLUMNS, song_id, false);
            }
            throw e;
        }
    }

    @Override
    public List<MusicTrack> findByFilter(Predicate<MusicTrack> filter) throws SQLException {

        List<MusicTrack> allTracks = getAll();

        return allTracks.stream()
                .filter(filter)
                .toList();
    }

    private List<MusicTrack> selectAllWithColumns(String[] columns) throws SQLException {
        String sql = String.format("SELECT songId as song_id, songTitle as song_title, BPM as bpm, durationInSeconds as duration_in_seconds, %s FROM music_tracks", String.join(", ", columns));

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<MusicTrack> tracks = new ArrayList<>();
            while (rs.next()) {
                tracks.add(mapRow(rs, columns));
            }
            return tracks;
        }
    }

    private Optional<MusicTrack> selectByIdWithColumns(String[] columns, int songId, boolean metadataOnly) throws SQLException {
        String sql = String.format("SELECT songId as song_id, songTitle as song_title, BPM as bpm, durationInSeconds as duration_in_seconds, %s FROM music_tracks WHERE songId = ?", String.join(", ", columns));

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, songId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    return Optional.empty();

                return Optional.of(metadataOnly ? mapMetadataRow(rs, columns) : mapRow(rs, columns));
            }
        }
    }

    private static MusicTrack mapRow(ResultSet rs, String[] columns) throws SQLException {
        int id = rs.getInt("song_id");
        String title = rs.getString("song_title");
        int bpm = rs.getInt("bpm");
        double duration = rs.getDouble("duration_in_seconds");

        byte[] audioFile;
        try {
            audioFile = rs.getBytes(columns[0]);
        } catch (SQLException e) {
            audioFile = null;
        }

        String fileName = rs.getString(columns[1]);
        if (fileName == null) {
            fileName = "";
        }

        String contentType = rs.getString(columns[2]);
        if (contentType == null) {
            contentType = "";
        }

        int fileSize = rs.getInt(columns[3]);

        return new MusicTrack(id, title, bpm, duration, audioFile, fileName, contentType, fileSize);
    }

    private static MusicTrack mapMetadataRow(ResultSet rs, String[] columns) throws SQLException {
        int id = rs.getInt("song_id");
        String title = rs.getString("song_title");
        int bpm = rs.getInt("bpm");
        double duration = rs.getDouble("duration_in_seconds");

        String fileName = rs.getString(columns[1]);
        if (fileName == null) {
            fileName = "";
        }

        String contentType = rs.getString(columns[2]);
        if (contentType == null) {
            contentType = "";
        }

        int fileSize = rs.getInt(columns[3]);

        return new MusicTrack(id, title, bpm, duration, null, fileName, contentType, fileSize);
    }

    private static boolean isUnknownColumnError(SQLException e) {
        return e.getMessage().contains("Unknown column") || (e.getSQLState() != null && e.getSQLState().startsWith("42"));
    }

    @Override
    public MusicTrack insertBinary(MusicTrack track) throws SQLException {
        if (track.getSongTitle() == null || track.getSongTitle().isBlank())
            throw new IllegalArgumentException("song_title is required");

        try {
            int id = insertBinaryWithColumns(track.getSongTitle(), track.getBpm(), track.getDurationInSeconds(), track.getAudioFile(), track.getFileName(), track.getContentType(), track.getFileSize(), NEW_BINARY_COLUMNS);
            track.setSongId(id);
            return track;
        } catch (SQLException e) {
            if (isUnknownColumnError(e)) {
                int id = insertBinaryWithColumns(track.getSongTitle(), track.getBpm(), track.getDurationInSeconds(), track.getAudioFile(), track.getFileName(), track.getContentType(), track.getFileSize(), LEGACY_BINARY_COLUMNS);
                track.setSongId(id);
                return track;
            }
            throw e;
        }
    }

    private int insertBinaryWithColumns(String songTitle, int bpm, double durationInSeconds, byte[] audioFile, String fileName, String contentType, int fileSize, String[] columns) throws SQLException {
        String sql = String.format("INSERT INTO music_tracks(songTitle, BPM, durationInSeconds, %s) VALUES (?, ?, ?, ?, ?, ?, ?)", String.join(", ", columns));

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, songTitle.trim());
            ps.setInt(2, bpm);
            ps.setDouble(3, durationInSeconds);
            ps.setBytes(4, audioFile);
            ps.setString(5, fileName);
            ps.setString(6, contentType);
            ps.setInt(7, fileSize);

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
    public Optional<MusicTrack> getMusicTrackWithBinaryById(int songId) throws SQLException {
        if (songId <= 0)
            return Optional.empty();

        try {
            return selectByIdWithColumns(NEW_BINARY_COLUMNS, songId, false);
        } catch (SQLException e) {
            if (isUnknownColumnError(e)) {
                return selectByIdWithColumns(LEGACY_BINARY_COLUMNS, songId, false);
            }
            throw e;
        }
    }

    @Override
    public Optional<MusicTrack> getMusicTrackMetadataById(int songId) throws SQLException {
        if (songId <= 0)
            return Optional.empty();

        try {
            return selectByIdWithColumns(NEW_BINARY_COLUMNS, songId, true);
        } catch (SQLException e) {
            if (isUnknownColumnError(e)) {
                return selectByIdWithColumns(LEGACY_BINARY_COLUMNS, songId, true);
            }
            throw e;
        }
    }
}