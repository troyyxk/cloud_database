package app_kvServer;
import java.io.IOException;
import java.net.*;
import logger.LogSetup;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// TODO: Add a threaded zookeeper listener

public class KVServer implements IKVServer, Runnable{

	private static Logger logger = Logger.getRootLogger();
	private int port;
	private List<KVClientConnection> connections = new ArrayList<>();
	private ServerSocket serverSocket;
	private DataAccessObject dao;

	private ServerState state;

	private int zkPort;
	private String zkHostname;
	private String serverName;
	/**
	 * For testing
	 * @param port port of Server
	 * @param cacheSize cache size of cache
	 * @param strategy strategies of cache, e.g. LRU, FIFO, etc
	 */
	public KVServer(int port, int cacheSize, String strategy) {
		this.port = port;
		this.dao = new DataAccessObject(cacheSize, strategy);
		this.state = new ServerState();
	}

	public void initZkListener(String zkHostname, int port, String serverName) {
		this.zkHostname = zkHostname;
		this.zkPort = port;
		this.serverName = serverName;
		this.state.setServerName(serverName);
		// TODO: initKVServer here??? Maybe ask Zk for server name
	}

	/**
	 * Start KV Server at given port
	 * @param port given port for storage server to operate
	 */
	public KVServer(int port) {
		this.port = port;
		int cacheSize = 4096;
		String strategy = "FIFO";
		this.dao = new DataAccessObject(cacheSize, strategy);
		this.state = new ServerState();
	}

	@Override
	public int getPort() {
		return this.port;
	}

	@Override
    public String getHostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.error("Error! " +
					"Unable to get hostname. \n", e);
			return null;
		}
	}

	@Override
	public String getKV(String key) throws Exception {
		return dao.getKV(key);
	}

	@Override
	public void putKV(String key, String value) throws Exception {
		dao.putKV(key, value);
	}

	@Override
    public void start() {
		state.setRunning(true);

		while(serverSocket != null) {
			try {
				Socket client = serverSocket.accept();
				KVClientConnection connection =
						new KVClientConnection(client, dao, state);
				new Thread(connection).start();
				connections.add(connection);
				logger.info("Connected to "
						+ client.getInetAddress().getHostName()
						+  " on port " + client.getPort());
			} catch (IOException e) {
				logger.error("Error! " +
						"Unable to establish connection. \n", e);
			}
		}
		logger.info("Server stopped.");
	}

	@Override
    public void shutDown(){
		state.setRunning(false);
		try {
			serverSocket.close();
			for (KVClientConnection cc : connections) {
				cc.close();
			}
		} catch (IOException e) {
			logger.error("Error! " +
					"Unable to close socket on port: " + port, e);
		}
	}

	@Override
	public void lockWrite() {
		state.setWritable(false);
	}

	@Override
	public void unLockWrite() {
		state.setWritable(true);
	}

	@Override
	public void moveData(String[] range, String server) {

	}

	@Override
	public void update(String metadata) {
		state.setMetadata(metadata);
	}

	@Override
	public void stop() {
		state.setRunning(false);
		dao.flush();
	}

	@Override
	public boolean initKVServer(String metadata) {
		state.setMetadata(metadata);

		logger.info("Initialize server ...");
		try {
			serverSocket = new ServerSocket(port);
			logger.info("Server connecting on port: "
					+ serverSocket.getLocalPort());

		} catch (IOException e) {
			logger.error("Error! Cannot open server socket:");
			if(e instanceof BindException){
				logger.error("Port " + port + " is already bound!");
			}
			return false;
		}
		return true;
	}

	/**
	 * Main entry point for the echo server application.
	 * @param args contains the port number at args[0].
	 */
	public static void main(String[] args) {
		try {
			new LogSetup("logs/KVserver.log", Level.ALL);
			if(args.length != 3 && args.length != 6) {
				printError("Please provide <port> <cacheSize> <strategy> <zk_host>? <zk_port>? <server_name>?");
				return;
			}
			if (args.length == 3) {
				int port = Integer.parseInt(args[0]);
				int cacheSize = Integer.parseInt(args[1]);
				KVServer server = new KVServer(port, cacheSize, args[2]);
				server.start();
			}

			else {
				int port = Integer.parseInt(args[0]);
				int cacheSize = Integer.parseInt(args[1]);
				KVServer server = new KVServer(port, cacheSize, args[2]);
				String zkHostName = args[3];
				int zkPort = Integer.parseInt(args[4]);
				String agentName = args[5];
				// TODO: might need to initKVServer here
				server.initZkListener(zkHostName, zkPort, agentName);
				server.start();
			}
		} catch (IOException e) {
			System.out.println("Error! Unable to initialize logger!");
			e.printStackTrace();
			System.exit(1);
		} catch (NumberFormatException nfe) {
			System.out.println("Error! Invalid argument <port>! Not a number!");
			System.out.println("Usage: Server <port>!");
			System.exit(1);
		}
	}

	private static void printError(String err) {
		logger.error(err);
		System.out.println(err);
	}

	@Override
	public void run() {
		this.start();
	}
}
