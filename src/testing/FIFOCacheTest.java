package testing;
import app_kvServer.storage.StorageFullException;
import app_kvServer.storage.cache.FIFOCache;
import app_kvServer.storage.cache.ICache;
import org.junit.After;
import org.junit.Before;
import junit.framework.TestCase;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Iterator;

public class FIFOCacheTest extends TestCase{
    private ICache cache;
    private int cacheSize = 100;

    @BeforeClass
    public void setUp() throws Exception {
        cache = new FIFOCache(cacheSize);
    }

    @After
    public void tearDown() {
        cache.clear();
    }

    @Test
    public void testPutShouldContain() {
        String key = "key";
        String value = "value";

        Exception e = null;
        try {
            cache.putKV(key, value);
            assertTrue(cache.contains(key));
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
            cache.putKV(key, value);
            assertEquals(value, cache.getKV(key));
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
    }

    @Test
    public void testClearShouldEmpty() {
        Exception e = null;
        try {
            for (int i = 0; i < cacheSize; i++) {
                String key = "key" + i;
                String value = "value" + i;
                cache.putKV(key, value);
            }
            cache.clear();
            assertTrue(cache.isEmpty());
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
    }

    @Test
    public void testDeleteFromEmptyShouldThrowException() {
        Exception e = null;
        try {
            cache.delete("ECE419");
        } catch (Exception ex) {
            e = ex;
        }
        assertNotNull(e);
    }

    @Test
    public void testDeleteShouldNotContain() {
        String key = "key";
        String value = "value";
        Exception e = null;
        try {
            cache.putKV(key, value);
            cache.delete(key);
            assertFalse(cache.contains(key));
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
    }

    @Test
    public void testDeleteAllMemberShouldEmpty() {
        Exception e = null;
        try {
            for (int i = 0; i < cacheSize; i++) {
                String key = "key" + i;
                String value = "value" + i;
                cache.putKV(key, value);
            }
            for (int i = 0; i < cacheSize; i++) {
                String key = "key" + i;
                cache.delete(key);
            }
            assertTrue(cache.isEmpty());
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
    }

    @Test
    public void testCacheStrategyShouldBeFIFO() {
        assertEquals(ICache.CacheStrategy.FIFO, cache.getCacheStrategy());
    }

    @Test
    public void testGetCacheSizeShouldMatch() {
        assertEquals(cacheSize, cache.getCacheSize());
    }

    @Test
    public void testEvictShouldReturnFirstMember() {
        Exception e = null;
        try {
            for (int i = 0; i < cacheSize; i++) {
                String key = "key" + i;
                String value = "value" + i;
                cache.putKV(key, value);
            }
            assertEquals("key0", cache.evict());
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);

    }

    @Test
    public void testEvictOrderShouldBeFIFO() {
        Exception e = null;
        try {
            for (int i = 0; i < cacheSize; i++) {
                String key = "key" + i;
                String value = "value" + i;
                cache.putKV(key, value);
            }
            for (int i = 0; i < cacheSize; i++) {
                assertEquals("key" + i, cache.evict());
                cache.delete(cache.evict());
            }
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
    }

    @Test
    public void testGetUnknownKeyShouldThrowException() {
        Exception e = null;
        try {
            cache.getKV("ECE419");
        } catch (Exception ex) {
            e = ex;
        }
        assertNotNull(e);
    }

    @Test
    public void testPutToFullCacheShouldThrowException() {
        Exception e = null;
        try {
            for (int i = 0; i < cacheSize; i++) {
                String key = "key" + i;
                String value = "value" + i;
                cache.putKV(key, value);
            }
            cache.putKV("ECE419", "GPA 4.0");
        } catch (Exception ex) {
            e = ex;
        }
        assertNotNull(e);
    }


}
