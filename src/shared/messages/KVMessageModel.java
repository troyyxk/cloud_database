package shared.messages;

import java.io.Serializable;

public class KVMessageModel implements KVMessage, Serializable {

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setStatusType(StatusType statusType) {
        this.statusType = statusType;
    }

    private String key;
    private String value;
    private StatusType statusType;

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public StatusType getStatus() {
        return this.statusType;
    }
}
