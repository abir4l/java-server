package util;

import model.HttpRequest;
import model.HttpSession;

import java.io.*;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class HeaderMethods {



    HttpRequest parseRequest(HttpSession session) throws IOException {
        InputStream stream = session.getConnection().getInputStream();
        byte buffer[] = new byte[Propertise.BUFFER_SIZE];
        int headerFinalIndex = findHeader(stream,buffer);
        if(headerFinalIndex != 0 )
            return parseHeader(buffer,headerFinalIndex);
        return null;
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
                if(!request.getHeaders().get("Content-Type").contains("multipart")) //Server doesn't support multipart-content type as of the moment.
                {
                    br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buffer, splitbyte, Propertise.BUFFER_SIZE - splitbyte)));
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

            }
            request.setParams(params);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        return headers;
    }

    private void printBytes(byte buffer[]){
        for (int i = 0; i < buffer.length; i++) {
            System.out.print((char)buffer[i]);
        }
    }

    private String getRequestURI(String line) {
        line = line.replaceAll("\\.\\./", "");
        Matcher matcher = Propertise.DIR.matcher(line);
        if (matcher.find()) {
            String file = matcher.group(0);
            return file.trim();
        }

        return null;
    }

    private String getHTTPMethod(String method) {
        return Arrays.stream(Propertise.HTTP_METHODS)
                .filter(method::equalsIgnoreCase)
                .findAny().orElseThrow(() -> new RuntimeException(method + " HTTP method not supported"));
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
        }
        catch (IOException e){

        }

        return headerEndIndex;

    }
    public String generateResponse(String code) {
        return String.format("%s %s\r\n", Propertise.HTTP_VERSION, code);
    }
    public String readFile(File f) throws IOException {
        String lines = "";
        String line;
        BufferedReader br = new BufferedReader(new FileReader(f));
        while ((line = br.readLine()) != null) {
            lines += line;
        }
        return lines;
    }
}
