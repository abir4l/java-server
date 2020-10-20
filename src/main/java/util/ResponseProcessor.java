package util;

import app.HttpResponse;
import lib.actionImpl.DirectoryAction;
import lib.actionImpl.FileAction;
import lib.actions.Action;
import lib.requestType.RequestType;
import model.HttpRequest;
import java.io.*;
import java.util.Map;
import java.util.Optional;


/**
 * @author abiral
 * created on 10/13/20
 */

public class ResponseProcessor {

    private final String NOT_FOUND = "404 NOT_FOUND";
    private final String OK = "200 OK";


    public ResponseProcessor() {

    }

    private static final String BAD_REQUEST = "400 BAD_REQUEST";

    public void sendResponse(OutputStream outputStream, String url,Action action) {

        OutputStreamWriter osw = new OutputStreamWriter(outputStream);
        String lines = null;

//        try {
//            lines = action.dispatchAction(url);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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

    public void sendResponse(HttpResponse response,OutputStream stream) {

        try
        {
            Map<String ,String> header = response.getHeaders();
            if ( response.getStatus()== null )
                throw new Error( "sendResponse(): Status can't be null." );


            PrintWriter pw = new PrintWriter( stream);
            pw.print("HTTP/1.0 " + response.getStatus() + " \r\n");

            if ( response.getMimeType() != null )
                pw.print("Content-Type: " + response.getMimeType() + "\r\n");

            if ( header != null )
            {
                header.forEach((key, value) -> pw.print(key + ": " + value + "\r\n"));
            }

            pw.print("\r\n");
            pw.flush();

            if(response.getTextData() != null && response.getData() == null){
                response.setData(new ByteArrayInputStream(
                        response.getTextData().getBytes()
                ));
            }
            if ( response.getData() != null )
            {
                byte[] buff = new byte[2048];
                int read = 2048;
                while ( read == 2048 )
                {
                    read = response.getData().read( buff, 0, 2048 );
                    stream.write( buff, 0, read );
                }
            }
            stream.flush();
            stream.close();
            if ( response.getData()!= null )
                response.getData().close();
        }
        catch( IOException ioe )
        {

        }
    }

    public Optional<Action> getAction(HttpRequest request) {
        Action action = null;
        if (request.getRequestType().isPresent()) {
            if (request.getRequestType().get().equals(RequestType.FILE) || request.getRequestType().get().equals(RequestType.WEBPAGE))
                action = new FileAction();
            else if (request.getRequestType().get().equals(RequestType.DIR))
                action = new DirectoryAction();
        }
        return Optional.ofNullable(action);

    }
}
