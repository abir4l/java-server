package util;

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
}
