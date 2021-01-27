package shared;

import java.io.IOException;
import java.io.InputStream;

public interface ConnWrapper {
    void close() throws IOException;
    boolean isValid();
    InputStream toChannel();
}
