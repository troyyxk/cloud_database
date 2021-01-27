package shared;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ConnWrapper {
    void close() throws IOException;
    boolean isValid();
    InputStream inChannel();
    OutputStream outChannel();
}
