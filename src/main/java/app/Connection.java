package app;

import gui.ServerStatus;
import model.HttpSession;
import util.HttpHandler;
import util.Propertise;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Connection {

    public final static ServerStatus APPLICATION_STATE = ServerStatus.getInstance();

    private ServerSocket socket;
    private boolean listening = true;

    public void connectionInit() {
        try {
            socket = new ServerSocket(4000);
            System.out.println("Server running on port 4000");
            APPLICATION_STATE.showWelcomeText();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void turnOffServer() {
        try {
            socket.close();
            this.listening = false;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void listenForConnection() {
        listening = true;
        Propertise.executor.execute(() -> {
            while (listening) {
                Socket session = null;
                try {
                    if (listening && socket.isClosed())
                        socket = new ServerSocket(4000);
                    session = socket.accept();
                    Socket clientSession = session;
                    //Continues the while loop
                    HttpSession httpSession = new HttpSession(clientSession);
                    HttpHandler.handleSession(httpSession);
                    APPLICATION_STATE.updateConnection();
                } catch (SocketException ex) {
                    if (!ex.getMessage().toLowerCase().equals("socket closed")) {
                        ex.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }
}
