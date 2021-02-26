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

public class KVServer implements IKVServer, Runnable{

	private static Logger logger = Logger.getRootLogger();
	private int port;
	private List<KVClientConnection> connections = new ArrayList<>();
	private ServerSocket serverSocket;
	private boolean running;
	private DataAccessObject dao;

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
	}

	public void initZkListener(String zkHostname, int port, String serverName) {
		this.zkHostname = zkHostname;
		this.zkPort = port;
		this.serverName = serverName;
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
	}

	@Override
	public int getPort(){
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
	public CacheStrategy getCacheStrategy() {
		return dao.getCacheStrategy();
	}

	@Override
	public int getCacheSize() {
		return dao.getCacheSize();
	}

	@Override
	public boolean inStorage(String key) {
		return dao.inStorage(key);
	}

	@Override
	public boolean inCache(String key) {
		return dao.inStorage(key);
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
	public void clearCache() {
		dao.clearCache();
	}

	@Override
	public void clearStorage() {
		dao.clearStorage();
	}

	@Override
	// TODO: rename to start
    public void start(){
		this.dao.clearCache();
		running = initializeServer();

		if(serverSocket != null) {
			while(isRunning()){
				try {
					Socket client = serverSocket.accept();
					KVClientConnection connection =
							new KVClientConnection(client, dao);
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
		}
		logger.info("Server stopped.");
	}

	@Override
    public void shutDown(){
		running = false;
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

	}

	@Override
	public void unLockWrite() {

	}

	@Override
	public void moveData(byte[] range, String server) {

	}

	@Override
	public void update(String metadata) {

	}

	@Override
	// TODO: change to stop
	public void stop() {
		running = false;
		dao.flush();
		shutDown();
	}

	private boolean initializeServer() {
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

	private boolean isRunning() {
		return this.running;
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
