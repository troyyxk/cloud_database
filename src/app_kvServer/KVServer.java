package app_kvServer;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import logger.LogSetup;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class KVServer extends Thread implements IKVServer {

	private static Logger logger = Logger.getRootLogger();

	private int port;
	private ServerSocket serverSocket;
	private boolean running;
	private DataAccessObject dao;


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

	/**
	 * Start KV Server at given port
	 * @param port given port for storage server to operate
	 * @param cacheSize specifies how many key-value pairs the server is allowed
	 *           to keep in-memory
	 * @param strategy specifies the cache replacement strategy in case the cache
	 *           is full and there is a GET- or PUT-request on a key that is
	 *           currently not contained in the cache. Options are "FIFO", "LRU",
	 *           and "LFU".
	 */
	public KVServer(int port, int cacheSize, String strategy) {
		this.port = port;
		this.dao = new DataAccessObject(cacheSize, strategy);
	}
	
	@Override
	public int getPort(){
		return port;
	}

	@Override
    public String getHostname(){
		String hostname = System.getenv("HOSTNAME");
		if (hostname == null) {
			hostname = "NullHostName";
		}
		return hostname;
	}

	@Override
    public void run(){
		running = initializeServer();

		if(serverSocket != null) {
			while(isRunning()){
				try {
					Socket client = serverSocket.accept();
					KVClientConnection connection =
							new KVClientConnection(client, dao);
					// TODO use thread pool
					new Thread(connection).start();

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
			return true;

		} catch (IOException e) {
			logger.error("Error! Cannot open server socket:");
			if(e instanceof BindException){
				logger.error("Port " + port + " is already bound!");
			}
			return false;
		}
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
			new LogSetup("logs/server.log", Level.ALL);
			if(args.length != 1) {
				int port = 8080;
				new KVServer(port).start();
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

}
