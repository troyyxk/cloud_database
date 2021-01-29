package app_kvServer;

import java.io.IOException;
import java.net.*;

import app_kvServer.storage.cache.FIFOCache;
import app_kvServer.storage.cache.LFUCache;
import app_kvServer.storage.cache.LRUCache;
import logger.LogSetup;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import app_kvServer.storage.cache.ICache;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class KVServer extends Thread implements IKVServer {

	private static Logger logger = Logger.getRootLogger();

	private int port;

	private int cacheSize;

	private List<KVClientConnection> connections = new ArrayList<>();

	private ICache.CacheStrategy strategy;
	private ICache cache;
	private ServerSocket serverSocket;
	private boolean running;
	private DataAccessObject dao;

	/**
	 * For testing
	 * @param port
	 * @param cacheSize
	 * @param strategy
	 */
	public KVServer(int port, int cacheSize, String strategy) {
		this.port = port;
		this.dao = new DataAccessObject(cacheSize, strategy);
		try{
			this.strategy = ICache.CacheStrategy.valueOf(strategy);
		}catch (IllegalArgumentException ex) {
			this.strategy = ICache.CacheStrategy.None;
		}

	}

	/**
	 * Start KV Server at given port
	 * @param port given port for storage server to operate
	 */
	public KVServer(int port) {
		this.port = port;
		int cacheSize = 4096;
		String strategy = "None";
		this.dao = new DataAccessObject(cacheSize, strategy);
	}

	@Override
	public int getPort(){
		return port;
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
    public void run(){
		// TODO: uncomment the statement here if DAO finishes
		//this.dao.clearStorage();
		//this.dao.clearCache();
		running = initializeServer();

		if(serverSocket != null) {
			while(isRunning()){
				try {
					Socket client = serverSocket.accept();
					KVClientConnection connection =
							new KVClientConnection(client, dao);
					// TODO use thread pool
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
    public void kill(){
		// TODO Auto-generated method stub
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
    public void close() {
		// TODO save to persistent storage
		kill();
	}

	private boolean initializeServer() {
		logger.info("Initialize server ...");
		try {
			serverSocket = new ServerSocket(port);
			logger.info("Server listening on port: "
					+ serverSocket.getLocalPort());

		} catch (IOException e) {
			logger.error("Error! Cannot open server socket:");
			if(e instanceof BindException){
				logger.error("Port " + port + " is already bound!");
			}
			return false;
		}
		//TODO: fill the switch statement:

		switch (this.strategy) {
			case LRU:
				//TODO: fill it
				this.cache = new LRUCache(this.cacheSize);
				break;

			case FIFO:
				this.cache = new FIFOCache(this.cacheSize);
				break;
			case LFU:
				this.cache = new LFUCache(this.cacheSize);
				break;
			case None:
				this.cache = null;
				break;
			default:
				this.cache = null;
				break;

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
			if(args.length != 3) {
				printError("Please provide <port> <cacheSize> <strategy>");
				return;
			}

			int port = Integer.parseInt(args[0]);
			int cacheSize = Integer.parseInt(args[1]);
			KVServer server = new KVServer(port, cacheSize, args[2]);
			server.start();
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
}
