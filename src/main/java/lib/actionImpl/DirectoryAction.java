package lib.actionImpl;

import lib.actions.Action;
import util.Propertise;

import java.io.File;
import java.io.IOException;

/**
 * @author abiral
 * created on 10/13/20
 */

public class DirectoryAction implements Action {

    @Override
    public String procesUrl(String url) throws IOException {
        File file = new File( url);
        StringBuilder lines = new StringBuilder();
        File[] files = file.listFiles();
        assert files != null;
        lines.append("<!DOCTYPE html><html lang=\"en\"><body><ul>");
        for (File f : files) {
            lines.append("<li>")
                    .append(f.getName())
                    .append("<br/>")
                    .append("</li>");
        }
        lines.append("</ul></body></html");

        return lines.toString();
    }


    @Override
    public String dispatchAction(String url) throws IOException {
        return procesUrl(Propertise.WEB+url);
    }
}
