package testing;

import app_kvServer.IKVServer;
import app_kvServer.storage.cache.ICache;
import app_kvServer.storage.cache.LFUCache;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class LFUCacheTest extends TestCase{
    private ICache cache;
    private int cacheSize = 5;

    @BeforeClass
    public void setUp() throws Exception {
        cache = new LFUCache(cacheSize);
    }

    @After
    public void tearDown() {
        cache.clearCache();
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
            cache.clearCache();
            assertTrue(cache.isEmpty());
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
    public void testCacheStrategyShouldBeLFU() {
        assertEquals(IKVServer.CacheStrategy.LFU, cache.getCacheStrategy());
    }

    @Test
    public void testGetCacheSizeShouldMatch() {
        assertEquals(0, cache.getCacheSize());
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
    public void testEvictOrderShouldBeLFU() {
        Exception e = null;
        try {
            for (int i = 0; i < cacheSize; i++) {
                String key = "key" + i;
                String value = "value" + i;
                cache.putKV(key, value);
            }
            // Should be in the addition order
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
    public void testLRUPut() {
        Exception e = null;
        try {
            for (int i = 0; i < cacheSize; i++) {
                String key = "key" + i;
                String value = "value" + i;
                cache.putKV(key, value);
            }
            cache.putKV("key0", "value0");
            assertEquals("key1", cache.evict());
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
    }

    @Test
    public void testLRUGet() {
        Exception e = null;
        try {
            for (int i = 0; i < cacheSize; i++) {
                String key = "key" + i;
                String value = "value" + i;
                cache.putKV(key, value);
            }
            cache.getKV("key0");
            assertEquals("key1", cache.evict());
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
    }

    @Test
    public void testLRUFull() {
        Exception e = null;
        try {
            for (int i = 0; i < cacheSize+2; i++) {
                String key = "key" + i;
                String value = "value" + i;
                cache.putKV(key, value);
            }
            assertEquals("key2", cache.evict());
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
    }

    @Test
    public void testLRUFullWithExtraPut() {
        Exception e = null;
        try {
            cache.putKV("key1", "value1");
            for (int i = 0; i < cacheSize+2; i++) {
                String key = "key" + i;
                String value = "value" + i;
                cache.putKV(key, value);
            }
            assertEquals("key3", cache.evict());
        } catch (Exception ex) {
            e = ex;
        }
        assertNull(e);
    }

}
