package app_kvServer.storage.cache;

import app_kvServer.storage.KeyNotFoundException;
import app_kvServer.storage.StorageFullException;

import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class FIFOCache implements ICache {
    private int cacheSize;
    private Map<String, LinkedNode> map;
    /**
     * Dummy nodes for ease of manipulation
     */
    private LinkedNode head, tail;

    /**
     * A cache object with LRU eviction strategy.
     * @param cacheSize specifies how many key-value pairs the server is allowed
     *           to keep in-memory
     */
    public FIFOCache(int cacheSize) {
        this.cacheSize = cacheSize;
        this.map = new ConcurrentHashMap<>(cacheSize);
        this.head = new LinkedNode(null, null);
        this.tail = new LinkedNode(null, null);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    @Override
    public CacheStrategy getCacheStrategy() {
        return CacheStrategy.FIFO;
    }

    @Override
    public int getCacheSize() {
        return this.cacheSize;
    }

    @Override
    public boolean contains(String key) {
        return map.containsKey(key);
    }

    @Override
    public String getKV(String key) throws Exception {
        if (map.containsKey(key)) {
            LinkedNode node = map.get(key);
            return node.value;
        } else {
            throw new KeyNotFoundException();
        }
    }

    @Override
    public void putKV(String key, String value) throws Exception {
        if (!map.containsKey(key)) {
            if (map.size() >= cacheSize) {
                throw new StorageFullException();
            }
            LinkedNode node = new LinkedNode(key, value);
            map.put(key, node);
            moveNodeToTail(node);
        } else {
            LinkedNode node = map.get(key);
            node.value = value;
            node.prev.next = node.next;
            moveNodeToTail(node);
        }
    }

    @Override
    public void clear() {
        head.next = tail;
        tail.prev = head;
        map.clear();
    }

    @Override
    public void delete(String key) {
        // No such key in cache, nothing to delete
        if (!map.containsKey(key)) {
            return;
        }
        LinkedNode node = map.get(key);
        node.prev.next = node.next;
        map.remove(key);
    }

    public String evict() {
        String key = head.next.key;
        return key;
    }

    private void moveNodeToTail(LinkedNode node) {
        node.next = null;
        node.prev = tail;
        tail.next = node;
        tail = node;
    }
}
