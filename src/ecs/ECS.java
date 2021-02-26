package ecs;

import app_kvECS.ECSClient;
import org.apache.log4j.Logger;
import shared.messages.MetaDataModel;
import shared.messages.Metadata;
import shared.util.ECSUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.TreeSet;

public class ECS {
    private static Logger globalLogger = Logger.getRootLogger();
    private String zHostname;
    private int zPort;
    private Metadata metaData;
    private TreeSet<IECSNode> managedServers = new TreeSet<>();
    private ECSzkWatcher watcher;
    private ECSClient client;

    public ECS(String zHostName, int zPort, String configFileName, ECSClient client) {
        loadWithFile(configFileName);
        this.metaData = new MetaDataModel();
        this.watcher = new ECSzkWatcher();
        this.client = client;
    }

    private void initECSNodes(TreeSet<IECSNode> nodes) {
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

    private void printError(String err) {
        globalLogger.error(err);
        System.out.println(err);
    }

    private void printInfo(String info) {
        globalLogger.info(info);
        System.out.println(info);
    }
}
