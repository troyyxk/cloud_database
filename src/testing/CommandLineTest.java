package testing;
import junit.framework.TestCase;
import org.junit.Test;
import app_kvClient.KVClient;
public class CommandLineTest extends TestCase {
    String invalidOperation = "xxxx";
    private Exception validTestStatement(String testStatement) {
        KVClient testClient = new KVClient();
        Exception missingHandler = null;
        try {
            testClient.handleCommand(testStatement);
        }

        catch (Exception e) {
            missingHandler = e;
        }

        return missingHandler;
    }
    @Test
    public void testInvalidOperation() {

        assertNull(validTestStatement(invalidOperation));
    }

    @Test
    public void testInvalidAddrPort() {
        assertNull(validTestStatement("connect 1 1"));
    }

    @Test
    public void testInvalidArgs() {
        assertNull(validTestStatement("put kk"));
    }
}
