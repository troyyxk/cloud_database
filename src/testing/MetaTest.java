package testing;
import junit.framework.TestCase;
import org.junit.Test;
import shared.messages.MetaDataModel;
import shared.messages.Metadata;

public class MetaTest extends TestCase {
    @Test
    public void testIfSerilizationCorrect() {
        String testData = "[{agentName: \"testing\", \"endHash\": \"adwsdcc\"}]";
        Metadata data = new MetaDataModel(testData);
        assertEquals("testing", data.getMetaRaw().first().getNodeName());
    }
}
