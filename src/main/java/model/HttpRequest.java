package model;

import lombok.Data;

import java.util.Map;

@Data
public class HttpRequest {

    private String httpMethod;
    private String requestPath;
    private Map<String,String> headers;
    private Map<String,String> params;
    private String version;

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }


}
