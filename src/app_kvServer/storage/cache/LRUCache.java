package app_kvServer.storage.cache;

import app_kvServer.storage.KeyNotFoundException;
import app_kvServer.storage.StorageFullException;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LRUCache implements ICache{
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
    public LRUCache(int cacheSize) {
        this.cacheSize = cacheSize;
        this.map = new ConcurrentHashMap<>();
        this.head = new LinkedNode(null, null);
        this.tail = new LinkedNode(null, null);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    @Override
    public CacheStrategy getCacheStrategy() {
        return CacheStrategy.LRU;
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
    public String getKV(String key) throws Exception{
        if (!map.containsKey(key)) {
            throw new KeyNotFoundException();
        }
        LinkedNode node = map.get(key);
        // Move node to the tail
        node.prev.next = node.next;
        node.next = null;
        node.prev = tail;
        tail.next = node;
        tail = node;
        return node.value;
    }

    @Override
    public void putKV(String key, String value) throws Exception{
        if (!map.containsKey(key)) {
            if (map.size() >= cacheSize) {
                throw new StorageFullException();
            }
            LinkedNode node = new LinkedNode(key, value);
            addNewNode(node);
        } else {
            LinkedNode node = map.get(key);
            node.value = value;
            // Move node to the tail
            node.prev.next = node.next;
            moveNodeToTail(node);
        }
    }

    void addNewNode(LinkedNode node) {
        map.put(node.key, node);
        moveNodeToTail(node);
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

    @Override
    public String evict() {
        return head.next.key;
    }

    private void moveNodeToTail(LinkedNode node) {
        node.next = null;
        node.prev = tail;
        tail.next = node;
        tail = node;
    }

    boolean isEmpty() {
        return this.map.isEmpty();
    }

}
