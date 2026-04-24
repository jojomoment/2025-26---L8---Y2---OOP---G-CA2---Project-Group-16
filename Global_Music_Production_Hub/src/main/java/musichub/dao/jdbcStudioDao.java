package musichub.dao;

import musichub.db.DatabaseConnection;
import musichub.domain.Studio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class jdbcStudioDao implements StudioDao
{

    public jdbcStudioDao()
    {
        // this.connection = connection; // removed invalid assignment
    }


    @Override
    public int insert(int studio_id, String location_name, int room_capacity, double hourly_rate) throws SQLException
    {

        //making sure invalid data isnt entered
        if (studio_id <=0)
            throw new IllegalArgumentException("studio id required");


        if (location_name == null || location_name.isBlank())
            throw new IllegalArgumentException("location name required");

        if (room_capacity <=0)
            throw new IllegalArgumentException("room capacity required");

        if (hourly_rate <=0)
            throw new IllegalArgumentException("hourly rate required");


        //adding row to music tracks table with specified parameters and placeholders
        String sql = "INSERT INTO studios(studio_id, location_name, room_capacity,hourly_rate) VALUES (?, ?, ?,?)"; // sql query in a java string


        // closes when finished
        try (Connection c = DatabaseConnection.getConnection();

             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) // sanitises inputs
        {

            // populate ? placeholders
            // numbers ref positon
            ps.setInt(1, studio_id);
            ps.setString(2, location_name);
            ps.setInt(3, room_capacity);
            ps.setDouble(4, hourly_rate);


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
    public List<Studio> getAll() throws SQLException
    {
        String sql = "SELECT studio_id, location_name, room_capacity,hourly_rate FROM studios";//sql query in string java code

        // closes when code finishes
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
        {

            List<Studio> studios = new ArrayList<>();
            while (rs.next()) studios.add(mapRow(rs));
            return studios;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Studio> getStudioById(int studio_id) throws SQLException
    {
        return Optional.empty();
    }

    private static Studio mapRow(ResultSet rs) throws Exception
    {
        int studio_id = rs.getInt("studio_id");
        String location_name = rs.getString("location_name");
        int room_capacity = rs.getInt("room_capacity");
        double hourly_rate = rs.getDouble("hourly_rate");
        return new Studio(studio_id, location_name, room_capacity, hourly_rate);
    }
}
