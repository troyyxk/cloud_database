package shared;
import shared.messages.KVMessage;
import org.json.*;
import shared.messages.KVMessageModel;

import java.io.IOException;

public class CommunicationTextMessageHandler extends CommunicationSockMessageHandler {
    public CommunicationTextMessageHandler(ConnWrapper sockWrapper) {
        super(sockWrapper);
    }

    public void sendMsg(KVMessage msg) throws IOException {
        JSONObject jObj = new JSONObject(msg);
        String jsonData = jObj.toString();
        sendMsg(jsonData);
    }

    public KVMessage getKVMsg() throws IOException, JSONException, IllegalArgumentException {
        KVMessageModel model = new KVMessageModel();

        JSONObject json = new JSONObject(this.getMsg());
        model.setKey(json.getString("key"));
        model.setValue(json.getString("value"));
        model.setStatusType(KVMessage.StatusType.valueOf(json.getString("statusType")));
        return model;
    }
}
