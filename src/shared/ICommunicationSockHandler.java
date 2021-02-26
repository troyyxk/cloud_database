package shared;

import java.io.IOException;

public interface ICommunicationSockHandler {
    void sendMsg(String content) throws IOException;
    String getMsg() throws IOException;
    void close() throws IOException;
}
