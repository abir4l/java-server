import app.Connection;
import gui.ServerStatus;

public class Server {

    public final static ServerStatus APPLICATION_STATE = ServerStatus.getInstance();

    public static void main(String[] args) {

        new Thread(){
            @Override
            public void run() {
                APPLICATION_STATE.startApplication();
            }
        }.run();

        Connection server = new Connection();
        server.connectionInit();

    }

}

