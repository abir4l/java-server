package model;

import app.HttpResponse;
import lombok.Data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
    public OutputStream getOutputStream(){
        try {
            return this.connection.getOutputStream();
        } catch (IOException ignored) {
        }
        return null;
    }
}
