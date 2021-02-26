package app_kvECS;

import java.io.IOException;
import java.util.*;

import app_kvClient.KVClient;
import com.sun.source.tree.Tree;
import ecs.ECS;
import ecs.IECSNode;
import logger.LogSetup;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ECSClient implements IECSClient {
    private static final String ECS_LOG_CONFIGURATION_DIR = "logs/ecs.log";
    private static Logger globalLogger = Logger.getRootLogger();
    private ECS ecs;
    private boolean serviceUp = true;
    private static final List<String> strategies = new ArrayList<>();
    public ECSClient(String zkHost, int zkPort, String confFilename) {
        this.ecs = new ECS(zkHost, zkPort, confFilename, this);
    }

    static {
        strategies.add("LRU");
        strategies.add("LFU");
        strategies.add("FIFO");
    }

    @Override
    public boolean start() {
        return ecs.start();
    }

    @Override
    public boolean stop() {
        return ecs.stop();
    }

    @Override
    public boolean shutdown() {
        // TODO
        return false;
    }

    @Override
    public IECSNode addNode(String cacheStrategy, int cacheSize) {
        TreeSet<IECSNode> nodes = ecs.setupNewServers(1, cacheStrategy, cacheSize);
        return nodes.first();
    }

    @Override
    public Collection<IECSNode> addNodes(int count, String cacheStrategy, int cacheSize) {
        return ecs.setupNewServers(count, cacheStrategy, cacheSize);
    }

    @Override
    public Collection<IECSNode> setupNodes(int count, String cacheStrategy, int cacheSize) {
        return ecs.setupNewServers(count, cacheStrategy, cacheSize);
    }

    @Override
    public boolean awaitNodes(int count, int timeout) throws Exception {
        // TODO: count
        return ecs.awaitNodes(timeout);
    }

    @Override
    public boolean removeNodes(Collection<String> nodeNames) {
        return ecs.removeServers(nodeNames);
    }

    @Override
    public Map<String, IECSNode> getNodes() {
        TreeSet<IECSNode> nodes = ecs.getServers();
        Map<String, IECSNode> map = new TreeMap<>();
        for (Iterator<IECSNode> it = nodes.iterator(); it.hasNext(); ) {
            IECSNode node = it.next();
            map.put(node.getNodeName(), node);
        }
        return map;
    }

    @Override
    public IECSNode getNodeByKey(String Key) {
        // TODO
        return null;
    }

    private static void setUpLogger () throws IOException {
        new LogSetup(ECS_LOG_CONFIGURATION_DIR, Level.ALL);
    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Please provide your config file name");
            return;
        }
        // TODO
        try {
            setUpLogger();
            new ECSClient("127.0.0.1", 2181, args[0]).serve();
        }

        catch (IOException e) {

        }
    }

    public void serve() {
        Scanner cmdline = new Scanner(System.in);
        while (this.serviceUp) {
            System.out.print("ms2-ecs-client> ");
            String command = cmdline.nextLine();
            System.out.println("You typed: ..." + command);
            handleCommand(command);
        }
    }

    public void handleCommand(String command) {
        String[] args = command.split("\\s+");
        if (args.length == 0) {
            unknown_respond();
        }
        String option = args[0];
        if (option.equals("quit")) {
            serviceUp = false;
            this.shutdown();
            System.out.println("<bye>");
        }

        else if (option.equals("start")) {
            if (!this.start()) {
                printError("start of the server failed!");
            } else {
                System.out.println("Start successfully!");
            }
        }

        else if (option.equals("shutdown")) {
            if (!this.shutdown()) {
                printError("Unable to shutdown the servers");
            } else {
                System.out.println("Shutdown successfully!");
            }
        }

        else if (option.equals("stop")) {
            if (!this.stop()) {
                printError("Stop failed!");
            } else {
                System.out.println("Stop successfully!");
            }
        }

        else if (option.equals("addnode")) {
            if (args.length != 3) {
                printError("addNode <strategy> <cache_size>");
                return;
            }
            String strategy = args[1];
            if (!strategies.contains(strategy)) {
                printInfo("Unknown strategy, set default to None");
                strategy = "None";
            }

            int cacheSize = Integer.parseInt(args[2]);
            this.addNode(strategy, cacheSize);
        }

        else if (option.equals("addnodes")) {
            if (args.length != 4) {
                printError("addnodes <count> <strategy> <cache_size>");
                return;
            }
            int count = Integer.parseInt(args[1]);
            String strategy = args[2];
            if (!strategies.contains(strategy)) {
                printInfo("Unknown strategy, default to None");
                strategy = "None";
            }
            int cacheSize = Integer.parseInt(args[3]);
            this.addNodes(count, strategy, cacheSize);
        }

        else if (option.equals("removenode")) {
            if (args.length != 2) {
                printError("removenode <node_name>");
                return;
            }

            final String agentName = args[1];
            List<String> nodes = new ArrayList(){{add(agentName);}};
            if (!this.removeNodes(nodes)) {
                printError("Fail to remove one node!");
            }

            else {
                printInfo("Node " + agentName + " got successfully removed");
            }
        }
    }

    private void printInfo(String info) {
        System.out.println(info);
        globalLogger.info(info);
    }

    private void printError(String error) {
        System.out.println(error);
        globalLogger.error(error);
    }

    private void printHelpText() {
        String helpText =":::::ms2-ecs help:::::: \n Command options: \n " +
                "start: start servers\n" +
                "stop : stop servers\n" +
                "addnode <cacheStrategy> <cacheSize>\n" +
                "addnodes <number_of_nodes> <cacheStrategy> <cacheSize>\n" +
                "help: get the man page\n" +
                "quit: exit the program\n" +
                "loglevel <level>: ALL|DEBUG|INFO|WARN|ERROR|FATAL|OFF\n";
        printInfo(helpText);
    }

    private void unknown_respond() {
        printError("Unknown Command");
        printHelpText();
    }

}
