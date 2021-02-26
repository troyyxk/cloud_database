package app_kvServer;

import com.google.gson.JsonParseException;
import org.apache.log4j.Logger;
import shared.messages.MetaDataModel;

/**
 * Represents the state of current server,
 */
public class ServerState {
    private boolean running = false;
    private boolean writable = true;
    private MetaDataModel metadata = null;

    private static final String EMPTY_JSON_ARRAY = "[]";

    private static Logger logger = Logger.getRootLogger();

    public String getMetadata() {
        try {
            return MetaDataModel.ConvertModelToJson(metadata);
        } catch (JsonParseException ex) {
            logger.error("Cannot convert metadata to string" + ex.getMessage());
            return EMPTY_JSON_ARRAY;
        }
    }

    public void setMetadata(String metadata) {
        this.metadata = new MetaDataModel(metadata);
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
