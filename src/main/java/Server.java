import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {



    private final String ROOT_DIR = "root-dir";
    private final String HTTP_METHOD = "HTTP/1.1";
    private final String OK = "200 OK";
    private final String NOT_FOUND = "404 NOT_FOUND";

    public static void main(String[] args) {
        new Server().connectionInit();
    }

    private final Pattern DIR = Pattern.compile("/.*\\s");

    private void connectionInit(){
        try {

            ServerSocket socket = new ServerSocket(4000);
            System.out.println("Starting server");
            Socket connection = socket.accept();
            readRequest(connection);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendRespond(OutputStream outputStream,String file) {

        OutputStreamWriter osw = new OutputStreamWriter(outputStream);
        PrintWriter bw = new PrintWriter(osw);

        File f = new File(ROOT_DIR+file);
        try {
            String lines = readFile(f);
            bw.println(generateResponse(OK));
            bw.println(lines);

        } catch (FileNotFoundException e) {
            bw.println(generateResponse(NOT_FOUND));

        } catch (IOException e) {
            e.printStackTrace();
        }


        bw.flush();
    }

    private String readFile(File f) throws IOException {
        String lines = "";
        String line = "";
        BufferedReader br = readFromStream(new FileInputStream(f));
        while((line = br.readLine()) != null){
            lines +=line;
        }
        return lines;
    }

    private void readRequest(Socket connection) {

        List<String> data = new ArrayList<>();
        String line;
        String file = "";
        try{
            BufferedReader br = readFromStream(connection.getInputStream());
            while ((line =  br.readLine() )!= null){
                if(line.contains("GET")){
                    System.out.println(line);
                    Matcher m = DIR.matcher(line);
                    if(m.find()){
                        file = m.group(0);
                        System.out.println(m.group(0));
                    }
                }
                data.add(line);
                if(line.isEmpty())
                    break;
            }
            System.out.println(data);
            sendRespond(connection.getOutputStream(),file.trim());
        }



        catch (Exception e){

            e.printStackTrace();

        }
    }

    private String generateResponse(String code){
        return String.format("%s %s\r\n", HTTP_METHOD,code);
    }

    private BufferedReader readFromStream(InputStream inputStream){
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(isr);
        return br;
    }

}
