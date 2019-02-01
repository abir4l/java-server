package app;

import util.Parser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection {


    private ServerSocket socket;

    public void connectionInit(){

        try {
            socket = new ServerSocket(4000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            Socket session = null;
            try {
                System.out.println("waiting...");
                session = socket.accept();
                session.setSoTimeout(5000);
                System.out.println("accpeted...");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Socket clientSession = session;
            //Continues the while loop
            new Thread(() -> {
                Parser.parse(clientSession);
            }).run();


        }
    }
}
