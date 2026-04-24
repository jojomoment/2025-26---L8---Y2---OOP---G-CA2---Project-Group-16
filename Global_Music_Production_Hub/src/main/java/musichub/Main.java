package musichub;

import musichub.dao.JdbcMusicTrackDao;
import musichub.dao.MusicTrackDao;
import musichub.domain.MusicTrack;

import java.util.List;
import java.util.Optional;

public class Main
{
    public static void main(String[] args) {
        try {
            MusicTrackDao dao = new JdbcMusicTrackDao();

            createTaskDemo(dao);
            readTaskDemo(dao);
            updateMusicTrackDemo(dao);
            deleteTaskDemo(dao);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTaskDemo(MusicTrackDao dao) {
        try {
            int newId = dao.insert("Write DAO notes example", 120, 180.0,
                                   null, "dao_notes.mp3", "audio/mpeg", 2400000);
            System.out.println("CREATE");
            System.out.println("Inserted task with id: " + newId);
            System.out.println();
        }
        catch (Exception e) {
            System.out.println("CREATE failed");
            e.printStackTrace();
        }
    }

    private static void readTaskDemo(MusicTrackDao dao) {
        try {
            System.out.println("READ");

            Optional<MusicTrack> found = dao.getMusicTrackById(1);
            if (found.isPresent())
                System.out.println("Found by id: " + found.get());
            else
                System.out.println("No task found with id 1");

            List<MusicTrack> allTracks = dao.getAll();
            System.out.println("All tasks:");
            for (MusicTrack track : allTracks)
                System.out.println(track);

            System.out.println();
        }
        catch (Exception e) {
            System.out.println("READ failed");
            e.printStackTrace();
        }
    }

    private static void updateMusicTrackDemo(MusicTrackDao dao) {
        try {
            System.out.println("UPDATE");

            MusicTrack updated = dao.updateTrack(1, "Updated Song Title", 130, 190.0,
                                                 null, "updated.mp3", "audio/mpeg", 2500000);
            System.out.println("Updated track 1: " + updated);

            Optional<MusicTrack> updatedTrack = dao.getMusicTrackById(1);
            if (updatedTrack.isPresent())
                System.out.println("After update: " + updatedTrack.get());

            System.out.println();
        }
        catch (Exception e) {
            System.out.println("UPDATE failed");
            e.printStackTrace();
        }
    }

    private static void deleteTaskDemo(MusicTrackDao dao) {
        try {
            System.out.println("DELETE");

            int tempId = dao.insert("Temporary task for delete demo", 110, 160.0,
                                    null, "temp.mp3", "audio/mpeg", 2000000);
            System.out.println("Inserted temporary task with id: " + tempId);

            boolean deleted = dao.deleteById(tempId);
            System.out.println("Deleted task " + tempId + ": " + deleted);

            Optional<MusicTrack> deletedTrack = dao.getMusicTrackById(tempId);
            if (deletedTrack.isEmpty())
                System.out.println("Confirmed: task no longer exists");

            System.out.println();
        }
        catch (Exception e) {
            System.out.println("DELETE failed");
            e.printStackTrace();
        }
    }
}

