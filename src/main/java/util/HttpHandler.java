package util;

import model.HttpRequest;
import model.HttpSession;
import servlet.Servlet;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class HttpHandler {

    private static final String BAD_REQUEST = "400 BAD_REQUEST";
    private final String NOT_FOUND = "404 NOT_FOUND";
    private final String OK = "200 OK";
    private final String ROOT_DIR = "/home/abiral/server-files";
    private final HeaderMethods methods = new HeaderMethods();

    private HttpRequest request;
    private HttpSession httpSession;

    private HttpHandler() {

    }

    public static void handleSession(HttpSession httpSession) {
        HttpHandler handler = new HttpHandler();
        handler.readRequest(httpSession);
    }

    private void readRequest(HttpSession session) {
        this.httpSession = session;

        Socket connection = session.getConnection();
        // IO reading thread for non blocking.
        Propertise.executor.execute(() -> {
            try {
                request = methods.parseRequest(httpSession);
                if (request == null){
                    sendError(connection.getOutputStream());
                }
                else{
                    try {
                        Servlet servlet = new Servlet();
                        Servlet.class.getMethod("processRequest",HttpRequest.class).invoke(servlet,request);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }


                    sendResponse(connection.getOutputStream(), request.getRequestPath());
                }
            } catch (SocketTimeoutException ste) {
                System.err.println("Timed out...");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        });

    }

    private void sendResponse(OutputStream outputStream, String url) {

        OutputStreamWriter osw = new OutputStreamWriter(outputStream);
        PrintWriter bw = new PrintWriter(osw);
        try {
            String lines = FileManager.readFile(url);
            bw.println(methods.generateResponse(OK) + "Connection: close\r\n");
            bw.println(lines);

        } catch (FileNotFoundException e) {
            bw.println(methods.generateResponse(NOT_FOUND));

        } catch (IOException e) {
            e.printStackTrace();
        }


        bw.flush();
        bw.close();
    }

    private void sendError(OutputStream stream) {
        PrintWriter bw = new PrintWriter(new OutputStreamWriter(stream));
        bw.println(methods.generateResponse(BAD_REQUEST) + "Connection: close\r\n");
        bw.close();
    }






}
