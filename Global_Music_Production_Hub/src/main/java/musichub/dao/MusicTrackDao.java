//java code that interacts with the database table
package musichub.dao;

import musichub.domain.MusicTrack;

import java.util.List;
import java.util.Optional;



public interface MusicTrackDao { //defines interface, defines methods

    int insert(String songTitle, int BPM, double durationInSeconds) throws Exception; // adding new track to database

    List<MusicTrack> getAll() throws Exception;

    Optional<MusicTrack> getMusicTrackById(int songId) throws Exception;

    }