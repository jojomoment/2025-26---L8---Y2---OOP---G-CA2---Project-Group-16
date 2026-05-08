package musichub.dao;

import musichub.db.DatabaseConnection;
import musichub.domain.MusicProducer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMusicProducerDao implements MusicProducerDao
{




    @Override
    public int insert(String stage_name, int tracks_uploaded, double average_rating) throws SQLException {

        if (stage_name == null || stage_name.isBlank())
            throw new IllegalArgumentException("stage name required");

        if (tracks_uploaded < 0)
            throw new IllegalArgumentException("tracks uploaded required: tracks uploaded must be >= 0");

        if (average_rating < 0 || average_rating > 5)
            throw new IllegalArgumentException("invalid rating: must be between 0 and 5");

        String sql = "INSERT INTO music_producers(stage_name, tracks_uploaded, average_rating) VALUES (?, ?, ?)";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, stage_name.trim());
            ps.setInt(2, tracks_uploaded);
            ps.setDouble(3, average_rating);

            int rows = ps.executeUpdate();
            if (rows != 1)
                throw new IllegalStateException("Insert failed");

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next())
                    throw new IllegalStateException("No generated key");

                return keys.getInt(1);
            }
        }
    }



    @Override
    public List<MusicProducer> getAll() throws SQLException {

        String sql = "SELECT producer_id, stage_name, tracks_uploaded, average_rating FROM music_producers";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<MusicProducer> producers = new ArrayList<>();

            while (rs.next()) {
                producers.add(mapRow(rs));
            }

            return producers;
        }
    }

    @Override
    public Optional<MusicProducer> getMusicProducerById(int producer_id) throws SQLException {

        String sql = "SELECT producer_id, stage_name, tracks_uploaded, average_rating FROM music_producers WHERE producer_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, producer_id);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next())
                    return Optional.empty();

                return Optional.of(mapRow(rs));
            }
        }
    }

    @Override
    public boolean deleteById(int producer_id) throws SQLException {

        String sql = "DELETE FROM music_producers WHERE producer_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, producer_id);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public MusicProducer updateProducer(int producer_id, String stage_name, int tracks_uploaded, double average_rating) throws SQLException {

        String sql = "UPDATE music_producers SET stage_name = ?, tracks_uploaded = ?, average_rating = ? WHERE producer_id = ?";

        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, stage_name);
            ps.setInt(2, tracks_uploaded);
            ps.setDouble(3, average_rating);
            ps.setInt(4, producer_id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                return getMusicProducerById(producer_id).orElse(null);
            }

            return null;
        }
    }

    private static MusicProducer mapRow(ResultSet rs) throws SQLException
    {
        int producer_id = rs.getInt("producer_id");
        String stage_name = rs.getString("stage_name");
        int tracks_uploaded = rs.getInt("tracks_uploaded");
        double average_rating = rs.getDouble("average_rating");

        return new MusicProducer(producer_id, stage_name, tracks_uploaded, average_rating);
    }
}
