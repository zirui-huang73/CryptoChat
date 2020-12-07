package ChatServer;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.util.List;


public class ServerHandler extends Thread {
    private Socket connectSocket;
    private final Server server;
    private DataInputStream localInput;
    private DataOutputStream localOutput;
    private String login;
    private BigInteger publicKey;


    public ServerHandler(Server server, Socket connectSocket) throws IOException {
        this.server = server;
        this.connectSocket = connectSocket;
        localInput = new DataInputStream(new BufferedInputStream(connectSocket.getInputStream()));
        localOutput = new DataOutputStream(connectSocket.getOutputStream());
    }


    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleClientSocket() throws IOException {

        String receivedMsg;
        while ((receivedMsg=localInput.readUTF())!=null){
            // First, check if there is any command
            String[] tokens = StringUtils.split(receivedMsg, ".");
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if ("logoff".equalsIgnoreCase(cmd)) {
                    handleLogOff();
                    break;
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(tokens);
                } else if ("msg".equals(cmd)) {
                        System.out.print(login + " :");

                        String encryptedMsg = receivedMsg.substring(4);
                        System.out.println(encryptedMsg);
//                        String[] tokenMsg = StringUtils.split(receivedMsg, null, 2);
                        handleMessage(encryptedMsg);
                } else if ("publicKey".equals(cmd)) {
                    receivePublicKey(tokens);
                    sharePublicKey();
                }
                else {
                    System.out.println("cannot understand");
                }
            }
        }
    }

    private void sharePublicKey() throws IOException {

        // send this client's public key to others
        String keyMsg = "peerKey." + publicKey.toString();
        List<ServerHandler> handlerList = server.getHandlerList();
        for (ServerHandler handler: handlerList) {
            if (handler.getLogin()!= null && !handler.getLogin().equals(login)) {
                handler.send(keyMsg);
                if (handler.getPublicKey() != null) {
                    String peerKeyMsg = "peerKey." + handler.getPublicKey();
                    send(peerKeyMsg);
                }
            }
        }
    }

    private void receivePublicKey(String[] tokens) {
        publicKey = new BigInteger(tokens[1]);
    }

    private void handleLogOff() throws IOException {
        System.out.println(login + " has logoff");
        server.removeHanlder(this);
        // Let other clients know this client is logoff
        List<ServerHandler> handlerList = server.getHandlerList();
        String logoffMsg = "server." + login + " logged off";
        for (ServerHandler handler:handlerList) {
            handler.send(logoffMsg);
        }
        Server.numConnected --;
    }

    private void handleLogin(String[] tokens) throws IOException {
        if (Server.numConnected > 1) {
            System.out.println("Exceed user number");
            send("failed there have 2 users already");
            return;
        }
        if (tokens.length ==2) {
            String login = tokens[1];
            if (!duplicateName(login)) {
                this.login = login;
                System.out.println("User logged in successfully " + login);
                List<ServerHandler> handlerList = server.getHandlerList();
                String successMsg = "success";
                send(successMsg);
                Server.numConnected++;
                // Let this client know who else is online
                for (ServerHandler handler:handlerList) {
                    if (!login.equals(handler.getLogin()) && handler.getLogin()!=null){
                        String msg = "server.online: " + handler.getLogin();
                        send (msg);
                    }
                }
                // Let other client knows the this client is online
                String onlineMsg = "server." + login + " just logged in";
                for (ServerHandler handler:handlerList) {
                    if (handler.getLogin()!= null && !handler.getLogin().equals(login)){
                        handler.send(onlineMsg);
                    }
                }
            } else {
                String msg = "failed duplicate name";
                send(msg);
            }
        }
    }

    private boolean duplicateName(String login) {
        for (ServerHandler handler:server.getHandlerList()) {
            if (handler.getLogin() != null && handler.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    private void handleMessage(String EncryptedMsg) throws IOException {
//        String body = null;
//        try{ body = tokens[1];
//        } catch (ArrayIndexOutOfBoundsException e) {
//            System.out.println("[Server] Out of Bound Error");
//        }
//        System.out.println(body);
        List<ServerHandler> handlerList = server.getHandlerList();
        String msg = "msg." + getLogin() + "." + EncryptedMsg;
        for (ServerHandler handler: handlerList) {
            if (!handler.getLogin().equals(login))
            handler.send(msg);
        }
    }

    public String getLogin() {
        return login;
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    private void send(String msg) throws IOException {
            localOutput.writeUTF(msg);
    }

    private void close() {
        try {
            connectSocket.close();
            localInput.close();
            localOutput.close();
        } catch (IOException e) {
            System.out.println("[SERVER] " + e.getMessage());
        }
        System.out.println("[SERVER] Closing connection with" + getLogin());
    }
}
