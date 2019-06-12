package model;

import lombok.Data;
import java.net.Socket;
@Data
public class HttpSession {
    private static int version = 0;
    private int id = version++;
    private HttpRequest httpRequest;
    private Socket connection;

    public HttpSession(Socket connection) {
        this.connection = connection;
    }
}
