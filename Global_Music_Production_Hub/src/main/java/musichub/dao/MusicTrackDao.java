//java code that interacts with the database table
package musichub.dao;

import musichub.domain.MusicTrack;
import java.util.List;
import java.util.Optional;



public interface MusicTrackDao { //defines interface, defines methods

    MusicTrack insert(MusicTrack track); // adding new track to database

    Optional<MusicTrack> findById(int id); // finds track by id, optional used instead of null, avoids crashing, can be used to highlight errors

    List<MusicTrack> findAll(); // lists all the musicTrack objects

    boolean deleteById(int id); //deletes MusicTrack opbject and returns true if it actually got deleted

    MusicTrack updateTrack(int id, MusicTrack track);//update/ replaces existing  track data with new data
}