package ecs;

public class ECSNode implements IECSNode, Comparable<ECSNode> {
    private String agentName;
    private String startHash;
    private String endHash;
    private int cacheSize;
    private int port;
    private String hostName;

    public ECSNode() {}
    public ECSNode(String agentName, String hostName, int port, String hashVal) {
        this.agentName = agentName;
        this.hostName = hostName;
        this.port = port;
        this.endHash = hashVal;
    }
    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    private String strategyName;

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
        return new String[]{this.startHash, this.endHash};
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

    public boolean md5MatchMe(String md5) {
        // if equals or between the clockwise location of start and end hash
        if (startHash.compareTo(endHash) >= 0) {
            return md5.compareTo(startHash) >= 0 || md5.compareTo(endHash) < 0;
        }

        else {
            return md5.compareTo(startHash) >= 0 && md5.compareTo(endHash) < 0;
        }
    }

    @Override
    public int compareTo(ECSNode o) {
        return this.endHash.compareTo(o.endHash);
    }
}
