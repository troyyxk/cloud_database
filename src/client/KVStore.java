package client;

import org.apache.log4j.Logger;
import shared.messages.KVMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KVStore implements KVCommInterface {
	private static Logger globalLogger = Logger.getRootLogger();
	private ExecutorService kvStoreThreadPool = Executors.newFixedThreadPool(5);
	private String targetAddress;
	private int port;
	private ClientConnWrapper connWrapper = null;
	/**
	 * Initialize KVStore with address and port of KVServer
	 * @param address the address of the KVServer
	 * @param port the port of the KVServer
	 */
	public KVStore(String address, int port) {
		// TODO Auto-generated method stub
		this.targetAddress = address;
		this.port = port;
		globalLogger.info("New KvStore: " + address + ":"+ port + ">");
	}

	@Override
	public void connect() throws IOException, Exception {
		// TODO Auto-generated method stub
		if (this.connWrapper != null) {
			closeSocket();
		}
		Socket newSocket = new Socket(this.targetAddress, this.port);
		this.connWrapper = new ClientConnWrapper(newSocket);
		printInfo("Storage successfully connected to " + this.targetAddress + ":" + port);
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		closeSocket();
	}

	@Override
	public KVMessage put(String key, String value) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KVMessage get(String key) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private void printInfo(String info) {
		System.out.println(info);
		globalLogger.info(info);
	}

	private synchronized void closeSocket() {
		try {
			connWrapper.close();
		}

		catch (IOException e) {
			globalLogger.error("Previous socket cannot be closed, maybe it's already broken");
		}

		finally {
			connWrapper = null;
		}
	}
}
