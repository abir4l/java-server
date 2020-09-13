package app;

import gui.ServerStatus;
import model.HttpSession;
import util.HttpHandler;
import util.Propertise;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection {

    public final static ServerStatus APPLICATION_STATE = ServerStatus.getInstance();

    private ServerSocket socket;

    public void connectionInit() {
        try {
            socket = new ServerSocket(4000);
            System.out.println("Server running on port 4000");
            APPLICATION_STATE.showWelcomeText();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForConnection(){

        while(true){

            Socket session = null;
            try {
                session = socket.accept();
                APPLICATION_STATE.updateConnection();
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
