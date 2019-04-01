package util;

import http.HttpRequest;
import jdk.internal.util.xml.impl.Input;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {


    private static final String BAD_REQUEST = "400 BAD_REQUEST";
    private final String HTTP_VERSION = "HTTP/1.1";
    private final String NOT_FOUND = "404 NOT_FOUND";
    private final String OK = "200 OK";
    private final String ROOT_DIR = "/home/abiral/server-files";
    private final String[] HTTP_METHODS = new String[]{"GET", "POST"};
    private final Pattern DIR = Pattern.compile("/.*(\\s|)");


    private HttpRequest request;

    private Parser() {

    }

    public String getHeader(String key) {
        String value = this.request.getHeaders().get(key);
        return value;
    }

    public static void parse(Socket connection) {
        Parser parser = new Parser();
        parser.readRequest(connection);
    }

    private void readRequest(Socket connection) {
        // IO reading thread for non blocking.

        new Thread(() -> {
            try {
                request = parseRequest(connection.getInputStream());
                if (request == null)
                {
                    return;
//                  sendError(connection.getOutputStream());
//                    sendRespond(connection.getOutputStream(), "Test.java");
                }
                else
                sendRespond(connection.getOutputStream(), request.getRequestPath());
            } catch (SocketTimeoutException ste) {
                System.err.println("Timed out...");
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }).start();
    }

    private HttpRequest parseRequest(InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line = br.readLine();
        if (line == null) {
            return null;
        }
        String methodArray[] = line.split(" ");
        HttpRequest request = new HttpRequest();
        try {
            request.setHttpMethod(getHTTPMethod(methodArray[0]));
            request.setRequestPath(getRequestURI(methodArray[1]));
        } catch (ArrayIndexOutOfBoundsException ar) {
            throw new RuntimeException("HTTP header malformed");
        }
        request.setHeaders(getRequestHeaders(br));
        return request;


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
        System.out.println(headers);
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
