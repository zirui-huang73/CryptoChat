package ChatClient;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class ClientMain extends JFrame{
    private final String DEFAULT_IP = "127.0.0.1";
    private final int DEFAULT_PORT = 8080;
    private final Client client;
    JPanel panel;
    JLabel userLabel, passwordLabel;
    JTextField userText, passwordText;
    JButton button;
    WindowAdapter exitAdapter;

    public ClientMain() {
        client = new Client(DEFAULT_IP, DEFAULT_PORT);
        client.connect();
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        exitAdapter = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    client.logoff();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        };
        addWindowListener(exitAdapter);


        panel = new JPanel();
        panel.setLayout(null);
        userLabel = new JLabel("User");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        userText = new JTextField(20);
        userText.setBounds(100, 20, 165, 25);
        panel.add(userText);

        passwordLabel = new JLabel("password");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordText = new JPasswordField();
        passwordText.setBounds(100, 50, 165, 25);
        panel.add(passwordText);

        button = new JButton("Login");
        button.setBounds(10, 80, 80, 25);
        panel.add(button);
        setupButton();

        getContentPane().add(panel, BorderLayout.CENTER);
        pack();
    }


    private void setupButton() {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });
    }

    private void doLogin() {
        String login = userText.getText();
        try {
            int res = client.login(login);
            if (res == 0) {
                MessagePane messagePane = new MessagePane(client);
                JFrame f = new JFrame("E2E Chat Room");


                f.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                f.addWindowListener(exitAdapter);

                f.setSize(500, 500);
                f.getContentPane().add(messagePane, BorderLayout.CENTER);
                f.setVisible(true);
                setVisible(false);
                client.showWelcomeMessage(login);
                client.startMessageReader();
            }  else if (res == 1){
                JOptionPane.showMessageDialog(this, "This name has already be used.");
            } else {
                JOptionPane.showMessageDialog(this, "There are already two users");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ClientMain gui = new ClientMain();
        gui.setTitle("Login Window");
        gui.setSize(300, 150);
        gui.setVisible(true);
    }
}
