package shared.messages;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import ecs.ECSNode;
import ecs.IECSNode;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MetaDataModel implements Metadata {
    private TreeSet<IECSNode> metaStruct;
    private boolean metaExists;
    public MetaDataModel(String metaData) {
        try {
            this.metaStruct = this.conertJsonToModel(metaData);
            this.metaExists = true;
        }

        catch (JsonParseException e) {
            System.out.println("Metadata format is invalid: " + e);
            this.metaExists = false;
        }
    }

    public void resetHashForMeta() {
        List<IECSNode> alias = new ArrayList<>(metaStruct);
        if (alias.size() == 0) return;
        ECSNode fNode = (ECSNode) alias.get(0);
        fNode.setStartHash(alias.get(alias.size() - 1).getNodeHashRange()[1]);
        for (int i = 1; i < alias.size(); i++) {
            ECSNode n = (ECSNode) alias.get(i);
            n.setStartHash(alias.get(i - 1).getNodeHashRange()[1]);
        }
        this.metaStruct = new TreeSet<>(alias);
    }

    @Override
    public String predecessor(String agentName) {
        List<IECSNode> iter = new ArrayList<>(metaStruct);
        for (int i = 0; i < iter.size(); i++) {
            if (iter.get(i).getNodeName().equals(agentName)) {
                return i == 0 ? iter.get(iter.size() - 1).getNodeName() :
                        iter.get(i - 1).getNodeName();
            }
        }

        return null;
    }

    @Override
    public String successor(String agentName) {
        List<IECSNode> iter = new ArrayList<>(metaStruct);
        for (int i = 0; i < iter.size(); i++) {
            if (iter.get(i).getNodeName().equals(agentName)) {
                return iter.get((i + 1) % iter.size()).getNodeName();
            }
        }
        return null;
    }

    @Override
    public String subHashRange(String agentName) {
        return null;
    }

    @Override
    public boolean isValid() {
        return this.metaExists;
    }

    public TreeSet<IECSNode> getMetaRaw() {
        return this.metaStruct;
    }

    private TreeSet<IECSNode> conertJsonToModel(String data) throws JsonParseException {
        // https://stackoverflow.com/questions/18397342/deserializing-generic-types-with-gson
        Type genericType = new TypeToken<TreeSet<ECSNode>>(){}.getType();
        return new Gson().fromJson(data, genericType);
    }
}
