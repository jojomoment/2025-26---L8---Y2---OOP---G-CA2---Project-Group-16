//java code that interacts with the database table
package musichub.dao;

import musichub.domain.MusicTrack;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


public interface MusicTrackDao { //defines interface, defines methods

    boolean deleteById(int song_id);

    MusicTrack updateTrack(int song_id, String newTitle, int newBPM, double newDuration) throws Exception;

    int insert(String song_title, int BPM, double duration_in_seconds) throws Exception; // adding new track to database

    List<MusicTrack> getAll() throws Exception;

    Optional<MusicTrack> getMusicTrackById(int song_id) throws Exception;


    List<MusicTrack> findByFilter(Predicate<MusicTrack> filter) throws Exception;
}