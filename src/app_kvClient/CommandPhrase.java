package app_kvClient;

public enum CommandPhrase {

    CONNECT("connect"),
    PUT("put"),
    LOG_LEVEL("logLevel"),
    HELP("help"),
    GET("get"),
    DISCONNECT("disconnect"),
    QUIT("quit");
    private String internalCommand;
    CommandPhrase(String command) {
        this.internalCommand = command;
    }

    public String value() {
        return internalCommand;
    }
}
