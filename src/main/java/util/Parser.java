package util;

import http.HttpRequest;
import http.HttpSession;
import servlet.Servlet;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {


    private static final String BAD_REQUEST = "400 BAD_REQUEST";
    private static final Integer BUFFER_SIZE = 8092;
    private final String HTTP_VERSION = "HTTP/1.1";
    private final String NOT_FOUND = "404 NOT_FOUND";
    private final String OK = "200 OK";
    private final String ROOT_DIR = "/home/abiral/server-files";
    private final String[] HTTP_METHODS = new String[]{"GET", "POST"};
    private final Pattern DIR = Pattern.compile("/.*(\\s|)");


    private HttpRequest request;
    private HttpSession httpSession;

    private Parser() {

    }

    public String getHeader(String key) {
        String value = this.request.getHeaders().get(key);
        return value;
    }

    public static void parse(HttpSession httpSession) {
        Parser parser = new Parser();
        parser.readRequest(httpSession);
    }

    private void readRequest(HttpSession session) {
        this.httpSession = session;
        Socket connection = session.getConnection();
        // IO reading thread for non blocking.
        Thread ioOperation = new Thread(() -> {
            try {
                request = parseRequest(httpSession);
                if (request == null)
                {

                        sendError(connection.getOutputStream());
//                    sendRespond(connection.getOutputStream(), "Test.java");
                }
                else{
                    try {
                        Servlet servlet = new Servlet();
                        Servlet.class.getMethod("processRequest",HttpRequest.class).invoke(servlet,request);

                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }


                    sendRespond(connection.getOutputStream(), request.getRequestPath());
                }
            } catch (SocketTimeoutException ste) {
                System.err.println("Timed out...");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        });

        ioOperation.start();


    }

    private HttpRequest parseRequest(HttpSession session) throws IOException {
        InputStream stream = session.getConnection().getInputStream();
        byte buffer[] = new byte[BUFFER_SIZE];
        int headerFinalIndex = findHeader(stream,buffer);
        return parseHeader(buffer,headerFinalIndex);
    }

    private HttpRequest parseHeader(byte[] buffer,Integer splitbyte) {
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer, 0, splitbyte)));
        HttpRequest request = new HttpRequest();
        String line = "";
        try {
            while ((line = br.readLine()) != null) {
                String methodArray[] = line.split(" ");

                try {
                    request.setHttpMethod(getHTTPMethod(methodArray[0]));
                    request.setRequestPath(getRequestURI(methodArray[1]));
                } catch (ArrayIndexOutOfBoundsException ar) {
                    throw new RuntimeException("HTTP header malformed");
                }
                request.setHeaders(getRequestHeaders(br));

            }

            Map<String, String> params = new HashMap<>();
            if (request.getHttpMethod().equalsIgnoreCase("post")) {
                br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer, splitbyte, BUFFER_SIZE - splitbyte)));
                line = "";
                while ((line = br.readLine()) != null) {
                    line = URLDecoder.decode(line,"UTF-8");
                    String parameters[] = line.split("&");
                    for (String parameter : parameters) {
                        String[] param = parameter.split("=");
                        params.put(param[0],param[1]);
                    }
                }
            }
            request.setParams(params);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return request;

    }

    private Integer findHeader(InputStream stream, byte buffer[]) {

        int read = 0;
        int headerEndIndex = 0;
        try {
            read = stream.read(buffer, 0, BUFFER_SIZE);
            while (headerEndIndex + 1 < read) {

                if (buffer[headerEndIndex] == '\n' &&
                        buffer[headerEndIndex + 1] == '\n') {
                    headerEndIndex += 2;
                    break;
                }

                if (buffer[headerEndIndex] == '\r' &&
                        buffer[headerEndIndex + 1] == '\n' &&
                        headerEndIndex + 3 < read &&
                        buffer[headerEndIndex + 2] == '\r' &&
                        buffer[headerEndIndex + 3] == '\n') {

                    headerEndIndex += 4;
                    break;
                }
                headerEndIndex++;
            }
        }
        catch (IOException e){

        }

        return headerEndIndex;

    }

    private void printBytes(byte buffer[]){
        for (int i = 0; i < buffer.length; i++) {
            System.out.print((char)buffer[i]);
        }
    }
    private Map<String, String> getRequestHeaders(BufferedReader br) {
        Map<String, String> headers = new HashMap<>();
        try {
            while (br.ready()) {
                String line = br.readLine();
                if (line.contains(":")) {
                    String headerArray[] = line.split(":");
                    headers.put(headerArray[0].trim(), headerArray[1].trim());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return headers;
    }

    private String getRequestURI(String line) {
        line = line.replaceAll("\\.\\./", "");
        Matcher matcher = DIR.matcher(line);
        if (matcher.find()) {
            String file = matcher.group(0);
            return file.trim();
        }

        return null;
    }

    private String getHTTPMethod(String method) {
        return Arrays.stream(HTTP_METHODS)
                .filter(method::equalsIgnoreCase)
                .findAny().orElseThrow(() -> new RuntimeException(method + " HTTP method not supported"));
    }


    private void sendRespond(OutputStream outputStream, String file) {

        OutputStreamWriter osw = new OutputStreamWriter(outputStream);
        PrintWriter bw = new PrintWriter(osw);
        File f = new File(ROOT_DIR + file);

        try {
            String lines = readFile(f);
            bw.println(generateResponse(OK) + "Connection: close\r\n");
            bw.println(lines);

        } catch (FileNotFoundException e) {
            bw.println(generateResponse(NOT_FOUND));

        } catch (IOException e) {
            e.printStackTrace();
        }


        bw.flush();
        bw.close();
    }

    private void sendError(OutputStream stream) {
        PrintWriter bw = new PrintWriter(new OutputStreamWriter(stream));
        bw.println(generateResponse(BAD_REQUEST) + "Connection: close\r\n");
        bw.close();
    }

    private String generateResponse(String code) {
        return String.format("%s %s\r\n", HTTP_VERSION, code);
    }

    private String readFile(File f) throws IOException {
        String lines = "";
        String line;
        BufferedReader br = new BufferedReader(new FileReader(f));
        while ((line = br.readLine()) != null) {
            lines += line;
        }
        return lines;
    }


}
