package lib.actionImpl;

import app.HttpResponse;
import lib.actions.Action;
import model.HttpRequest;
import util.Propertise;

import java.io.File;
import java.io.IOException;

/**
 * @author abiral
 * created on 10/13/20
 */

public class DirectoryAction implements Action {

    @Override
    public HttpResponse procesUrl(String url) throws IOException {

        HttpResponse response = new HttpResponse();
        File file = new File(Propertise.WEB+url);
        StringBuilder lines = new StringBuilder();
        File[] files = file.listFiles();
        assert files != null;
        lines.append("<!DOCTYPE html><html lang=\"en\"><body>");
        lines.append("<ul>");
        for (File f : files) {
            lines.append("<li>")
                    .append(String.format("<a href='%s/%s'>",url,f.getName()))
                    .append(f.getName())
                    .append("</a>")
                    .append("<br/>")
                    .append("</li>");
        }
        lines.append("</ul></body></html");
        response.setTextData(lines.toString());
        return response;
    }


    @Override
    public HttpResponse dispatchAction(HttpRequest request) throws IOException {
        return procesUrl(request.getRequestPath());
    }
}
