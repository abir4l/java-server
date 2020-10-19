package model;

import app.HttpResponse;
import lombok.Data;
import java.net.Socket;
@Data
public class HttpSession {
    private static int version = 0;
    private int id = version++;
    private HttpRequest httpRequest;
    private HttpResponse response;

    private Socket connection;

    public HttpSession(Socket connection) {
        this.connection = connection;
    }
}
