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
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {

            String request;

            while ((request = in.readLine()) != null) 
                {
                String responseJson;

                //get all
                if ("GET_ALL".equalsIgnoreCase(request)) 
                    {

                    List<MusicTrack> tracks = dao.getAll();
                    ServerResponse<List<MusicTrack>> resp =
                            ServerResponse.ok("Tracks fetched", tracks);

                    responseJson = MusicTrackJsonUtil.toJson(resp);

                }

                // delete
                else if (request.startsWith("DELETE:")) 
                    {

                    try 
                    {
                        int id = Integer.parseInt(request.split(":")[1]);

                        dao.deleteById(id);

                        ServerResponse<Object> resp =
                                ServerResponse.ok("Track deleted", null);

                        responseJson = MusicTrackJsonUtil.toJson(resp);

                    } catch (Exception e) 
                    
                    {
                        responseJson = MusicTrackJsonUtil.toJson
                        (
                                ServerResponse.error("Invalid delete request")
                        );
                    }

                }

                // unknown
                else 
                    {
                    ServerResponse<Object> resp =
                            ServerResponse.error("Unknown request");

                    responseJson = MusicTrackJsonUtil.toJson(resp);
                }

                out.println(responseJson);
            }

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        } 
        finally 
        {
            try { client.close(); } catch (Exception ignored) {}
        }
    }
}
