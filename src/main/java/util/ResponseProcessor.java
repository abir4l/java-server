package util;

import app.HttpResponse;
import lib.actions.Action;

import java.io.*;


/**
 * @author abiral
 * created on 10/13/20
 */

public class ResponseProcessor {

    private final String NOT_FOUND = "404 NOT_FOUND";
    private final String OK = "200 OK";

    Action action;

    public ResponseProcessor(Action action) {
        this.action = action;
    }

    private static final String BAD_REQUEST = "400 BAD_REQUEST";

    public void sendResponse(OutputStream outputStream, String url) {

        OutputStreamWriter osw = new OutputStreamWriter(outputStream);
        String lines = null;

        try {
            lines = action.dispatchAction(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter bw = new PrintWriter(osw);
        bw.println(generateResponse(OK) + "Connection: close\r\n");
        bw.println(lines);
        bw.flush();
        bw.close();

    }

    public void sendError(OutputStream stream) {
        PrintWriter bw = new PrintWriter(new OutputStreamWriter(stream));
        bw.println(generateResponse(BAD_REQUEST) + "Connection: close\r\n");
        bw.close();
    }

    public String generateResponse(String code) {
        return String.format("%s %s\r\n", Propertise.HTTP_VERSION, code);
    }

    public void sendResponse(HttpResponse response) {
    }
}
