package musichub.server;

import musichub.dao.MusicTrackDao;
import musichub.dao.FakeMusicTrackDao;
import musichub.dao.JdbcMusicTrackDao;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final MusicTrackDao dao;
    private final ExecutorService pool;

    public Server(int port, MusicTrackDao dao) {
        if (port < 1024 || port > 65535)
            throw new IllegalArgumentException("Port must be 1024-65535");
        if (dao == null)
            throw new IllegalArgumentException("DAO required");

        this.port = port;
        this.dao = dao;
// F10 - ExecutorService for multithreaded clients
this.pool = Executors.newCachedThreadPool();
    }

    public void start() throws IOException {
        System.out.println("Server listening on port " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket client = serverSocket.accept();
                System.out.println("Client connected: " + client.getInetAddress());
// F10 - Each client own thread
pool.submit(new ClientHandler(client, dao));

            }
        }
    }

    public static void main(String[] args) throws Exception {
        MusicTrackDao dao = new JdbcMusicTrackDao();
        new Server(9001, dao).start();
    }
}