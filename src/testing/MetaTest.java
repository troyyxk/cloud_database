package testing;
import ecs.ECSNode;
import ecs.IECSNode;
import junit.framework.TestCase;
import org.junit.Test;
import shared.messages.MetaDataModel;
import shared.messages.Metadata;

import java.util.TreeSet;

public class MetaTest extends TestCase {
    @Test
    public void testIfSerilizationCorrect() {
        String testData = "[{agentName: \"testing\", \"endHash\": \"adwsdcc\"}]";
        Metadata data = new MetaDataModel(testData);
        assertEquals("testing", data.getMetaRaw().first().getNodeName());
    }

    private static final String[] servers = new String[] {"server1", "server2", "server3", "server4", "server5", "server6", "server7", "server8"};

    @Test
    public void predecessorTest() {
        Metadata dta = generateMeta();
        for (int i = 1; i < servers.length; i++) {
            String prev = dta.predecessor(servers[i]);
            assertEquals(servers[i - 1], prev);
        }
    }

    @Test
    public void testSuccessor() {
        Metadata dta = generateMeta();
        for (int i = servers.length - 2; i >= 0; i--) {
            String next = dta.successor(servers[i]);
            assertEquals(servers[i + 1], next);
        }
    }

    private Metadata generateMeta() {
        TreeSet<IECSNode> nodes = new TreeSet<>();
        for (int i = 0; i < servers.length; i++) {
            nodes.add(new ECSNode(servers[i], "127.0.0.1", 500003, Integer.toString(i)));
        }
        Metadata meta = new MetaDataModel(nodes);
        return meta;
    }

}
