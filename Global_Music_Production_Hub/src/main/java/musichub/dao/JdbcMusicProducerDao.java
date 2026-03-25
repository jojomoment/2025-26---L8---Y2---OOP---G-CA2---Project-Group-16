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
    public int insert(int producer_id, String stage_name, int tracks_uploaded, double average_rating) throws SQLException

    { //making sure invalid data isnt entered
        if (producer_id <=0)
            throw new IllegalArgumentException("producer id   is required");


        if (stage_name == null || stage_name.isBlank())
            throw new IllegalArgumentException("stage name  is required");

        if (tracks_uploaded <=0)
            throw new IllegalArgumentException("uplodaded tracks   required");

        if (average_rating <=0)
            throw new IllegalArgumentException("average rating  required");

        //adding row to music tracks table with specified parameters and placeholders
        String sql = "INSERT INTO music_producers(producer_id, stage_name, tracks_uploaded,average_rating) VALUES (?, ?, ?,?)"; // sql query in a java string


        // closes when finished
        try (Connection c = DatabaseConnection.getConnection();

             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) // sanitises inputs
        {

            // populate ? placeholders
            // numbers ref positon
            ps.setInt(1, producer_id);
            ps.setString(2, stage_name);
            ps.setInt(3, tracks_uploaded);
            ps.setDouble(4, average_rating);




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
    public List<MusicProducer> getAll() throws SQLException
    {  String sql = "SELECT producer_id, stage_name, tracks_uploaded, average_rating FROM music_producers WHERE producer_id ";//sql query in string java code

        // closes when code finishes
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
        {

            List<MusicProducer> producers  = new ArrayList<>();
            while (rs.next()) producers.add(mapRow(rs));
            return producers;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<MusicProducer> getMusicProducerById(int producer_id) throws SQLException
    {
        return Optional.empty();
    }

    private static MusicProducer mapRow(ResultSet rs) throws Exception
    {
        int producer_id = rs.getInt("producer_id");
        String stage_name = rs.getString("stage_name");
        int tracks_uploaded = rs.getInt("tracks_uploaded");
        double average_rating = rs.getDouble("average_rating");
        return new MusicProducer(producer_id, stage_name, tracks_uploaded, average_rating);
    }
}
