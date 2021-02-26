package ecs;

import client.ClientConnWrapper;
import org.apache.log4j.Logger;
import shared.CommunicationTextMessageHandler;
import shared.ConnWrapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ECSListener implements Runnable {
    private static Logger globalLogger = Logger.getRootLogger();
    private CommunicationTextMessageHandler handler = null;
    private ECS ecs;
    private IECSNode node;
    private boolean isRunning = true;

    public ECSListener(ECS ecs, IECSNode node) {
        this.ecs = ecs;
        this.node = node;
    }

    @Override
    public void run() {
        Socket sock = new Socket();
        try {
            sock.connect(new InetSocketAddress(node.getNodeHost(), node.getNodePort()));
            ConnWrapper wrapper = new ClientConnWrapper(sock);
            if (wrapper.isValid()) {
                handler = new CommunicationTextMessageHandler(wrapper);
            }

            else {
                globalLogger.error("unable to establish effective connection to server " + node.getNodeName());
                return;
            }
        }

        catch (IOException e) {
            globalLogger.error("Unable to connect to server " + node.getNodeName());
            return;
        }

        try {
            while (isRunning) {
                handler.getKVMsg();
            }
        }

        catch (IOException e) {
            globalLogger.error("Connection failed suddenly for server: " + node.getNodeName());
            if (isRunning) {
                ecs.processWhenNodeFail(node);
                stop();
            }
        }

    }

    public void stop() {
        isRunning = false;
        try {
            handler.close();
        }

        catch (IOException e) {
            globalLogger.error("unable to stop the conn for server " + node.getNodeName() + " maybe it's alreadty broken");
        }
    }
}
