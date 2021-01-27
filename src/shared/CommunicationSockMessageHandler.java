package shared;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

// handle all possible socket message information and wrap them with certain protocolsssssss
public class CommunicationSockMessageHandler implements ICommunicationSockHandler {
    private ConnWrapper sockWrapper;
    private static Logger globalLogger = Logger.getRootLogger();
    private static final char C_RETURN = 13;
    private static final int BUFFER_SIZE = 4096;
    private static final char LINE_FEED = 10;
    private static final int READ_LIMIT = BUFFER_SIZE * BUFFER_SIZE / 4;
    public CommunicationSockMessageHandler(ConnWrapper sockWrapper) {
        this.sockWrapper = sockWrapper;
    }

    @Override
    public void sendMsg(String content) throws IOException {

    }

    @Override
    public String getMsg() throws IOException {
        return null;
    }
}
