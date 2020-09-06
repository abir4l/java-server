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

    private ServerStatus() throws HeadlessException {

        super("Server Status");
        connectionLabel = new JLabel(getConnectionValue());
        this.add(connectionLabel);


    }


    public void startApplication(){

        setLocationRelativeTo(null);
        setSize(500,500);
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



    public void updateConnection(){
        connections ++;
        connectionLabel.setText(getConnectionValue());
    }





}
