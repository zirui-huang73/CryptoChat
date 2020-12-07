package ChatServer;

import java.io.IOException;

public class ServerMain {
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws IOException {
        Server server = new Server(DEFAULT_PORT);
        server.start();
    }
}
