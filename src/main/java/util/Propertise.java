package util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class Propertise {

    public static final Pattern DIR = Pattern.compile("/.*(\\s|)");
//    public static final Pattern WEB = Pattern.compile("/home/abiral/server-files");
    public static final Pattern WEB = Pattern.compile("./src/resources/webapp");
    public static final Integer BUFFER_SIZE = 8092;
    public static final Integer POOL_SIZE = 100;
    public static final String[] HTTP_METHODS = new String[]{"GET", "POST"};
    public static final String HTTP_VERSION = "HTTP/1.1";
    public static final ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
    public static final Map<String,String> mimeType = new HashMap<>();
    static{
                mimeType.put("htm","text/html");
                mimeType.put("html","text/html");
                mimeType.put("txt","text/plain");
                mimeType.put("asc","text/plain");
                mimeType.put("gif","image/gif");
                mimeType.put("jpg","image/jpeg");
                mimeType.put("jpeg","image/jpeg");
                mimeType.put("png","image/png");
                mimeType.put("mp3","audio/mpeg");
                mimeType.put("m3u","audio/mpeg-url");
                mimeType.put("pdf","application/pdf");
                mimeType.put("doc","application/msword");
                mimeType.put("ogg","application/x-ogg");
                mimeType.put("zip","application/octet-stream");
                mimeType.put("exe","application/octet-stream");
                mimeType.put("class","application/octet-stream");


    }
    public static final String MIME_HTML = "text/html", MIME_PLAINTEXT = "text/plain", MIME_DEFAULT_BINARY = "application/octet-stream";
}
