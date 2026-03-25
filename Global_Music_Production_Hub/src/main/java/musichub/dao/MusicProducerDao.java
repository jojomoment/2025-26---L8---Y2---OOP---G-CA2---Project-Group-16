package musichub.dao;

import musichub.domain.MusicProducer;


import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


public interface MusicProducerDao
{

    int insert(int producer_id, String stage_name, int tracks_uploaded,  double average_rating) throws SQLException; // adding new track to database

    List<MusicProducer> getAll() throws SQLException;

    Optional<MusicProducer> getMusicProducerById(int producer_id) throws SQLException;
}
