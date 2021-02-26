package app_kvServer.storage.cache;

import app_kvServer.IKVServer;
import app_kvServer.storage.KeyNotFoundException;
import app_kvServer.storage.StorageFullException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LRUCache implements ICache {
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
    public IKVServer.CacheStrategy getCacheStrategy() {
        return IKVServer.CacheStrategy.LRU;
    }

    @Override
    public int getCacheSize() {
        return this.map.size();
    }

    @Override
    public boolean contains(String key) {
        return map.containsKey(key);
    }

    @Override
    public String getKV(String key) throws KeyNotFoundException{
        if (!map.containsKey(key)) {
            throw new KeyNotFoundException();
        }
        LinkedNode node = map.get(key);
        // Move node to the tail
        moveNodeToTail(node);
        return node.value;
    }

    @Override
    public void putKV(String key, String value) {
        if (!map.containsKey(key)) {
            if (map.size() >= cacheSize) {
                String evict_key = evict();
                LinkedNode evict_node = map.get(evict_key);
                removeNode(evict_node);
                map.remove(evict_key);
            }
            LinkedNode node = new LinkedNode(key, value);
            addNewNode(node);
        } else {
            LinkedNode node = map.get(key);
            node.value = value;
            removeNode(node);
            moveNodeToTail(node);
        }
    }

    void addNewNode(LinkedNode node) {
        map.put(node.key, node);
        moveNodeToTail(node);
    }

    @Override
    public void clearCache() {
        head.next = tail;
        tail.prev = head;
        map.clear();
    }

    @Override
    public void delete(String key) throws KeyNotFoundException{
        // No such key in cache, nothing to delete
        if (!map.containsKey(key)) {
            throw new KeyNotFoundException();
        }
        LinkedNode node = map.get(key);
        removeNode(node);
        map.remove(key);
    }

    @Override
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
