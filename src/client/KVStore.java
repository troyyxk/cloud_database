package client;

import ecs.IECSNode;
import org.apache.log4j.Logger;
import shared.CommunicationSockMessageHandler;
import shared.CommunicationTextMessageHandler;
import shared.ConnWrapper;
import shared.messages.KVMessage;
import shared.messages.KVMessageModel;
import shared.messages.MetaDataModel;
import shared.messages.Metadata;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KVStore implements KVCommInterface {

	static class KVStorageMetaInfo {
		private String address;
		private int port;

		public CommunicationTextMessageHandler getNetwork() {
			return network;
		}

		public void setNetwork(CommunicationTextMessageHandler network) {
			this.network = network;
		}

		CommunicationTextMessageHandler network;

		KVStorageMetaInfo(String address, int port) {
			this.address = address;
			this.port = port;
		}

		public String getAddress() {
			return address;
		}

		public int getPort() {
			return port;
		}
	}
	private final static String DEFAULT_SERVER_NAME = "server1_0";
	private final static int PROCESS_LIMIT = 100;
	private static Logger globalLogger = Logger.getRootLogger();
	private ExecutorService kvStoreThreadPool = Executors.newFixedThreadPool(5);
	private String targetAddress;
	private int port;
	private ClientConnWrapper connWrapper = null;
	private static final int FINAL_TIMEOUT = 1_0000;
	private Map<String, KVStorageMetaInfo> storageMap = new HashMap<>();
	private Metadata metadata;
	/**
	 * Initialize KVStore with address and port of KVServer
	 * @param address the address of the KVServer
	 * @param port the port of the KVServer
	 */
	public KVStore(String address, int port) {
		this.targetAddress = address;
		this.port = port;
		globalLogger.info("New KvStore: " + address + ":"+ port + ">");
		this.storageMap.put(DEFAULT_SERVER_NAME, new KVStorageMetaInfo(address, port));
	}

	@Override
	public void connect() throws IOException, Exception {
//		System.out.println("connecting...");
//		Socket newSocket = new Socket();
//		newSocket.setSoTimeout(FINAL_TIMEOUT);
//		newSocket.connect(new InetSocketAddress(this.targetAddress, this.port), FINAL_TIMEOUT);
//		this.connWrapper = new ClientConnWrapper(newSocket);
//		if (this.connWrapper.isValid()) {
//			CommunicationTextMessageHandler handler = new CommunicationTextMessageHandler(this.connWrapper);
////			this.storageMap.put(DEFAULT_SERVER_NAME, handler);
//			// send to every lines
////			KVMessage message = handler.getKVMsg();
////			if (message.getStatus().toString().equals(KVMessage.StatusType.GET_SUCCESS.toString())) {
////				printInfo("Successfully get from server: " + message.getKey() + ": " + message.getValue());
////				printInfo("Storage successfully connected to " + this.targetAddress + ":" + port);
////			}
////
////			else {
////				printError("Connection might have been possessed or some server internal errors, please try again");
////			}
//			handleConnectedChannel(handler);
//		}
//
//		else {
//			printError("Connection is invalid!");
//		}
		if (this.storageMap.size() != 0) {
			Map.Entry<String, KVStorageMetaInfo> info = this.storageMap.entrySet().iterator().next();
			printInfo("Connecting to entry: " + info.getKey());
			this.connectToChannel(info.getValue());
		}
	}
	// TODO: migrate connect to this method
	private void connectToChannel(KVStorageMetaInfo info) throws IOException {

		System.out.println("connecting...");
		Socket newSocket = new Socket();
		newSocket.setSoTimeout(FINAL_TIMEOUT);
		newSocket.connect(new InetSocketAddress(info.getAddress(), info.getPort()), FINAL_TIMEOUT);
		ConnWrapper wrapper = new ClientConnWrapper(newSocket); // TODO: replace this.connWrapper to multiple wrapper in the map
		if (wrapper.isValid()) {
			CommunicationTextMessageHandler handler = new CommunicationTextMessageHandler(wrapper);
			info.setNetwork(handler);
//			this.storageMap.put(DEFAULT_SERVER_NAME, handler);
			// send to every lines
//			KVMessage message = handler.getKVMsg();
//			if (message.getStatus().toString().equals(KVMessage.StatusType.GET_SUCCESS.toString())) {
//				printInfo("Successfully get from server: " + message.getKey() + ": " + message.getValue());
//				printInfo("Storage successfully connected to " + this.targetAddress + ":" + port);
//			}
//
//			else {
//				printError("Connection might have been possessed or some server internal errors, please try again");
//			}
			handleConnectedChannel(handler);
		}

		else {
			printError("Connection is invalid!");
		}
	}

	private void handleConnectedChannel(CommunicationTextMessageHandler handler) throws IOException {
		KVMessage message = handler.getKVMsg();
		if (message.getStatus().toString().equals(KVMessage.StatusType.GET_SUCCESS.toString())) {
			printInfo("Successfully get from server: " + message.getKey() + ": " + message.getValue());
			printInfo("Storage successfully connected to " + this.targetAddress + ":" + port);
		}

		else {
			printError("Connection might have been possessed or some server internal errors, please try again");
		}
	}

	@Override
	public void disconnect() {
		closeSocket();
	}

	@Override
	public KVMessage put(String key, String value) throws Exception {
//		if (this.connWrapper == null) {
//			throw new IOException();
//		}
		KVMessageModel kvMsg = new KVMessageModel();
		kvMsg.setStatusType(KVMessage.StatusType.PUT);
		kvMsg.setKey(key);
		kvMsg.setValue(value);
		KVMessage res = processMetaAndGetResponse(kvMsg, 0);
//		CommunicationTextMessageHandler handler = new CommunicationTextMessageHandler(this.connWrapper);
//		handler.sendMsg(kvMsg);
//		KVMessage res = handler.getKVMsg();
		return res;
	}

	@Override
	public KVMessage get(String key) throws Exception {
		KVMessageModel kvMsg = new KVMessageModel();
		kvMsg.setKey(key);
		kvMsg.setValue("null");
		kvMsg.setStatusType(KVMessage.StatusType.GET);
//		CommunicationTextMessageHandler handler = new CommunicationTextMessageHandler(this.connWrapper);
//		handler.sendMsg(kvMsg);
//		KVMessage res = handler.getKVMsg();
		KVMessage res = processMetaAndGetResponse(kvMsg, 0);
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

		for (Map.Entry<String, KVStorageMetaInfo> infoSet: this.storageMap.entrySet()) {
			String name = infoSet.getKey();
			KVStorageMetaInfo info = infoSet.getValue();
			try {
				info.getNetwork().close();
			}

			catch (IOException e) {
				globalLogger.error("Previous socket for : " + name + " cannot be closed, maybe it's alreacy broken");
			}
		}

		this.storageMap = new HashMap<>();
		printInfo("All sockets have been successfully closed!");
	}

	private KVMessage processMetaAndGetResponse(KVMessage kvToProcess, int curTimes) {
		if (curTimes > PROCESS_LIMIT) {
			printError("Metadata updates too many times, something wrong might be happening to the server");
			return null;
		}
		String agentName = null;
		KVMessage result = null;

		try {
			if (metadata == null) {
				agentName = DEFAULT_SERVER_NAME;
				storageMap.get(agentName).getNetwork().sendMsg(kvToProcess);
				result = storageMap.get(agentName).getNetwork().getKVMsg();
			}

			else {
				IECSNode n = metadata.getNode(kvToProcess.getKey());
				if (n == null) {
					n = metadata.getMetaRaw().first();
				}
				printInfo("The server name chosen for this process is : " + n.getNodeName());
				agentName = n.getNodeName();

				if (!storageMap.containsKey(agentName) || storageMap.get(agentName) == null) {
					KVStorageMetaInfo newInfo = new KVStorageMetaInfo(n.getNodeHost(), n.getNodePort());
					connectToChannel(newInfo);
					this.storageMap.put(n.getNodeName(), newInfo);
				}
				this.storageMap.get(n.getNodeName()).getNetwork().sendMsg(kvToProcess);
				result = this.storageMap.get(n.getNodeName()).getNetwork().getKVMsg(); // get the procedure here
			}

			switch (result.getStatus()) {
				case SERVER_NOT_RESPONSIBLE:
					metadata = new MetaDataModel(result.getValue());
					return processMetaAndGetResponse(kvToProcess, curTimes + 1);
				default:
					break;
			}
		} catch (IOException e) {
			storageMap.remove(agentName);
			printInfo("Server" + " " + agentName + "  might be crashed, please choose another server");
		}
		return result;
	}
}
