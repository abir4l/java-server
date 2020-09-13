package lib.actions;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author abiral
 * created on 10/13/20
 * Actions specific to requests
 */

public interface Action {
//    public void dispatchAction(OutputStreamWriter streamWriter);
    String procesUrl(String url) throws IOException;
    String dispatchAction(String url) throws IOException;
}
