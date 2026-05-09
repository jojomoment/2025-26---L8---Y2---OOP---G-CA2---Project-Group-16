//java code that interacts with the database table
package musichub.dao;

import musichub.domain.MusicTrack;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


public interface MusicTrackDao { //defines interface, defines methods

    boolean deleteById(int songId);

    MusicTrack updateMusicTrack(int songId, MusicTrack entity) throws Exception;

    MusicTrack insert(MusicTrack track) throws Exception; // adding new track to database

    List<MusicTrack> getAll() throws Exception;

    Optional<MusicTrack> getMusicTrackById(int songId) throws Exception;


    List<MusicTrack> findByFilter(Predicate<MusicTrack> filter) throws Exception;

    // Binary file operations
    MusicTrack insertBinary(MusicTrack track) throws Exception;

    Optional<MusicTrack> getMusicTrackWithBinaryById(int songId) throws Exception;

    Optional<MusicTrack> getMusicTrackMetadataById(int songId) throws Exception;
}