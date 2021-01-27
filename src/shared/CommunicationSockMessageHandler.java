package shared;

import java.net.Socket;

// handle all possible socket message information and wrap them with certain protocolsssssss
public class CommunicationSockMessageHandler {
    private Socket socket;
    public CommunicationSockMessageHandler(Socket rawSocket) {
        this.socket = rawSocket;
    }
}
