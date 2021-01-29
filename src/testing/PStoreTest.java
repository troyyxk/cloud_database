package testing;

import app_kvServer.storage.persistence.IPersistence;
import app_kvServer.storage.persistence.PStore;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class PStoreTest extends TestCase{
    private IPersistence disk;
    private int testSize = 100;

    @BeforeClass
    public void setUp() throws Exception {
        disk = new PStore("./db.txt");
    }

    @After
    public void tearDown() {
        try {
            disk.clearStorage();
        } catch (Exception ex) {

        }

    }

    @Test
    public void testPutThenCheckContains() {
        String key = "key";
        String value = "value";

        Exception e = null;
        try {
            disk.putKV(key, value);
            assertTrue(disk.contains(key));
            disk.clearStorage();
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
    }

    @Test
    public void testPutThenGetShouldEqual() {
        String key = "key";
        String value = "value";
        Exception e = null;
        try {
            disk.putKV(key, value);
            assertEquals(value, disk.getKV(key));
            disk.clearStorage();
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
    }

    @Test
    public void testClearShouldEmpty() {

        Exception e = null;
        try {
            for (int i = 0; i < testSize; i++) {
                String key = "key" + i;
                String value = "value" + i;
                disk.putKV(key, value);
            }
            disk.clearStorage();
            for (int i = 0; i < testSize; i++) {
                String key = "key" + i;
                assertFalse(disk.contains(key));
            }
            disk.clearStorage();
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
    }

    @Test
    public void testDeleteShouldNotContain() {
        String key = "key";
        String value = "value";
        Exception e = null;
        try {
            disk.putKV(key, value);
            disk.delete(key);
            assertFalse(disk.contains(key));
            disk.clearStorage();
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
    }

    @Test
    public void testGetUnknownKeyShouldThrowException() {
        Exception e = null;
        try {
            disk.getKV("ECE419");
            disk.clearStorage();
        } catch (Exception ex) {
            e = ex;
        }
        assertNotNull(e);
    }

}
