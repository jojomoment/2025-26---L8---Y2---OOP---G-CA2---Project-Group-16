package musichub.server;

import musichub.dao.MusicTrackDao;
import musichub.domain.MusicTrack;
import musichub.shared.ServerResponse;
import musichub.util.MusicTrackJsonUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket client;
    private final MusicTrackDao dao;

    public ClientHandler(Socket client, MusicTrackDao dao) {
        if (client == null || dao == null)
            throw new IllegalArgumentException("socket and dao required");
        this.client = client;
        this.dao = dao;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {

            String request;
            while ((request = in.readLine()) != null) {
                String responseJson;

                // simple Stage 2 handling: "GET_ALL" returns all tracks
                if ("GET_ALL".equalsIgnoreCase(request)) {
                    List<MusicTrack> tracks = dao.getAll();
                    ServerResponse<List<MusicTrack>> resp = ServerResponse.ok("Tracks fetched", tracks);
                    responseJson = MusicTrackJsonUtil.toJson(resp);
                } else {
                    ServerResponse<Object> resp = ServerResponse.error("Unknown request");
                    responseJson = MusicTrackJsonUtil.toJson(resp);
                }

                out.println(responseJson);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { client.close(); } catch (Exception ignored) {}
        }
    }
}