package testing;
import app_kvClient.KVClient;
import app_kvECS.ECSClient;
import junit.framework.TestCase;
import org.junit.Test;
public class ECSClientTest extends TestCase {

    private ECSClient cli =new ECSClient("127.0.0.1", 2181, "ecs.config");
    @Test
    public void testEcsCliValid() {
        assertNull(validTestStatement("efaw", cli));
    }

    @Test
    public void testQuit() {
        assertNull(validTestStatement("start", cli));
    }

    @Test
    public void testAddNode() {
        assertNull(validTestStatement("addnode a 1000", cli));
    }

    @Test
    public void testRemoveNode() {
        assertNull(validTestStatement("removenode server1", cli));
    }

//    @Test
//    public void testShutdown() {
//        assertNull(validTestStatement("shutdown", cli));
//    }

    private Exception validTestStatement(String testStatement, ECSClient client) {
        Exception missingHandler = null;
        try {
            client.handleCommand(testStatement);
        }

        catch (Exception e) {
            missingHandler = e;
        }

        return missingHandler;
    }
}
