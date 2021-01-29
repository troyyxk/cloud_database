package testing;

import app_kvServer.DataAccessObject;
import app_kvServer.storage.cache.FIFOCache;
import app_kvServer.storage.cache.ICache;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class DAOTest extends TestCase{
    private DataAccessObject dao;
    private int cacheSize = 1000;
    private String strategy = "NONE";

    @BeforeClass
    public void setUp() throws Exception {
        this.dao = new DataAccessObject(cacheSize, strategy);
    }

    @After
    public void tearDown() {
        dao.clearStorage();
        dao.clearCache();
    }

    @Test
    public void testPutShouldContain() {
        String key = "key";
        String value = "value";

        Exception e = null;
        try {
            dao.putKV(key, value);
            assertTrue(dao.contains(key));
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
            dao.putKV(key, value);
            assertEquals(value, dao.getKV(key));
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
    }

    @Test
    public void testClearShouldEmpty() {
        Exception e = null;
        try {
            for (int i = 0; i < cacheSize * 2; i++) {
                String key = "key" + i;
                String value = "value" + i;
                dao.putKV(key, value);
            }
            dao.clearCache();
            dao.clearStorage();
            for (int i = 0; i < cacheSize * 2; i++) {
                String key = "key" + i;
                assertFalse(dao.contains(key));
            }
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
    }

    @Test
    public void testDeleteFromEmptyShouldThrowException() {
        Exception e = null;
        try {
            dao.delete("ECE419");
        } catch (Exception ex) {
            e = ex;
        }
        assertNotNull(e);
    }

    @Test
    public void testGetCacheSizeShouldMatch() {
        assertEquals(0, dao.getCacheSize());
    }

    @Test
    public void testGetUnknownKeyShouldThrowException() {
        Exception e = null;
        try {
            dao.getKV("ECE419");
        } catch (Exception ex) {
            e = ex;
        }
        assertNotNull(e);
    }
}
