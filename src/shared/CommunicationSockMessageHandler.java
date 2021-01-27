package shared;

import java.io.IOException;
import java.net.Socket;

// handle all possible socket message information and wrap them with certain protocolsssssss
public class CommunicationSockMessageHandler implements ICommunicationSockHandler {
    private Socket socket;
    public CommunicationSockMessageHandler(Socket rawSocket) {
        this.socket = rawSocket;
    }

    @Override
    public void sendMsg(String content) throws IOException {

    }

    @Override
    public String getMsg() throws IOException {
        return null;
    }
}
