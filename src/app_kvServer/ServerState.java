package app_kvServer;

/**
 * Represents the state of current server,
 */
public class ServerState {
    private boolean running = false;
    private boolean writable = true;
    private String metadata = null;

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }
}
