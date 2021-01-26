package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientConnWrapper {
    private Socket rawSocket;
    private InputStream inStream;
    private OutputStream outStream;

    public ClientConnWrapper(Socket rawSocket) throws IOException {
        this.rawSocket = rawSocket;
        this.inStream = rawSocket.getInputStream();
        this.outStream = rawSocket.getOutputStream();
    }

    public void close() throws IOException {
        if (rawSocket == null) return;
        this.inStream.close();
        this.outStream.close();
        rawSocket = null;
    }
}
