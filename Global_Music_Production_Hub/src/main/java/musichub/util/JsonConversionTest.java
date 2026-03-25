package musichub.util;

import musichub.domain.MusicTrack;
import java.util.List;
import java.util.ArrayList;

public class JsonConversionTest {
    public static void main(String[] args) {
        // Single object test
        MusicTrack track = new MusicTrack(1, "Song A", 120, 180);
        String json = MusicTrackJsonUtil.toJson(track);
        MusicTrack trackBack = MusicTrackJsonUtil.fromJson(json, MusicTrack.class);

        System.out.println("Original: " + track);
        System.out.println("JSON: " + json);
        System.out.println("Round-trip: " + trackBack);

        // List test
        List<MusicTrack> tracks = new ArrayList<>();
        tracks.add(track);
        tracks.add(new MusicTrack(2, "Song B", 130, 200));

        String listJson = MusicTrackJsonUtil.listToJson(tracks);
        List<MusicTrack> tracksBack = MusicTrackJsonUtil.listFromJson(listJson, MusicTrack.class);

        System.out.println("Original list: " + tracks);
        System.out.println("JSON list: " + listJson);
        System.out.println("Round-trip list: " + tracksBack);
    }
}