package ChatClient;
import java.math.BigInteger;
import java.net.*;
import java.io.*;

import org.apache.commons.lang3.StringUtils;


public class Client
{
    // initialize socket and input output streams
    private Socket socket;
    private String address;
    private int port;
    private DHExchange dhExchange;
    private DataOutputStream output;
    private DataInputStream input;

    MessageListener messageListener;



    // constructor to put ip address and port
    public Client(String address, int port) {
        if (port < 0 || port > 65353 ) {
            System.out.println("[CLIENT] Invalid port number");
        }
        this.address = address;
        this.port = port;
        dhExchange = new DHExchange();
    }

    public boolean connect() {
        System.out.println("[LOCAL] Initializing Connection");
        try {
            socket = new Socket(address, port);
            System.out.println("[LOCAL] Connection Established");
            this.output =  new DataOutputStream(socket.getOutputStream());
            this.input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    readMessageLoop();
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        socket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    private void readMessageLoop() throws IOException {
        String receivedMsg;
        while ((receivedMsg=input.readUTF()) != null) {
            String[] tokens = StringUtils.split(receivedMsg, ".");
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if ("msg".equalsIgnoreCase(cmd)) {
                    String[] tokenPeerMsg = StringUtils.split(receivedMsg, ".", 3);
                    handlePeerMsg(tokenPeerMsg);
                } else if ("peerKey".equalsIgnoreCase(cmd)) {
                    handlePeerPublicKey(tokens);
                } else if ("server".equalsIgnoreCase(cmd)) {
                    String[] tokenServerMsg = StringUtils.split(receivedMsg, ".", 2);
                    handleServerMsg(tokenServerMsg);
                }
            }
        }
    }


    public int login(String username) throws IOException {
        String loginMsg = "login." + username;
        output.writeUTF(loginMsg);
        String response = input.readUTF();
        if("success".equalsIgnoreCase(response)) {
            System.out.println("logged in");
            sendPublicKey();
            return 0;
        } else if ("failed duplicate name".equalsIgnoreCase(response)) {
            System.out.println(response);
            return 1;
        } else {
            System.out.println(response);
            return 2;
        }
    }

    public void sendPeerMessage(String msgBody) throws IOException {
        if (dhExchange.getAesKey() == null) {
           messageListener.onMessage("[Warning] >>> AES key has not been generated, message rejected.");
        } else {
            String  encryptedMsg = AESEncoder.encode(msgBody,dhExchange.getAesKey());
            String msg = "msg." + encryptedMsg;
            output.writeUTF(msg);
        }
    }

    private void sendPublicKey() throws IOException {
        String keyMsg = "publicKey." + dhExchange.getPublicKey();
        output.writeUTF(keyMsg);
    }


    public void logoff() throws IOException {
        String logoffMsg = "logoff.";
        output.writeUTF(logoffMsg);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        socket.close();
        System.exit(0);
    }




    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }


    private void handlePeerMsg(String[] tokenPeerMsg) {
        // empty string
        String peerName = tokenPeerMsg[1];
        String msgBody = null;
        try {
            msgBody = tokenPeerMsg[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            msgBody = "";
        }

        String decryptedMsg = AESDecoder.decode(msgBody, dhExchange.getAesKey());
        String msg = "[" + peerName + "] >>> " + decryptedMsg;
        messageListener.onMessage(msg);

    }

    private void handleServerMsg(String[] tokens) {
        String serverMsg = tokens[1];
        String msg = "[Server] >>> " + serverMsg;
        messageListener.onMessage(msg);
    }

    private void handlePeerPublicKey(String[] tokens) {
        BigInteger peerPublicKey = new BigInteger(tokens[1]);
        dhExchange.setPrivateShareKey(peerPublicKey);
        dhExchange.setAESKey();
        messageListener.onMessage("[AES KEY]  >>> " + dhExchange.getAesKey());
        System.out.println(dhExchange.getAesKey());
    }


    public void showWelcomeMessage(String name) {
        String welcomeMessage = "   -------- WELCOME TO E2E CHAT " + name + " -------- ";
        messageListener.onMessage(welcomeMessage);
    }
}