package app_kvServer.storage.cache;

import app_kvServer.IKVServer;
import app_kvServer.storage.IStorage;

import java.util.Map;

/**
 * A cache is an object to temporarily store data.
 */
public interface ICache extends IStorage {

    /**
     * A Linked List implementation. Used for Cache implementation
     * using FIFO or LRU strategy.
     */
    class LinkedNode {
        protected String key;
        protected String value;
        protected LinkedNode prev, next;

        LinkedNode(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }


    /**
     * Get the cache strategy of the server
     * @return  cache strategy
     */
    public IKVServer.CacheStrategy getCacheStrategy();

    /**
     * Get the cache size
     * @return  cache size
     */
    public int getCacheSize();


    /**
     * Clear Cache
     */
    public void clearCache();

    /**
     * Nominate a key for eviction to cache strategy
     * @return return a candidate key for eviction
     */
    public String evict();

    public boolean isEmpty();

}
