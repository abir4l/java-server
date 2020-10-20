package util;

import app.HttpResponse;
import lib.actionImpl.DirectoryAction;
import lib.actionImpl.FileAction;
import lib.actions.Action;
import lib.requestType.RequestType;
import model.HttpRequest;
import model.HttpSession;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Optional;

public class HttpHandler {


    private final String ROOT_DIR = "/home/abiral/server-files";
    private RequestParser parser;
    private ResponseProcessor responseProcessor;

    private HttpRequest request;
    private HttpSession httpSession;

    private HttpHandler( RequestParser parser) {
        this();
        this.parser = parser;
    }

    private HttpHandler(){
        this.responseProcessor = new ResponseProcessor();

    }
    public static void handleSession(HttpSession httpSession) {

        RequestParser requestParser = new RequestParser();
        HttpHandler handler = new HttpHandler(requestParser);
        handler.processSession(httpSession);
    }

    private void processSession(HttpSession session) {

        this.httpSession = session;
        Socket connection = session.getConnection();
        // IO reading thread for non blocking.
        Propertise.executor.execute(() -> {
            try {
                request = parser.parseRequest(httpSession);

                if (request == null){
                    //handle empty request here
                    responseProcessor.sendError(connection.getOutputStream());
                }
                else{
                    //based on the request we configure response here
                    Optional<Action> action = responseProcessor.getAction(request);
                    if(action.isPresent()){
                        HttpResponse response = serveResponse(request,action.orElseGet(() -> null));
                        assert response != null;
                        response.setStatus("200 OK");
                        this.responseProcessor.sendResponse(response,session.getOutputStream());
                    }else{
                        this.responseProcessor.sendError(session.getOutputStream());
                    }

}
            } catch (SocketTimeoutException ste) {
                System.err.println("Timed out...");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        });

    }

    private HttpResponse serveResponse(HttpRequest request, Action action) {

        // process directory

        try {
            return action.dispatchAction(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

}
