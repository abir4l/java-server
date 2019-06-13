package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileManager {

    static String readFile(String url) throws IOException {
        File file = new File(Propertise.DIR+url);

        String lines = "";
        String line;
        if(file.exists()){
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                lines += line;
            }
        }
        else {
            lines+="Implement directory listing or 404 error";
        }

        return lines;
    }



}
