package ecs;

import app_kvECS.ECSClient;
import org.apache.log4j.Logger;
import shared.messages.MetaDataModel;
import shared.util.ECSUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ECS {
    private static Logger globalLogger = Logger.getRootLogger();

    private static final String ROOT_PATH = "/ecs";

    private static final int TIME_OUT = 100000;

    private String zHostname;
    private int zPort;
    private MetaDataModel metaData;
    private TreeSet<IECSNode> managedServers = new TreeSet<>();
    private ECSzkWatcher watcher;
    private HashMap<String, ECSListener> listeners;
    private ECSClient client;

    public ECS(String zHostName, int zPort, String configFileName, ECSClient client) {
        loadWithFile(configFileName);
        this.metaData = new MetaDataModel();
        this.watcher = new ECSzkWatcher();
        this.client = client;
    }

    private void loadWithFile(String configFileName) {
        try {
            File configFile = new File(configFileName);
            Scanner scanner = new Scanner(configFile);
            while (scanner.hasNextLine()) {
                String serverInfo = scanner.nextLine();
                String[] n3 = serverInfo.split(" ");
                String serverName = n3[0];
                String address = n3[1];
                int port = Integer.parseInt(n3[2]);
                String aliasHashName = ECSUtil.generateServerHashName(address, port);
                String md5Alias = ECSUtil.convertStringtoMD5Value(aliasHashName);
                ECSNode node = new ECSNode();

                node.setEndHash(md5Alias);
                node.setAgentName(serverName);
                node.setHostName(address);
                node.setPort(port);
                managedServers.add(node);
            }
        }

        catch (FileNotFoundException e) {
            printError("No config file ready to launch the ecs!");
            printError(e.getMessage());
            System.exit(1);
        }

        catch (NoSuchAlgorithmException e) {
            printError(e.getMessage());
        }
    }

    public void processWhenNodeFail(IECSNode n) {
        ArrayList<String> list = new ArrayList<>();
        list.add(n.getNodeName());

        removeServers(list);
        listeners.remove(n.getNodeName());

        // TODO: Should we try to bring up server when node fail?
        client.addNodes(1, ((ECSNode) n).getStrategyName(), ((ECSNode) n).getCacheSize());
    }

    public void initECSNodes(TreeSet<IECSNode> nodes) {
        watcher.setSemaphore(nodes.size());
        for (IECSNode node : nodes) {
            ECSNode eNode = (ECSNode) node;
            String startScript = String.format("ssh -n %s nohup java -jar ./m2-server.jar %s %s %s %s %s %s &",
                    node.getNodeHost(), node.getNodePort(),
                    ((ECSNode) node).getCacheSize(),
                    ((ECSNode) node).getStrategyName(), zHostname, zPort,
                    node.getNodeName());
            try {
                ECSUtil.execCommand(startScript);
            }
            catch (IOException e) {
                printError("Failed to start the server: " + node.getNodeName());
            }
        }
    }

    public void broadcastMeta() {
        watcher.writeData(ROOT_PATH, MetaDataModel.ConvertModelToJson(metaData));
    }

    public void updateServerMeta() {
        printInfo("Updating metadata");
        watcher.setSemaphore(metaData.getMetaRaw().size());
        broadcastMeta();
        awaitNodes(TIME_OUT);

        printInfo("Finished metadata update");
    }

    public void updateServerData() {
        printInfo("Updating server data");

        watcher.setSemaphore(metaData.getMetaRaw().size());

        broadcastMeta();

        awaitNodes(TIME_OUT);

        printInfo("Finished server data update");
    }


    public boolean awaitNodes(int timeout) {
        return watcher.awaitNodes(timeout);
    }


    public TreeSet<IECSNode> setupNewServers(int count, String cacheStrategy, int cacheSize) {

        if (managedServers.size() < count) {
            printError("Do not have enough servers");
            return null;
        }


        TreeSet<IECSNode> list = new TreeSet<>();

        for (int i = 0; i < count; i++) {
            ECSNode node = (ECSNode) managedServers.pollFirst();
            node.setCacheSize(cacheSize);
            node.setStrategyName(cacheStrategy);
            list.add(node);
            metaData.addNode(node);
            watcher.watchNewNode(node.getNodeName());
        }

        metaData.resetHashForMeta();

        return list;
    }

    public boolean removeServers(Collection<String> nodeNames) {

        for (String name : nodeNames) {
            IECSNode node = metaData.removeNode(name);
        }

        metaData.resetHashForMeta();

        broadcastMeta();


        for (String name : nodeNames) {
            watcher.deleteNode(ROOT_PATH + "/" + name);
        }

        updateServerMeta();

        return true;
    }


    public TreeSet<IECSNode> getServers() {
        return metaData.getMetaRaw();
    }



    public boolean start() {

        printInfo("Starting servers");

        watcher.setSemaphore(metaData.getMetaRaw().size());

        broadcastMeta();

        awaitNodes(TIME_OUT);

        printInfo("All server started");

        return true;
    }

    public boolean stop() {
        broadcastMeta();
        return true;
    }

    public boolean shutdown() {
        removeListeners(metaData.getNameList());

        boolean flag = watcher.deleteAllNodes(metaData.getMetaRaw());
        watcher.releaseConnection();
        return flag;
    }

    /**
     * Following functions will interact with ECSDetector
     */

    public void addListeners(Collection<IECSNode> list) {
        for (IECSNode node : list) {

            ECSListener listener = new ECSListener(this, node);
            String nodeName = node.getNodeName();
            if (nodeName == null) {
                printError("Null here");
            }
            listeners.put(node.getNodeName(), listener);
            new Thread(listener).start();
        }
    }

    public void removeListeners(Collection<String> list) {
        for (String node : list) {
            ECSListener listener = listeners.remove(node);
            if (listener != null)
                listener.stop();
        }
    }

    private void printError(String err) {
        globalLogger.error(err);
        System.out.println(err);
    }

    private void printInfo(String info) {
        globalLogger.info(info);
        System.out.println(info);
    }
}
