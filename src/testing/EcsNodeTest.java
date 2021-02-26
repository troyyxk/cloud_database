package testing;
import ecs.ECSNode;
import junit.framework.TestCase;
import org.junit.Test;

public class EcsNodeTest extends TestCase {
    private ECSNode generateECSNode() {
        ECSNode node = new ECSNode("test", "127.0.0.1", 50000, "1");
        node.setEndHash("10");
        return node;
    }

    @Test
    public void testNodeNameRetrieve() {
        ECSNode node = generateECSNode();
        String myName = node.getNodeName();
        assertEquals("test", myName);
    }

    @Test
    public void testHost() {
        ECSNode node = generateECSNode();
        String myHost = node.getNodeHost();
        assertEquals("127.0.0.1", myHost);
    }

    @Test
    public void testGetPort() {
        ECSNode node = generateECSNode();
        int myPort = node.getNodePort();
        assertEquals(50000, myPort);
    }
}
