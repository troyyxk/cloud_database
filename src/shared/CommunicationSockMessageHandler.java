package shared;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        byte[] rawData = content.getBytes();
        byte[] defReturn = new byte[]{LINE_FEED, C_RETURN};
        byte[] msg = new byte[rawData.length + defReturn.length];

        OutputStream oStream = this.sockWrapper.outChannel();
        System.arraycopy(rawData, 0, msg, 0, rawData.length);
        System.arraycopy(defReturn, 0, msg, rawData.length, defReturn.length);
        oStream.write(msg, 0, msg.length);
        oStream.flush();
        globalLogger.info("Message sent: " + content);
    }

    @Override
    public String getMsg() throws IOException {
        int index = 0;
        byte[] msgBytes = null, tmp = null;
        byte[] bufferBytes = new byte[BUFFER_SIZE];
        InputStream input = this.sockWrapper.inChannel();
        /* read first char from stream */
        byte read = (byte) input.read();
        boolean reading = true;

        while(read != 13 && reading) {/* carriage return */
            /* if buffer filled, copy to msg array */
            if(index == BUFFER_SIZE) {
                if(msgBytes == null){
                    tmp = new byte[BUFFER_SIZE];
                    System.arraycopy(bufferBytes, 0, tmp, 0, BUFFER_SIZE);
                } else {
                    tmp = new byte[msgBytes.length + BUFFER_SIZE];
                    System.arraycopy(msgBytes, 0, tmp, 0, msgBytes.length);
                    System.arraycopy(bufferBytes, 0, tmp, msgBytes.length,
                            BUFFER_SIZE);
                }

                msgBytes = tmp;
                bufferBytes = new byte[BUFFER_SIZE];
                index = 0;
            }

            /* only read valid characters, i.e. letters and numbers */
            if((read > 31 && read < 127)) {
                bufferBytes[index] = read;
                index++;
            }

            /* stop reading is DROP_SIZE is reached */
            if(msgBytes != null && msgBytes.length + index >= READ_LIMIT) {
                reading = false;
            }

            /* read next char from stream */
            read = (byte) input.read();
        }

        if(msgBytes == null){
            tmp = new byte[index];
            System.arraycopy(bufferBytes, 0, tmp, 0, index);
        } else {
            tmp = new byte[msgBytes.length + index];
            System.arraycopy(msgBytes, 0, tmp, 0, msgBytes.length);
            System.arraycopy(bufferBytes, 0, tmp, msgBytes.length, index);
        }

        msgBytes = tmp;
        /* build final String */
        String newMsg = new String(msgBytes);
        globalLogger.info("Receive message:\t '" + newMsg);
        return newMsg;
    }

    @Override
    public void close() throws IOException {
        if (this.sockWrapper == null) return;
        this.sockWrapper.close();
    }
}
