package app;

import model.HttpSession;
import util.HttpHandler;
import util.Propertise;

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
                session = socket.accept();
                session.setSoTimeout(5000);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Socket clientSession = session;
            //Continues the while loop
           Propertise.executor.execute(() -> {
               HttpSession httpSession = new HttpSession(clientSession);
               HttpHandler.handleSession(httpSession);

           });



        }
    }
}
