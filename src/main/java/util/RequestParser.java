package util;

import app.Connection;
import lib.requestType.RequestType;
import model.HttpRequest;
import model.HttpSession;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;

public class RequestParser {

    HttpRequest parseRequest(HttpSession session) throws IOException {

        InputStream stream = session.getConnection().getInputStream();
        byte buffer[] = new byte[Propertise.BUFFER_SIZE];
        int headerFinalIndex = findHeader(stream, buffer);
        if (headerFinalIndex != 0)
            return parseRequest(buffer, headerFinalIndex);
//        printBytes(buffer);
        return null;
    }

    private HttpRequest parseRequest(byte[] buffer, Integer splitbyte) {

        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer, 0, splitbyte)));
        HttpRequest request = null;
        try {
             request = extractMethodsAndRequestedResources(br);
            if (request.getHttpMethod().equalsIgnoreCase("post")) {
                if (!request.getHeaders().get("Content-Type").contains("multipart")) //Server doesn't support multipart-content type as of the moment.
                {
                    br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer, splitbyte, Propertise.BUFFER_SIZE - splitbyte)));
                    var params = extractParamsFromHeaders(br);
                    request.setParams(params);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return request;

    }

    private HttpRequest extractMethodsAndRequestedResources(BufferedReader br) throws IOException {

        HttpRequest request = new HttpRequest();
        String line = "";
        while ((line = br.readLine()) != null) {
            String methodArray[] = line.split(" ");
            try {
                request.setHttpMethod(getHTTPMethod(methodArray[0]));
                request.setRequestPath(getRequestURI(methodArray[1]));
                request.setRequestedEntity(getRequestedEntity(request.getRequestPath()));
                if (request.getRequestedEntity().isPresent())
                    request.setRequestType(processRequestedEntity(request.getRequestedEntity().get()));
                else
                    request.setRequestType(Optional.empty());
            } catch (ArrayIndexOutOfBoundsException ar) {
                throw new RuntimeException("HTTP header malformed");
            }
            request.setHeaders(getRequestHeaders(br));
            Connection.APPLICATION_STATE.logger("URL: " + request.getRequestPath() + " Method::" + request.getHttpMethod());

        }
        return request;
    }

    private Map<String, String> extractParamsFromHeaders(BufferedReader br) throws IOException {
        String line = "";
        Map<String, String> params = new HashMap<>();
        while ((line = br.readLine()) != null) {
            line = URLDecoder.decode(line, "UTF-8");
            String parameters[] = line.split("&");
            for (String parameter : parameters) {
                String[] param = parameter.split("=");
                params.put(param[0], param[1]);
            }
        }
        return params;

    }

    private Optional<RequestType> processRequestedEntity(File requestedEntity) {

        Objects.requireNonNull(requestedEntity);
        String fileName = requestedEntity.getName();

        if (requestedEntity.isDirectory())
            return Optional.of(RequestType.DIR);
        else if (fileName.endsWith(".html")) {
            return Optional.of(RequestType.WEBPAGE);
        } else
            return Optional.of(RequestType.FILE);

    }

    private Optional<File> getRequestedEntity(String requestPath) {
        File file = new File(Propertise.WEB + requestPath);
        if (file.exists())
            return Optional.of(file);
        return Optional.empty();
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

    private void printBytes(byte buffer[]) {
        char arr[] = new char[buffer.length];
        for (int i = 0; i < buffer.length; i++) {
            System.out.print((char) buffer[i]);
        }
    }

    private String getRequestURI(String line) {

        line = line.replaceAll("\\.\\./", "");
        Matcher matcher = Propertise.DIR.matcher(line);
        if (matcher.find()) {
            String file = matcher.group(0);
            file = file.equalsIgnoreCase("/") ? "/index.html" : file;
            return file.trim();
        }

        return null;
    }

    private String getHTTPMethod(String method) {
        return Arrays.stream(Propertise.HTTP_METHODS)
                .filter(method::equalsIgnoreCase)
                .findAny()
                .orElseThrow(() -> new RuntimeException(method + " HTTP method not supported"));
    }

    private Integer findHeader(InputStream stream, byte buffer[]) {

        int read = 0;
        int headerEndIndex = 0;
        try {
            read = stream.read(buffer, 0, Propertise.BUFFER_SIZE);
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
        } catch (IOException ignored) {
        }

        return headerEndIndex;

    }


}
