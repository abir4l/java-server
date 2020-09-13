package model;

import lib.requestType.RequestType;
import lombok.Data;

import java.io.File;
import java.util.Map;
import java.util.Optional;

@Data
public class HttpRequest {

    private String httpMethod;
    private String requestPath;
    private Map<String,String> headers;
    private Map<String,String> params;
    private String version;
    private Optional<RequestType> requestType;
    private Optional<File> requestedEntity;


    public String getHttpMethod() {
        return httpMethod;
    }
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
    public HttpRequest() {
    }

    public HttpRequest(String httpMethod, String requestPath, Map<String, String> headers, Map<String, String> params) {
        this.httpMethod = httpMethod;
        this.requestPath = requestPath;
        this.headers = headers;
        this.params = params;
    }
}
