import app.Connection;
import util.Propertise;

public class Server {


    public static void main(String[] args) {
        Propertise.executor.execute(()->{
            Connection server = new Connection();
            Connection.APPLICATION_STATE.startApplication(server);
            server.connectionInit();
            server.listenForConnection();
        });

    }

}

