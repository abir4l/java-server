package util;

import java.util.regex.Pattern;

public class Propertise {

    public static final Pattern DIR = Pattern.compile("/.*(\\s|)");
    public static final Integer BUFFER_SIZE = 8092;
    public static final String[] HTTP_METHODS = new String[]{"GET", "POST"};
    public static final String HTTP_VERSION = "HTTP/1.1";
}
