package shared.messages;

import ecs.IECSNode;

import java.util.TreeSet;

public interface Metadata {

    String predecessor(String agentName);
    String successor(String agentName);
    String subHashRange(String agentName);
    TreeSet<IECSNode> getMetaRaw();
    IECSNode getNode(String key);
    void addNode(IECSNode node);
    boolean isValid(); // if the metadata is valid
}
