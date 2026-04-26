package musichub.client;

import musichub.domain.MusicTrack;
import musichub.util.MusicTrackJsonUtil;
import musichub.shared.ServerResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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


            // F18 Binary File Upload
            try {
                System.out.println("\n📤 Uploading file (F18)...");

                // 1. Read file from disk
                Path path = Paths.get("src/test.mp3");
                byte[] fileBytes = Files.readAllBytes(path);

                // 2. Extract metadata
                String fileName = path.getFileName().toString();
                String contentType = Files.probeContentType(path);
                int fileSize = fileBytes.length;

                // 3. Encode to Base64
                String base64File = Base64.getEncoder().encodeToString(fileBytes);

                // 4. Build request
                Map<String, Object> uploadRequest = new HashMap<>();
                uploadRequest.put("type", "UPLOAD_FILE");
                uploadRequest.put("songTitle", "Test Upload");
                uploadRequest.put("bpm", 120);
                uploadRequest.put("durationInSeconds", 180.0);
                uploadRequest.put("fileName", fileName);
                uploadRequest.put("contentType", contentType);
                uploadRequest.put("fileSize", fileSize);
                uploadRequest.put("fileData", base64File);

                // 5. Send request
                String json = MusicTrackJsonUtil.toJson(uploadRequest);
                output.println("UPLOAD:" + json);

                // 6. Read response
                String uploadResponse = input.readLine();
                System.out.println("\n📥 Upload Response:");
                System.out.println(uploadResponse);

            } catch (Exception e) {
                e.printStackTrace();
            }





            socket.close();
            System.out.println("\nClient finished demo.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }









}

