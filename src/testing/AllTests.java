package testing;

import java.io.IOException;

import client.KVStore;
import org.apache.log4j.Level;

import app_kvServer.KVServer;
import junit.framework.Test;
import junit.framework.TestSuite;
import logger.LogSetup;

import javax.sound.sampled.Port;


public class AllTests {

	public static int PORT = 50000;

	static {
		try {
			new LogSetup("logs/testing/test.log", Level.ERROR);
			KVServer kvServer = new KVServer(PORT, 10, "FIFO");
			new Thread(kvServer).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static Test suite() {
		TestSuite clientSuite = new TestSuite("Basic Storage ServerTest-Suite");
		clientSuite.addTestSuite(ConnectionTest.class);
		clientSuite.addTestSuite(InteractionTest.class);
		clientSuite.addTestSuite(AdditionalTest.class);
		clientSuite.addTestSuite(PStoreTest.class);
		clientSuite.addTestSuite(FIFOCacheTest.class);
		clientSuite.addTestSuite(LRUCacheTest.class);
		clientSuite.addTestSuite(DAOTest.class);
		clientSuite.addTestSuite(CommandLineTest.class);
		return clientSuite;
	}
	
}
