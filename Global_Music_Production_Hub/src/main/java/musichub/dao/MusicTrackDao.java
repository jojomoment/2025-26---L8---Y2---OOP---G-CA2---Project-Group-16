//java code that interacts with the database table
package musichub.dao;

import musichub.domain.MusicTrack;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


public interface MusicTrackDao { //defines interface, defines methods

    boolean deleteById(int songId);

    MusicTrack updateTrack(int songId, String newTitle, int newBPM, double newDuration,
                           byte[] audioFile, String fileName, String contentType, int fileSize) throws Exception;

    int insert(String songTitle, int BPM, double durationInSeconds,
               byte[] audioFile, String fileName, String contentType, int fileSize) throws Exception; // adding new track to database

    List<MusicTrack> getAll() throws Exception;

    Optional<MusicTrack> getMusicTrackById(int songId) throws Exception;

    // F20 — File Metadata Query (no BLOB fetch)
    Optional<MusicTrack> getMetadataById(int songId) throws Exception;

    List<MusicTrack> findByFilter(Predicate<MusicTrack> filter) throws Exception;
}
