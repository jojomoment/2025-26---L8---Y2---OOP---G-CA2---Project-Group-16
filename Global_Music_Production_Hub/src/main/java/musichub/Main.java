package musichub;

import musichub.dao.JdbcMusicTrackDao;
import musichub.dao.MusicTrackDao;
import musichub.domain.MusicTrack;

import java.util.List;
import java.util.Optional;

public class Main
{
    public static void main(String[] args) {
        MusicTrackDao dao = new JdbcMusicTrackDao();

        createTaskDemo(dao);
        readTaskDemo(dao);
        updateMusicTrackDemo(dao);
        deleteTaskDemo(dao);
    }

    private static void createTaskDemo(MusicTrackDao dao) {
        try {
            int newId = dao.insert("Write DAO notes example", "TODO");
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
//        try {
//            System.out.println("UPDATE");
//
////            boolean updated = dao.updateStatus(1, "DONE");
////            System.out.println("Updated task 1: " + updated);
//
//            Optional<Task> updatedTask = dao.findById(1);
//            if (updatedTask.isPresent())
//                System.out.println("After update: " + updatedTask.get());
//
//            System.out.println();
//        }
//        catch (Exception e) {
//            System.out.println("UPDATE failed");
//            e.printStackTrace();
//        }
    }

    private static void deleteTaskDemo(MusicTrackDao dao) {
//        try {
//            System.out.println("DELETE");
//
//            int tempId = dao.insert("Temporary task for delete demo", "TODO");
//            System.out.println("Inserted temporary task with id: " + tempId);
//
//            boolean deleted = dao.deleteById(tempId);
//            System.out.println("Deleted task " + tempId + ": " + deleted);
//
//            Optional<Task> deletedTask = dao.findById(tempId);
//            if (deletedTask.isEmpty())
//                System.out.println("Confirmed: task no longer exists");
//
//            System.out.println();
//        }
//        catch (Exception e) {
//            System.out.println("DELETE failed");
//            e.printStackTrace();
//        }
    }
}
