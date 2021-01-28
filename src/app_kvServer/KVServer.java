package app_kvServer;

import client.ClientConnWrapper;
import shared.CommunicationTextMessageHandler;
import shared.ConnWrapper;
import shared.messages.KVMessageModel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import shared.messages.KVMessage;

public class KVServer implements IKVServer {
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
		// TODO Auto-generated method stub
	}

	public static void main(String[] args) {
		int test_port = 5050;
		System.out.println("server started!");
		boolean running = true;
		ServerSocket test_socket;
		try {
			test_socket = new ServerSocket(5050);
		}

		catch (IOException e) {
			return;
		}
		while (running) {
			try {
				Socket client = test_socket.accept();
				ConnWrapper wrapper = new ClientConnWrapper(client);
				CommunicationTextMessageHandler handler = new CommunicationTextMessageHandler(wrapper);
				KVMessageModel msg = new KVMessageModel();
				msg.setStatusType(KVMessage.StatusType.GET_SUCCESS);
				msg.setKey("status");
				msg.setValue("GET_SUCCESS");
				handler.sendMsg(msg);
			}
			catch (IOException e) {
			}
		}
	}
	@Override
	public int getPort(){
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
    public String getHostname(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public CacheStrategy getCacheStrategy(){
		// TODO Auto-generated method stub
		return IKVServer.CacheStrategy.None;
	}

	@Override
    public int getCacheSize(){
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
    public boolean inStorage(String key){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
    public boolean inCache(String key){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
    public String getKV(String key) throws Exception{
		// TODO Auto-generated method stub
		return "";
	}

	@Override
    public void putKV(String key, String value) throws Exception{
		// TODO Auto-generated method stub
	}

	@Override
    public void clearCache(){
		// TODO Auto-generated method stub
	}

	@Override
    public void clearStorage(){
		// TODO Auto-generated method stub
	}

	@Override
    public void run(){
		// TODO Auto-generated method stub
	}

	@Override
    public void kill(){
		// TODO Auto-generated method stub
	}

	@Override
    public void close(){
		// TODO Auto-generated method stuisrunningb
	}
}
