import http.HttpRequest;

import java.io.*;
import java.lang.invoke.WrongMethodTypeException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {


    private final String ROOT_DIR = "<server-files>";
    private final String HTTP_VERSION = "HTTP/1.1";
    private final String OK = "200 OK";
    private final String NOT_FOUND = "404 NOT_FOUND";
    private final String [] HTTP_METHODS ={"GET","POST"};
    private Map<String,String> headers = new HashMap<>();

    public static void main(String[] args) {
        new Server().connectionInit();
    }

    private final Pattern DIR = Pattern.compile("/.*\\s");
    private void connectionInit(){
        try {

            ServerSocket socket = new ServerSocket(4000);
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
        try{
            BufferedReader br = readFromStream(connection.getInputStream());
            HttpRequest request = getHttpMethod(br.readLine());
            request.setHeaders(parseHeaders(br));
            sendRespond(connection.getOutputStream(),request.getRequestPath());
        }

        catch (Exception e){

            e.printStackTrace();

        }
    }

    private Map<String,String> parseHeaders(BufferedReader br) {
        Map<String,String> headers = new HashMap<>();
        try{
            String line = "";
            while((line = br.readLine()) != null){

                if(line.isEmpty())break;
                String headerArray[] = line.split(":");
                headers.put(headerArray[0].trim(),headerArray[1].trim());
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
        return headers;
    }

    private HttpRequest getHttpMethod(String line) {
        String methodArray[]= line.split(" ");
        HttpRequest request = new HttpRequest();
        try{
            request.setHttpMethod(stripMethodFromHeader(methodArray[0]));
            request.setRequestPath(stripPathFromHeader(methodArray[1]));
        }catch (ArrayIndexOutOfBoundsException ar){
            throw new RuntimeException("HTTP header malformed");
        }

        return request;


    }

    private String stripPathFromHeader(String line) {
        line = line.replaceAll("\\.\\./","");
        Matcher matcher = DIR.matcher(line);
        if(matcher.find()){
            String file = matcher.group(0);
            return file.trim();
        }

        return null;
    }

    private String stripMethodFromHeader(String method) {
        return Arrays.stream(HTTP_METHODS)
                .filter(s1 -> method.equalsIgnoreCase(s1))
                .findAny().orElseThrow(WrongMethodTypeException::new);
    }

    private String generateResponse(String code){
        return String.format("%s %s\r\n", HTTP_VERSION,code);
    }

    private BufferedReader readFromStream(InputStream inputStream){
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(isr);
        return br;
    }

}

