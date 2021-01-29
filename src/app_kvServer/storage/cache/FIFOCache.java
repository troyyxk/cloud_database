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
    public String getKV(String key) throws KeyNotFoundException {
        if (map.containsKey(key)) {
            LinkedNode node = map.get(key);
            return node.value;
        } else {
            throw new KeyNotFoundException();
        }
    }

    @Override
    public void putKV(String key, String value) throws StorageFullException {
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
            removeNode(node);
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
    public void delete(String key) throws KeyNotFoundException {
        // No such key in cache
        if (!map.containsKey(key)) {
            throw new KeyNotFoundException();
        }
        LinkedNode node = map.get(key);
        removeNode(node);
        map.remove(key);
    }

    public String evict() {
        return head.next.key;
    }

    private void moveNodeToTail(LinkedNode node) {
        tail.prev.next = node;
        node.prev = tail.prev;
        node.next = tail;
        tail.prev = node;
    }

    private void removeNode(LinkedNode node) {
        LinkedNode prev = node.prev;
        node.next.prev = prev;
        prev.next = node.next;
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
}
