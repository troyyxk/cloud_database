package testing;
import junit.framework.TestCase;
import org.junit.Test;
import app_kvClient.KVClient;


import static testing.AllTests.PORT;

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

    @Test
    public void testInvalidArgsLoglevel() {
        assertNull(validTestStatement("loglevel DEEE"));
    }

    @Test
    public void testValidStatement() {
        assertNull(validTestStatement("help"));
    }

    @Test
    public void testGetValidStatement() {
        assertNull(validTestStatement("get myKey"));
    }

    @Test
    public void testPutValidStatement() {
        assertNull(validTestStatement("put k v"));
    }

    @Test
    public void testConnectValidStatement() {
        assertNull(validTestStatement("connect 127.0.0.1 " + PORT));
    }

    @Test
    public void testValidLogLevel() {
        assertNull(validTestStatement("loglevel DEBUG"));
    }

    @Test
    public void testGetInvalid() {
        assertNull(validTestStatement("get m c"));
    }
}
