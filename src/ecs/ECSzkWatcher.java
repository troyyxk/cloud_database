package ecs;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ECSzkWatcher {

    private static Logger globalLogger = Logger.getRootLogger();

    private ZooKeeper zk = null;
    private static final String ROOT_PATH = "/ecs";
    private static final int SESSION_TIMEOUT = 5000;
    private static final String CHILDREN_PATH = "/ecs/";
    private CountDownLatch connectedSemaphore = new CountDownLatch(1);

    private Watcher rootWatcher = null;

    private Watcher childrenWatcher = null;

    private CountDownLatch zkAgentsSemaphores;

    public void init(String zkHostname, int zkPort) {
        rootWatcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event == null) return;


                KeeperState keeperState = event.getState();

                EventType eventType = event.getType();

                printInfo("ROOT watcher triggered " + event.toString());

                if (keeperState == KeeperState.SyncConnected) {
                    switch (eventType) {
                        case None:
                            connectedSemaphore.countDown();
                            printInfo("Successfully connected to zookeeper server");
                            exists(ROOT_PATH, this);
                            break;
                        default:
                            break;
                    }

                } else {
                    printError("Failed to connect with zookeeper server -> root node");
                }
            }
        };

        childrenWatcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event == null) return;


                KeeperState keeperState = event.getState();

                EventType eventType = event.getType();

                String path = event.getPath();

                printInfo("Children watcher triggered " + event.toString());

                if (keeperState == KeeperState.SyncConnected) {
                    switch (eventType) {
                        case NodeCreated:
                            printInfo("Children Node Created at " + path);
                            zkAgentsSemaphores.countDown();
                            exists(path,this);
                            break;
                        case NodeDataChanged:
                            printInfo("Children Node signal received at " + path);
                            zkAgentsSemaphores.countDown();
                            exists(path,this);
                            break;
                        case NodeDeleted:
                            printInfo("Children Node Deleted at " + path);
                            zkAgentsSemaphores.countDown();
                            break;
                        default:
                            exists(path, this);
                            break;
                    }

                } else {
                    printError("Failed to connect with zookeeper server -> children node");
                }
            }
        };
        try {
            zk = new ZooKeeper(zkHostname + ":" + zkPort, SESSION_TIMEOUT, rootWatcher);

            connectedSemaphore.await();

            createPath(ROOT_PATH, "");

        } catch (Exception e) {
            printError("Failed to process KVServer Watcher " + e);
        }
    }


    public void setSemaphore(int serverNum) {
        this.zkAgentsSemaphores = new CountDownLatch(serverNum);
    }

    public Stat exists(String path, Watcher watch) {
        try {
            return this.zk.exists(path, watch);
        } catch (Exception e) {
            globalLogger.error(e.getMessage());
            return null;
        }
    }

    public void createPath(String path, String data) {
        try {
            this.zk.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            printInfo("Successfully create new node " + path);
        } catch (Exception e) {
            printError("Failed to create new node " + path);
        }
    }

    public boolean writeData(String path, String data) {
        try {
            this.zk.setData(path, data.getBytes(), -1);
            printInfo("Successfully update Node at " + path);
        } catch (Exception e) {
            printError("Failed to update Node at " + path);
            printError(e.getMessage());
            return false;
        }
        return true;
    }

    public void watchNewNode(String name) {

        exists(CHILDREN_PATH + name, childrenWatcher);

    }

    public boolean deleteNode(String path) {
        try {

            if (zk.exists(path, false) != null) {
                this.zk.delete(path, -1);
            }

            return true;

        } catch (Exception e) {
            printError("Failed to delete Node at path " + path);
            return false;
        }
    }

    public boolean awaitNodes(int timeout) {

        boolean ifNotTimeout = true;

        try {
            ifNotTimeout = zkAgentsSemaphores.await(timeout, TimeUnit.MILLISECONDS);
            if(ifNotTimeout)
                printInfo("All Server Signal received.");
            else
                printError("Timeout while waiting Server Signal.");

        } catch (InterruptedException e) {
            printError("Await Nodes has been interrupted!");
        }
        return ifNotTimeout;
    }


    public boolean deleteAllNodes(TreeSet<IECSNode> serverRepoTaken) {

        printInfo("Deleting all nodes");

        try {
            if (this.zk.exists(ROOT_PATH, false) == null)
                return true;

            for (IECSNode node : serverRepoTaken) {
                int i = 0;

                while(exists(CHILDREN_PATH + node.getNodeName(),null) != null &&
                        !deleteNode(CHILDREN_PATH + node.getNodeName())) {
                    deleteNode(CHILDREN_PATH + node.getNodeName() + "/server" + i);
                    i++;
                }
            }

            deleteNode(ROOT_PATH);

            printInfo("Done");
            return true;
        } catch (Exception e) {
            printError("Cannot Do ZK operation");
            return false;
        }
    }

    public void releaseConnection() {
        if (this.zk != null) {
            try {
                this.zk.close();
            } catch (InterruptedException e) {
                printError(e.getMessage());
            }
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
