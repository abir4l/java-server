package lib.actionImpl;

import app.HttpResponse;
import lib.actions.Action;
import model.HttpRequest;
import util.Propertise;

import java.io.*;

/**
 * @author abiral
 * created on 10/13/20
 */

public class FileAction implements Action {


    public FileAction() {
    }

    @Override
    public HttpResponse dispatchAction(HttpRequest request) throws IOException {
        // verification for if the files should be downloaded or rendered
        String url= request.getRequestPath();
        if (request.getRequestPath().equalsIgnoreCase("/"))
            url = "/index.html";
        return procesUrl(url);

    }

    @Override
    public HttpResponse procesUrl(String url) throws IOException {

        int dot = url.lastIndexOf('.');
        String mime = "";
        if (dot >= 0) {
            mime = (String) Propertise.mimeType.get(url.substring(dot + 1).toLowerCase());
        }
        if (mime == null)
            mime = Propertise.MIME_DEFAULT_BINARY;

        HttpResponse response = new HttpResponse(mime);
        File file = new File(Propertise.WEB + url);
        StringBuilder lines = new StringBuilder();
        String line;
        if (file.exists() && !file.isDirectory()) {
            long startFrom = 0;
            FileInputStream fis = new FileInputStream(file);
            fis.skip(startFrom);
            response.addHeader("Content-length", "" + (file.length() - startFrom));
            response.addHeader("Content-range", "" + startFrom + "-" +
                    (file.length() - 1) + "/" + file.length());
            response.setData(fis);
            return response;
        }
        return response;
    }

}
