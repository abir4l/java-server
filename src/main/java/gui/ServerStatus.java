package gui;

import javax.swing.*;
import java.awt.*;

/**
 * @author abiral
 * created on 10/12/20
 */

public class ServerStatus extends JFrame {


    private int connections;
    private JLabel connectionLabel;
    private JTextArea textArea;


    private JLabel welcomeText;

    private ServerStatus() throws HeadlessException {

        super("Server Status");

        connectionLabel = new JLabel(getConnectionValue());
        welcomeText = new JLabel("Welcome text");
        textArea = new JTextArea();
        textArea.setEditable(false);
        var panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(textArea,BorderLayout.CENTER);
        panel.add(welcomeText,BorderLayout.NORTH);
        panel.add(connectionLabel,BorderLayout.SOUTH);
        this.add(panel);
        this.pack();


    }

    public void startApplication(){
        setLocationRelativeTo(null);
        setSize(250,150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private String getConnectionValue() {
        String labelText = "%s = %s";
        return String.format(labelText,"Connections",String.valueOf(connections));
    }

    public static ServerStatus getInstance(){
        return new ServerStatus();
    }


    public void showWelcomeText(){
        this.welcomeText.setText("Server running on port 4000");
    }

    public void updateConnection(){
        connections ++;
        connectionLabel.setText(getConnectionValue());
    }


    public void logger(String log){
        String data = textArea.getText();
        textArea.setText(data + "\n"+ log);
    }

}
