package testing;
import com.google.gson.JsonParseException;
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

    @Test
    public void testIfDeserializationCorrect() {
        String testData = "[{agentName:\"testing\",\"endHash\":\"adwsdcc\"}]";
        MetaDataModel data = new MetaDataModel(testData);
        Exception ex = null;
        try {
            String serialized = MetaDataModel.ConvertModelToJson(data);
            MetaDataModel reDeserialized = new MetaDataModel(serialized);
            assertEquals("testing", reDeserialized.getMetaRaw().first().getNodeName());
        } catch (JsonParseException e) {
            ex = e;
        }
        assertNull(ex);

    }
}
