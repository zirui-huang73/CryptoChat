/*
    TCP Sockets.Client.Client-Server.Server implementation reference list
    https://www.geeksforgeeks.org/socket-programming-in-java/
    https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
 */

/*
   A multithreaded TCP server
   Server receive message from 2 client, and forward information from A to B
 */


package ChatServer;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server
{
    //initialize socket and input stream
    private List<ServerHandler> handlerList = new ArrayList<>();
    private ServerSocket serverSocket;
    private int port = 0;
    static int numConnected = 0;

    public Server (int port) {
        this.port = port;
    }

    public List<ServerHandler> getHandlerList() {
        return handlerList;
    }

    public void start() {
        try {
            // Initialize server socket
            System.out.println("[SERVER] Initializing Socket");
            initServerSocket();
            // Accept client and handle connection
            System.out.println("[SERVER] Waiting For Client");
            while (true) {
               handleNewConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initServerSocket() throws IOException {
        serverSocket = new ServerSocket(port);
    }


    private void handleNewConnection() throws IOException {
        Socket connectSocket;
        connectSocket = serverSocket.accept();
        System.out.println("[SERVER] New client connection accepted from port" + connectSocket.getPort());
        ServerHandler serverHandler = new ServerHandler(this, connectSocket);
        handlerList.add(serverHandler);
        serverHandler.start();
    }

    public void removeHanlder(ServerHandler serverHandler) {
        handlerList.remove(serverHandler);
    }
}