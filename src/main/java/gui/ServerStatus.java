package gui;

import app.Connection;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * @author abiral
 * created on 10/12/20
 */

public class ServerStatus extends JFrame {


    private int connections;
    private JLabel connectionLabel;
    private JTextArea textArea;
    private JButton start;
    private JButton stop;

    private Connection connection;

    private JLabel welcomeText;

    private ServerStatus() throws HeadlessException {

        super("Server Status");

        var northPanel = new JPanel();
        var mainPanel = new JPanel();
        var northButtonsPanel = new JPanel();

        stop = new JButton("stop");
        start = new JButton("Start");
        start.setEnabled(false);

        start.addActionListener(event ->this.startServer());
        stop .addActionListener( event -> this.stopServer());

        connectionLabel = new JLabel(getConnectionValue());
        welcomeText = new JLabel("Welcome text");

        textArea = new JTextArea();
        textArea.setEditable(false);

        var scrollTextArea = new JScrollPane(textArea);
        northButtonsPanel.setLayout(new BoxLayout(northButtonsPanel, BoxLayout.X_AXIS));
        northButtonsPanel.add(start);
        northButtonsPanel.add(stop);

        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(northButtonsPanel);
        northPanel.add(welcomeText);

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(scrollTextArea, BorderLayout.CENTER);
        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(connectionLabel, BorderLayout.SOUTH);
        mainPanel.setPreferredSize(new Dimension(300, 150));

        this.add(mainPanel);
        this.pack();


    }

    public void startApplication(Connection server) {
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        this.connection = server;
    }

    private String getConnectionValue() {
        String labelText = "%s = %s";
        return String.format(labelText, "Connections", String.valueOf(connections));
    }

    public static ServerStatus getInstance() {
        return new ServerStatus();
    }


    public void showWelcomeText() {
        this.welcomeText.setText("Server running on port 4000");
    }

    public void updateConnection() {
        connections++;
        connectionLabel.setText(getConnectionValue());
    }


    public void logger(String log) {
        String data = textArea.getText();
        textArea.setText(data + "\n" + log);
    }

    public void stopServer(){
        start.setEnabled(true);
        stop.setEnabled(false);
        logger("Stopping server..... ");
        connection.turnOffServer();
        logger("Server Stopped..... ");
        this.welcomeText.setText("Server Stopped.");
    }

    public void startServer(){
        start.setEnabled(false);
        stop.setEnabled(true);
        logger("Starting server");
        this.showWelcomeText();
        logger("Server started.. :D ");
        connection.listenForConnection();
    }



}
