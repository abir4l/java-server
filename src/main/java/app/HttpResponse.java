package app;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    @Getter
    private Map<String,String> headers;
    @Getter
    private String mimeType;
    @Getter
    @Setter
    private InputStream data;
    @Setter
    @Getter
    private String textData;

    @Setter
    @Getter
    private String status;

    public HttpResponse() {
        this.headers = new HashMap<>();
    }

    public HttpResponse(String mime) {
        this();
        this.mimeType = mime;
    }

    public void addHeader(String key, String value){
        this.headers.put(key.toLowerCase(),value);
    }
}
