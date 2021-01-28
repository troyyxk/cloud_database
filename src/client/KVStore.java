package client;

import org.apache.log4j.Logger;
import shared.CommunicationSockMessageHandler;
import shared.CommunicationTextMessageHandler;
import shared.messages.KVMessage;
import shared.messages.KVMessageModel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KVStore implements KVCommInterface {
	private static Logger globalLogger = Logger.getRootLogger();
	private ExecutorService kvStoreThreadPool = Executors.newFixedThreadPool(5);
	private String targetAddress;
	private int port;
	private ClientConnWrapper connWrapper = null;
	private static final int FINAL_TIMEOUT = 1_0000;
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
		System.out.println("connecting...");
		Socket newSocket = new Socket();
		newSocket.setSoTimeout(FINAL_TIMEOUT);
		newSocket.connect(new InetSocketAddress(this.targetAddress, this.port), FINAL_TIMEOUT);
		this.connWrapper = new ClientConnWrapper(newSocket);
		if (this.connWrapper.isValid()) {
			CommunicationTextMessageHandler handler = new CommunicationTextMessageHandler(this.connWrapper);
			KVMessage message = handler.getKVMsg();
			if (message.getStatus().toString().equals(KVMessage.StatusType.GET.toString())) {
				printInfo("Successfully get from server: " + message.getKey() + ": " + message.getValue());
				printInfo("Storage successfully connected to " + this.targetAddress + ":" + port);
			}

			else {
				printError("Connection might have been possessed or some server internal errors, please try again");
			}

		}

		else {
			printError("Connection is invalid!");
		}
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
		KVMessageModel kvMsg = new KVMessageModel();
		kvMsg.setKey(key);
		kvMsg.setStatusType(KVMessage.StatusType.GET);
		CommunicationTextMessageHandler handler = new CommunicationTextMessageHandler(this.connWrapper);
		handler.sendMsg(kvMsg);
		KVMessage res = handler.getKVMsg();
		return res;
	}

	private void printInfo(String info) {
		System.out.println(info);
		globalLogger.info(info);
	}

	private void printError(String err) {
		System.out.println(err);
		globalLogger.error(err);
	}

	private synchronized void closeSocket() {

		if (connWrapper == null) return;

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
