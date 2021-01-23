package app_kvClient;

import client.KVCommInterface;
import client.KVStore;
import logger.LogSetup;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class KVClient implements IKVClient {
    private static String KV_LOG_CONFIGURATION_DIR = "logs/KVclient.log";
    private static String PREFIX = "ms1-client";
    private Logger globalLogger = Logger.getRootLogger();
    private boolean serviceUp = true;
    public static void main(String[] args) {
        try {
            setUpLogger();
            new KVClient().serve();
        }

        catch (IOException e) {

        }
    }

    private KVStore storageConnection;
    @Override
    public void newConnection(String hostname, int port) throws Exception{
        // TODO Auto-generated method stub=
        // if there's already a connection, disconnect
        if (this.storageConnection != null) {
            this.storageConnection.disconnect();
        }
        this.storageConnection = new KVStore(hostname, port);
        this.storageConnection.connect();
    }

    @Override
    public KVCommInterface getStore(){
        // TODO Auto-generated method stub
        return this.storageConnection;
    }

    public void serve() {
        Scanner cmdline = new Scanner(System.in);
        while (this.serviceUp) {
            System.out.print("ms1-client>>>>>> ");
            String command = cmdline.nextLine();
            System.out.println("You typed: ..." + command);
            handleCommand(command);
        }
    }

    public void handleCommand(String command) {
        if (command.toLowerCase().trim().equals("quit") ||
                command.toLowerCase().trim().equals("q")) {
            System.out.println("<Bye>");
            globalLogger.info(buildInteractivePhrase("Application quit at: " + getCurrentTime()));
            this.serviceUp = false;
            return;
        }

    }
    private static void setUpLogger () throws IOException {
        new LogSetup(KV_LOG_CONFIGURATION_DIR, Level.ALL);
    }

    private String buildInteractivePhrase(String message) {
        return PREFIX + "> " + message;
    }

    private String getCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return dtf.format(LocalDateTime.now());
    }
}
