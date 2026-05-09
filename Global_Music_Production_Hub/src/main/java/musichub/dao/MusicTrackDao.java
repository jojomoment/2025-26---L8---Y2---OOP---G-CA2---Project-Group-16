//java code that interacts with the database table
package musichub.dao;

import musichub.domain.MusicTrack;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


public interface MusicTrackDao { //defines interface, defines methods

    boolean deleteById(int songId);

    MusicTrack updateTrack(int songId, String newTitle, int newBPM, double newDuration) throws Exception;

    int insert(String songTitle, int BPM, double durationInSeconds) throws Exception; // adding new track to database

    List<MusicTrack> getAll() throws Exception;

    Optional<MusicTrack> getMusicTrackById(int songId) throws Exception;


    List<MusicTrack> findByFilter(Predicate<MusicTrack> filter) throws Exception;

    // Binary file operations
    int insertBinary(String songTitle, int BPM, double durationInSeconds, byte[] audioFile, String fileName, String contentType, int fileSize) throws Exception;

    Optional<MusicTrack> getMusicTrackWithBinaryById(int songId) throws Exception;

    Optional<MusicTrack> getMusicTrackMetadataById(int songId) throws Exception;
}