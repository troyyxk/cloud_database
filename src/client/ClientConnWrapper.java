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
    private static final int TIME_WAIT = 1000;

    public ClientConnWrapper(Socket rawSocket) throws IOException {
        this.rawSocket = rawSocket;
        if (isValid()) {
            this.inStream = rawSocket.getInputStream();
            this.outStream = rawSocket.getOutputStream();
        }

        else {
            this.rawSocket = null;
        }
    }
    @Override
    public void close() throws IOException {
        if (rawSocket == null) return;
        this.inStream.close();
        this.outStream.close();
        rawSocket = null;
    }

    @Override
    public boolean isValid() throws IOException {
        if(rawSocket == null) {
            return false;
        }
        boolean isV = this.rawSocket.getInetAddress().isReachable(TIME_WAIT);
        return isV;
    }

    @Override
    public InputStream inChannel() {
        return this.inStream;
    }

    @Override
    public OutputStream outChannel() {
        return this.outStream;
    }
}
