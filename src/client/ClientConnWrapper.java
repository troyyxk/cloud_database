package client;

import shared.ConnWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientConnWrapper implements ConnWrapper {
    private Socket rawSocket;
    private InputStream inStream;
    private OutputStream outStream;

    public ClientConnWrapper(Socket rawSocket) throws IOException {
        this.rawSocket = rawSocket;
        this.inStream = rawSocket.getInputStream();
        this.outStream = rawSocket.getOutputStream();
    }
    @Override
    public void close() throws IOException {
        if (rawSocket == null) return;
        this.inStream.close();
        this.outStream.close();
        rawSocket = null;
    }

    @Override
    public boolean isValid() {
        return rawSocket != null;
    }

    @Override
    public InputStream toChannel() {
        return this.inStream;
    }
}
