package ecs;

public class ECSNode implements IECSNode, Comparable<ECSNode> {
    private String agentName;
    private String startHash;
    private String endHash;
    private int cacheSize;
    private int port;
    private String hostName;

    @Override
    public String getNodeName() {
        return agentName;
    }

    @Override
    public String getNodeHost() {
        return this.hostName;
    }

    @Override
    public int getNodePort() {
        return this.port;
    }

    @Override
    public String[] getNodeHashRange() {
        return new String[0];
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getStartHash() {
        return startHash;
    }

    public void setStartHash(String startHash) {
        this.startHash = startHash;
    }

    public String getEndHash() {
        return endHash;
    }

    public void setEndHash(String endHash) {
        this.endHash = endHash;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Override
    public int compareTo(ECSNode o) {
        return this.endHash.compareTo(o.endHash);
    }
}
