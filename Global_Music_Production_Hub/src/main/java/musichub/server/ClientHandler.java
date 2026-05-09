package musichub.server;

import musichub.dao.MusicTrackDao;
import musichub.domain.MusicTrack;
import musichub.shared.ServerResponse;
import musichub.util.MusicTrackJsonUtil;
import java.util.Optional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

// F11 - ServerResponse wrapper for all replies


public class ClientHandler implements Runnable 
{

    private final Socket client;
    private final MusicTrackDao dao;

    public ClientHandler(Socket client, MusicTrackDao dao) 
    {
        if (client == null || dao == null)
            throw new IllegalArgumentException("socket and dao required");
        this.client = client;
        this.dao = dao;
    }

    @Override
    public void run()
     {
        try (Socket currentClient = client;
             BufferedReader in = new BufferedReader(new InputStreamReader(currentClient.getInputStream()));
             PrintWriter out = new PrintWriter(currentClient.getOutputStream(), true)) {

            String request;

            while ((request = in.readLine()) != null) 
                {
                String responseJson;
                // F12 - Display All + By ID
                // get all
                if ("GET_ALL".equalsIgnoreCase(request)) 
                    {
                    try {
                        List<MusicTrack> tracks = dao.getAll();
                        ServerResponse<List<MusicTrack>> resp =
                                ServerResponse.ok("Tracks fetched", tracks);
                        responseJson = MusicTrackJsonUtil.toJson(resp);
                    } catch (Exception e) {
                        responseJson = MusicTrackJsonUtil.toJson(
                                ServerResponse.error("Failed to fetch tracks"));
                    }
                }
                // F14 Delete Entity
                // delete
                else if (request.startsWith("DELETE:")) 
                    {
                    try {
                        String[] parts = request.split(":");
                        int id = Integer.parseInt(parts[1]);
                        boolean deleted = dao.deleteById(id);
                        if (deleted) {
                            ServerResponse<Object> resp =
                                    ServerResponse.ok("Track deleted", null);
                            responseJson = MusicTrackJsonUtil.toJson(resp);
                        } else {
                            responseJson = MusicTrackJsonUtil.toJson(
                                    ServerResponse.error("Track not found"));
                        }
                    } catch (Exception e) {
                        responseJson = MusicTrackJsonUtil.toJson(
                                ServerResponse.error("Invalid delete request"));
                    }
                }
                // F15 Update Entity
                // update
                else if (request.startsWith("UPDATE:")) 
                    {
                    try {
                        String json = request.substring(7);
                        MusicTrack track =
                                MusicTrackJsonUtil.fromJson(json, MusicTrack.class);
                        MusicTrack updated = dao.updateTrack(
                                track.getSongId(),
                                track.getSongTitle(),
                                track.getBpm(),
                                track.getDurationInSeconds()
                        );
                        if (updated != null) {
                            ServerResponse<MusicTrack> resp =
                                    ServerResponse.ok("Track updated", updated);
                            responseJson = MusicTrackJsonUtil.toJson(resp);
                        } else {
                        responseJson = MusicTrackJsonUtil.toJson(
                                    ServerResponse.error("Update failed - track not found"));
                        }
                    } catch (Exception e) {
                        responseJson = MusicTrackJsonUtil.toJson(
                                ServerResponse.error("Invalid update request"));
                    }
                }
                // F12 - Display By ID
                // get by id
                else if (request.startsWith("GET_BY_ID:")) 
                    {
                    try {
                        String[] parts = request.split(":");
                        int id = Integer.parseInt(parts[1]);
                        Optional<MusicTrack> opt = dao.getMusicTrackById(id);
                        if (opt.isPresent()) {
                            ServerResponse<MusicTrack> resp =
                                    ServerResponse.ok("Track fetched", opt.get());
                            responseJson = MusicTrackJsonUtil.toJson(resp);
                        } else {
                            responseJson = MusicTrackJsonUtil.toJson(
                                    ServerResponse.error("Track not found"));
                        }
                    } catch (Exception e) {
                        responseJson = MusicTrackJsonUtil.toJson(
                                ServerResponse.error("Invalid get by id request"));
                    }
                }
                // F13 Add Entity
                // insert
                else if (request.startsWith("INSERT:")) 
                    {
                    try {
                        String json = request.substring(7);
                        MusicTrack temp = MusicTrackJsonUtil.fromJson(json, MusicTrack.class);
                        int newId = dao.insert(
                                temp.getSongTitle(),
                                temp.getBpm(),
                                temp.getDurationInSeconds()
                        );
                        MusicTrack newTrack = new MusicTrack(newId,
                                temp.getSongTitle(),
                                temp.getBpm(),
                                temp.getDurationInSeconds());
                        ServerResponse<MusicTrack> resp =
                                ServerResponse.ok("Track created", newTrack);
                        responseJson = MusicTrackJsonUtil.toJson(resp);
                    } catch (Exception e) {
                        responseJson = MusicTrackJsonUtil.toJson(
                                ServerResponse.error("Invalid insert request"));
                    }
                }
                // Binary upload
                else if (request.startsWith("UPLOAD_BINARY:")) 
                    {
                    try {
                        String json = request.substring(14);
                        MusicTrack temp = MusicTrackJsonUtil.fromJson(json, MusicTrack.class);
                        int newId = dao.insertBinary(
                                temp.getSongTitle(),
                                temp.getBpm(),
                                temp.getDurationInSeconds(),
                                temp.getAudioFile(),
                                temp.getFileName(),
                                temp.getContentType(),
                                temp.getFileSize()
                        );
                        MusicTrack newTrack = new MusicTrack(newId,
                                temp.getSongTitle(),
                                temp.getBpm(),
                                temp.getDurationInSeconds(),
                                temp.getAudioFile(),
                                temp.getFileName(),
                                temp.getContentType(),
                                temp.getFileSize());
                        ServerResponse<MusicTrack> resp =
                                ServerResponse.ok("Binary track uploaded", newTrack);
                        responseJson = MusicTrackJsonUtil.toJson(resp);
                    } catch (Exception e) {
                        responseJson = MusicTrackJsonUtil.toJson(
                                ServerResponse.error("Invalid binary upload request"));
                    }
                }
                // Binary retrieve
                else if (request.startsWith("RETRIEVE_BINARY:")) 
                    {
                    try {
                        String[] parts = request.split(":");
                        int id = Integer.parseInt(parts[1]);
                        Optional<MusicTrack> opt = dao.getMusicTrackWithBinaryById(id);
                        if (opt.isPresent()) {
                            ServerResponse<MusicTrack> resp =
                                    ServerResponse.ok("Binary track retrieved", opt.get());
                            responseJson = MusicTrackJsonUtil.toJson(resp);
                        } else {
                            responseJson = MusicTrackJsonUtil.toJson(
                                    ServerResponse.error("Binary track not found"));
                        }
                    } catch (Exception e) {
                        responseJson = MusicTrackJsonUtil.toJson(
                                ServerResponse.error("Invalid binary retrieve request"));
                    }
                }
                // Get metadata
                else if (request.startsWith("GET_METADATA:")) 
                    {
                    try {
                        String[] parts = request.split(":");
                        int id = Integer.parseInt(parts[1]);
                        Optional<MusicTrack> opt = dao.getMusicTrackMetadataById(id);
                        if (opt.isPresent()) {
                            ServerResponse<MusicTrack> resp =
                                    ServerResponse.ok("Metadata retrieved", opt.get());
                            responseJson = MusicTrackJsonUtil.toJson(resp);
                        } else {
                            responseJson = MusicTrackJsonUtil.toJson(
                                    ServerResponse.error("Track not found"));
                        }
                    } catch (Exception e) {
                        responseJson = MusicTrackJsonUtil.toJson(
                                ServerResponse.error("Invalid metadata request"));
                    }
                }
                // disconnect
                else if ("DISCONNECT".equalsIgnoreCase(request)) 
                    {
                    ServerResponse<Object> resp = ServerResponse.ok("Client disconnected", null);
                    responseJson = MusicTrackJsonUtil.toJson(resp);
                    out.println(responseJson);
                    break;
                }
                // unknown
                else 
                    {
// F16 - Error handling (no raw exceptions to client)
                ServerResponse<Object> resp = ServerResponse.error("Unknown request");
                    responseJson = MusicTrackJsonUtil.toJson(resp);
                }

                out.println(responseJson);
            }

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
}
