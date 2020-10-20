package lib.actions;

import app.HttpResponse;
import model.HttpRequest;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author abiral
 * created on 10/13/20
 * Actions specific to requests
 */

public interface Action {
//    public void dispatchAction(OutputStreamWriter streamWriter);
    HttpResponse procesUrl(String url) throws IOException;
    HttpResponse dispatchAction(HttpRequest request) throws IOException;
}
