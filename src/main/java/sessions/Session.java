package sessions;

import java.io.IOException;
import java.net.Socket;

public class Session {

    public Session(Socket socket) {
        this.socket = socket;
    }

    Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public void closeSocket() throws IOException {
        this.socket.close();
    }
}
