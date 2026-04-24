package musichub.client;

import musichub.domain.MusicTrack;
import musichub.util.MusicTrackJsonUtil;
import musichub.shared.ServerResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class TestClient {

    public static void main(String[] args) {
        try {
            System.out.println("Client starting...");
            Socket socket = new Socket("localhost", 9001);

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            // GET_ALL tracks
            output.println("GET_ALL");
            String getAllResponse = input.readLine();
            ServerResponse<List<MusicTrack>> getAllRespObj =
                    MusicTrackJsonUtil.fromJson(getAllResponse, ServerResponse.class);
            System.out.println("\n🎵 All Tracks:");
            System.out.println(getAllResponse); // raw JSON
            // Optional: iterate and print titles nicely if needed

            // DELETE track with ID 1
            output.println("DELETE:2");
            String deleteResponse = input.readLine();
            System.out.println("\n🗑 Delete Track Response:");
            System.out.println(deleteResponse);

            // 3] UPDATE track with ID 2
            MusicTrack updatedTrack = new MusicTrack(2, "Updated Song", 128, 200.0,
                                                      null, "updated_song.mp3", "audio/mpeg", 4500000);
            String updateJson = MusicTrackJsonUtil.toJson(updatedTrack);
            output.println("UPDATE:" + updateJson);
            String updateResponse = input.readLine();
            System.out.println("\n✏️ Update Track Response:");
            System.out.println(updateResponse);

            // GET_BY_ID track 1
            output.println("GET_BY_ID:1");
            String getByIdResponse = input.readLine();
            System.out.println("\n🔍 Get Track 1:");
            System.out.println(getByIdResponse);

            // INSERT new track (no id)
            MusicTrack newTrack = new MusicTrack(0, "New Inserted Track", 110, 160.0,
                                                  null, "new_track.mp3", "audio/mpeg", 3200000);
            String insertJson = MusicTrackJsonUtil.toJson(newTrack);
            output.println("INSERT:" + insertJson);
            String insertResponse = input.readLine();
            System.out.println("\n➕ Insert Response:");
            System.out.println(insertResponse);

            socket.close();
            System.out.println("\nClient finished demo.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

