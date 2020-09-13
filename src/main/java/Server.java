import app.Connection;
import util.Propertise;

public class Server {


    public static void main(String[] args) {

        Connection.APPLICATION_STATE.startApplication();
        Propertise.executor.execute(()->{
            Connection server = new Connection();
            server.connectionInit();
            server.listenForConnection();
        });

    }

}

