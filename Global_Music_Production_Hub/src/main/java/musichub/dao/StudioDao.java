package musichub.dao;


import musichub.domain.Studio;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public interface StudioDao
{
    // try this out int insert(Studio studio)
    int insert(int studio_id, String location_name, int  room_capacity, double hourly_rate) throws SQLException; // adding new track to database

    List<Studio> getAll() throws SQLException;

    Optional<Studio> getStudiById(int studio_id) throws SQLException;
}
