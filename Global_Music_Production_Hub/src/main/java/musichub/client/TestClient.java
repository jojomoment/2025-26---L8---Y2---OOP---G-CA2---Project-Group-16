package musichub.client;

import musichub.domain.MusicTrack;
import musichub.util.MusicTrackJsonUtil;
import musichub.shared.ServerResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class TestClient {

    public static void main(String[] args) {
        System.out.println("Client starting...");
        try (Socket socket = new Socket("localhost", 9001);
             BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

            sendRequest(output, input, "GET_ALL", "\n🎵 All Tracks:");

            File demoFile = createDemoBinaryFile();
            MusicTrack uploadTrack = new MusicTrack(0, "Upload Demo Track", 130, 180.0);
            int uploadedId = uploadBinaryFile(output, input, uploadTrack, demoFile);

            if (uploadedId > 0) {
                retrieveBinaryFile(output, input, uploadedId);
                requestMetadata(output, input, uploadedId);
            }

            output.println("DISCONNECT");
            String disconnectResponse = input.readLine();
            System.out.println("\n✅ Disconnected cleanly:");
            System.out.println(disconnectResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendRequest(PrintWriter output, BufferedReader input, String request, String label) throws IOException {
        output.println(request);
        String response = input.readLine();
        System.out.println(label);
        System.out.println(response);
    }

    private static int uploadBinaryFile(PrintWriter output, BufferedReader input, MusicTrack track, File file) throws IOException {
        byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
        track.setAudioFile(fileBytes);
        track.setFileName(file.getName());
        track.setContentType("audio/wav");
        track.setFileSize(fileBytes.length);

        String uploadJson = MusicTrackJsonUtil.toJson(track);
        output.println("UPLOAD_BINARY:" + uploadJson);

        String response = input.readLine();
        System.out.println("\n📤 Upload Response:");
        System.out.println(response);

        ServerResponse<?> uploadResp = MusicTrackJsonUtil.fromJson(response, ServerResponse.class);
        if (uploadResp != null && uploadResp.getData() instanceof MusicTrack uploadedTrack) {
            System.out.println("Uploaded record ID: " + uploadedTrack.getSongId());
            return uploadedTrack.getSongId();
        }
        return -1;
    }

    private static void retrieveBinaryFile(PrintWriter output, BufferedReader input, int id) throws IOException {
        output.println("RETRIEVE_BINARY:" + id);
        String response = input.readLine();
        System.out.println("\n📥 Retrieve Response:");
        System.out.println(response);

        ServerResponse<?> retrieveResp = MusicTrackJsonUtil.fromJson(response, ServerResponse.class);
        if (retrieveResp != null && retrieveResp.getData() instanceof MusicTrack track) {
            byte[] bytes = track.getAudioFile();
            if (bytes != null) {
                File outFile = new File("retrieved-" + track.getFileName());
                java.nio.file.Files.write(outFile.toPath(), bytes);
                System.out.println("Reconstructed file on disk: " + outFile.getAbsolutePath());
            }
        }
    }

    private static void requestMetadata(PrintWriter output, BufferedReader input, int id) throws IOException {
        output.println("GET_METADATA:" + id);
        String response = input.readLine();
        System.out.println("\n📝 Metadata Response:");
        System.out.println(response);
    }

    private static File createDemoBinaryFile() throws IOException {
        File temp = File.createTempFile("musichub-upload-demo", ".wav");
        byte[] demoBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        java.nio.file.Files.write(temp.toPath(), demoBytes);
        temp.deleteOnExit();
        return temp;
    }
}
