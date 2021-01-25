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
            System.out.print("ms1-client> ");
            String command = cmdline.nextLine();
            System.out.println("You typed: ..." + command);
            handleCommand(command);
        }
    }

    public void handleCommand(String command) {

        String[] args = command.split("\\s+");
        if (args.length == 0) {
            unknown_respond();
        }
        String option = args[0];
        if (option.toLowerCase().trim().equals(CommandPhrase.QUIT.value()) ||
                command.toLowerCase().trim().equals("q")) {
            System.out.println("<Bye>");
            globalLogger.info(buildInteractivePhrase("Application quit at: " + getCurrentTime()));
            this.serviceUp = false;
            if (this.storageConnection != null) {
                this.storageConnection.disconnect();
            }
        }

        else if (option.trim().equals(CommandPhrase.CONNECT.value())) {
            if (args.length == 3) {
                String addr = args[1];
                try {
                    int port = Integer.parseInt(args[2]);
                    this.newConnection(addr, port);
                }

                catch (NumberFormatException e) {
                    globalLogger.error("The port number should be an integer");
                }

                catch (Exception e) {
                    globalLogger.error("Unknown host address error");
                }

            }

            else {
                globalLogger.error("Invalid number of arguments for connect");
            }
        }

        else if (option.trim().equals(CommandPhrase.DISCONNECT.value())) {
            if (args.length == 1) {
                this.storageConnection.disconnect();
            }

            else {
                globalLogger.error("Invalid number of args when disconnecting");
            }
        }

        else if (option.trim().equals(CommandPhrase.GET.value())) {

        }

        else if (option.trim().equals(CommandPhrase.PUT.value())) {

        }

        else if (option.trim().equals(CommandPhrase.HELP.value())) {
            if (args.length != 1) {
                globalLogger.error("No need to pass params to help command");
            }

            else {
                printHelpText();
            }
        }

        else if (option.trim().equals(CommandPhrase.LOG_LEVEL.value())) {
            String level = args[1];
            boolean isSuccessful = true;
            try {
                if (level.equals("ALL")) {
                    new LogSetup(KV_LOG_CONFIGURATION_DIR, Level.ALL);
                }

                else if (level.equals("DEBUG")) {
                    new LogSetup(KV_LOG_CONFIGURATION_DIR, Level.DEBUG);
                }

                else if (level.equals("INFO")) {
                    new LogSetup(KV_LOG_CONFIGURATION_DIR, Level.INFO);
                }

                else if (level.equals("WARN")) {
                    new LogSetup(KV_LOG_CONFIGURATION_DIR, Level.WARN);
                }

                else if (level.equals("ERROR")) {
                    new LogSetup(KV_LOG_CONFIGURATION_DIR, Level.ERROR);
                }

                else if (level.equals("FATAL")) {
                    new LogSetup(KV_LOG_CONFIGURATION_DIR, Level.FATAL);
                }

                else if (level.equals("OFF")) {
                    new LogSetup(KV_LOG_CONFIGURATION_DIR, Level.OFF);
                }

                else {
                    isSuccessful = false;
                    globalLogger.error("No such level option");
                }

                if (isSuccessful) {
                    System.out.println("Current Log status level: " + globalLogger.getLevel() + " logdir: " + globalLogger.getLoggerRepository());
                }
            }

            catch (IOException e) {
                globalLogger.error("log level setting failed!");
            }
        }

        else {
            unknown_respond();
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

    private void unknown_respond() {
        System.out.println("Unknown Command");
        printHelpText();
    }

    private void printHelpText() {
        globalLogger.info("Command options: \n " +
                "connect <server> <port>;\n" +
                             "disconnect\n" +
                "put <key> <value>\n" +
                "get <key>\n" +
                "help\n" +
                "quit");
    }
}
