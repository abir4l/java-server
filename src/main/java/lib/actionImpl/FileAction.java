package lib.actionImpl;

import lib.actions.Action;
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
    public String dispatchAction(String url) throws IOException {
        // verification for if the files should be downloaded or rendered
        if (url.equalsIgnoreCase("/"))
            url = "/index.html";
        return procesUrl(url);

    }

    @Override
    public String procesUrl(String url) throws IOException {

        File file = new File(Propertise.WEB + url);
        StringBuilder lines = new StringBuilder();
        String line;
        if (file.exists() && !file.isDirectory()) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                lines.append(line);
            }
        } else if (file.isDirectory()) {
            File files[] = file.listFiles();
            assert files != null;
            lines.append("<!DOCTYPE html><html lang=\"en\"><body><ul>");
            for (File f : files) {
                lines.append("<li>")
                        .append(f.getName())
                        .append("<br/>")
                        .append("</li>");
            }
            lines.append("</ul></body></html");
        } else {
            lines.append("Implement directory listing or 404 error");
        }

        return lines.toString();
    }
}
