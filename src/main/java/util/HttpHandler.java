package util;

import lib.actionImpl.DirectoryAction;
import lib.actionImpl.FileAction;
import lib.actions.Action;
import lib.requestType.RequestType;
import model.HttpRequest;
import model.HttpSession;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class HttpHandler {


    private final String ROOT_DIR = "/home/abiral/server-files";
    private RequestParser parser;
    private ResponseProcessor responseProcessor;

    private HttpRequest request;
    private HttpSession httpSession;

    private HttpHandler( RequestParser parser) {
        this.parser = parser;
    }

    private HttpHandler(){

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
                    this.responseProcessor = new ResponseProcessor(null);
                    responseProcessor.sendError(connection.getOutputStream());
                }
                else{
                    //based on the request we configure response here
                    Action action = null;

                    if(request.getRequestType().isPresent()){
                        if(request.getRequestType().get().equals(RequestType.FILE) || request.getRequestType().get().equals(RequestType.WEBPAGE))
                            action = new FileAction();
                        else if(request.getRequestType().get().equals(RequestType.DIR))
                            action = new DirectoryAction();
                    }else{
                        this.responseProcessor = new ResponseProcessor(null);// because no action available for no resource
                        this.responseProcessor.sendError(connection.getOutputStream());
                        return;
                    }
                    this.responseProcessor = new ResponseProcessor(action);
                    this.responseProcessor.sendResponse(connection.getOutputStream(), request.getRequestPath());
                }
            } catch (SocketTimeoutException ste) {
                System.err.println("Timed out...");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        });

    }

}
