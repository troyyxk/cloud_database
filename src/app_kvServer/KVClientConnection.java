package app_kvServer;

import client.ClientConnWrapper;
import org.apache.log4j.Logger;
import shared.CommunicationTextMessageHandler;
import shared.ConnWrapper;
import shared.messages.KVMessageModel;

import java.io.IOException;
import java.net.Socket;
import shared.messages.KVMessage;

/**
 * Represents a connection end point for a particular client that is 
 * connected to the server. This class is responsible for message reception 
 * and sending. 
 * The class also implements the echo functionality. Thus whenever a message 
 * is received it is going to be echoed back to the client.
 */
public class KVClientConnection implements Runnable {

	private static Logger logger = Logger.getRootLogger();

	private boolean isOpen;
	private static final int BUFFER_SIZE = 1024;
	private static final int DROP_SIZE = 128 * BUFFER_SIZE;

	private ConnWrapper socketWrapper;
	private DataAccessObject dao;
	private ServerState serverState;

	/**
	 * Constructs a new ClientConnection object for a given TCP socket.
	 * @param clientSocket the Socket object for the client connection.
	 */
	public KVClientConnection(Socket clientSocket, DataAccessObject dao, ServerState serverState) throws IOException {
		this.socketWrapper = new ClientConnWrapper(clientSocket);
		this.isOpen = true;
		this.dao = dao;
		this.serverState = serverState;
	}

	
	/**
	 * Initializes and starts the client connection. 
	 * Loops until the connection is closed or aborted by the client.
	 */
	public void run() {
		CommunicationTextMessageHandler textComm = new CommunicationTextMessageHandler(this.socketWrapper);
		KVMessageModel successMsg = new KVMessageModel();
		successMsg.setValue("Successfully established connection");
		successMsg.setKey("status");
		successMsg.setStatusType(KVMessage.StatusType.GET_SUCCESS);
		try {
			textComm.sendMsg(successMsg);
			while(isOpen && serverState.isRunning()) {
				try {
					KVMessage msg = textComm.getKVMsg();
					KVMessageModel returnResult = new KVMessageModel();
					/*
						Handle GET request
					 */
					if (msg.getStatus().equals(KVMessage.StatusType.GET)) {
						String key = msg.getKey();
						String value;
						// TODO: check whether in this server's hash range
						try {
							System.out.println("get request!");
							value = dao.getKV(key);
							returnResult.setKey(msg.getKey());
							returnResult.setValue(value);
							returnResult.setStatusType(KVMessage.StatusType.GET_SUCCESS);
						} catch (Exception kex) {
							logger.error("GET_ERROR key: " + key);
							returnResult.setKey(key);
							returnResult.setStatusType(KVMessage.StatusType.GET_ERROR);
						}
						textComm.sendMsg(returnResult);
					}
					/*
						Handle PUT request
					 */
					else if (msg.getStatus().equals(KVMessage.StatusType.PUT)) {
						System.out.println("put request!");
						String key = msg.getKey();
						String value = msg.getValue();
						/*
							Server write lock
						 */
						if (!serverState.isWritable()) {
							returnResult.setStatusType(KVMessage.StatusType.SERVER_WRITE_LOCK);
						}
						/*
							PUT request with null value means deletion
						 */
						else if (value == null || value.equals("NULL") || value.equals("null")) {
							try {
								dao.delete(key);
								returnResult.setStatusType(KVMessage.StatusType.DELETE_SUCCESS);
							} catch (IOException IOEx){
								logger.error("DELETE_ERROR key: " + key);
								returnResult.setStatusType(KVMessage.StatusType.DELETE_ERROR);
							}
						}
						/*
							PUT request, either update or add new entry
						 */
						else {
							/*
								Update request
							 */
							if (dao.contains(key)) {
								try {
									dao.putKV(key, value);
									returnResult.setStatusType(KVMessage.StatusType.PUT_UPDATE);
								} catch (Exception ex) {
									logger.error("UPDATE_ERROR key: " + key);
									returnResult.setStatusType(KVMessage.StatusType.PUT_ERROR);
								}
							}
							/*
								New entry request
							 */
							else {
								try {
									dao.putKV(key, value);
									returnResult.setStatusType(KVMessage.StatusType.PUT_SUCCESS);
								} catch (Exception ex) {
									logger.error("PUT_ERROR key: " + key);
									returnResult.setStatusType(KVMessage.StatusType.PUT_ERROR);
								}
							}
						}
						returnResult.setKey(key);
						returnResult.setValue(value);
						textComm.sendMsg(returnResult);
					}

				/* connection either terminated by the client or lost due to 
				 * network problems*/	
				} catch (IOException ioe) {
					logger.error("Error! Connection lost!");
					isOpen = false;
				}
			}
			
		} catch (IOException ioe) {
			logger.error("Error! Connection could not be established!", ioe);
			successMsg.setStatusType(KVMessage.StatusType.GET_ERROR);
			try {
				textComm.sendMsg(successMsg);
			}
			catch (IOException e) {
				logger.error("Pipes broken completely, unable to send feedback to server!");
			}
			
		} finally {
			
			try {
				if (this.socketWrapper != null) {
					this.socketWrapper.close();
				}
			} catch (IOException ioe) {
				logger.error("Error! Unable to tear down connection!", ioe);
			}
		}
	}

	public synchronized void close() throws IOException {
		this.socketWrapper.close();
		this.isOpen = false;
		this.socketWrapper = null;
	}
}
