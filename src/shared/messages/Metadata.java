package shared.messages;

public interface Metadata {

    String predecessor(String agentName);
    String successor(String agentName);
    String subHashRange(String agentName);
    boolean isValid(); // if the metadata is valid
}
