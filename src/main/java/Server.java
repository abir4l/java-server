import http.HttpRequest;
import sessions.Session;
import util.ConnectionUtils;

import java.io.*;
import java.net.ServerSocket;


public class Server {

    private final ConnectionUtils utils = new ConnectionUtils();
    ServerSocket socket;



    public static void main(String[] args) {
        new Server().connectionInit();
    }

    private void connectionInit(){
        try {
            socket = new ServerSocket(4000);
        }catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(()->{
            while(true){
                try {
                    Session session = new Session(socket.accept());
                    utils.readRequest(session.getSocket());
                    session.closeSocket();
                }catch (IOException e){
                    e.printStackTrace();
                }


            }

        }).run();



    }



}

