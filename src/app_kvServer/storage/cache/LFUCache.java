package app_kvServer.storage.cache;

import app_kvServer.storage.KeyNotFoundException;
import app_kvServer.storage.StorageFullException;

import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LFUCache implements ICache{
    private int cacheSize;
    /**
     * Head of the minFreqQueue is the minimum frequency,
     * tail of the minFreqQueue is the largest frequency,
     * makes eviction O(1)
     */
    private int minFreq;
    private Map<Integer, LRUCache> freqMap;
    private Map<String, FreqLinkedNode> keyMap;

    class FreqLinkedNode extends LinkedNode {
        protected int freq;
        FreqLinkedNode(String key, String value) {
            super(key, value);
            this.freq = 0;
        }
    }

    /**
     * A cache object with LFU eviction strategy.
     * @param cacheSize specifies how many key-value pairs the server is allowed
     *           to keep in-memory
     */
    public LFUCache(int cacheSize) {
        this.cacheSize = cacheSize;
        this.freqMap = new ConcurrentHashMap<>();
        /*
         Put dummy freq level for ease of manipulation.
         Less code if freqLinkedNode can start with 0 freq.
         */
        this.freqMap.put(0, new LRUCache(1));
        this.minFreq = 0;
        this.keyMap = new ConcurrentHashMap<>();
    }
    @Override
    public CacheStrategy getCacheStrategy() {
        return CacheStrategy.LFU;
    }

    @Override
    public int getCacheSize() {
        return this.cacheSize;
    }

    @Override
    public boolean contains(String key) {
        return keyMap.containsKey(key);
    }

    @Override
    public String getKV(String key) throws Exception {
        if (!keyMap.containsKey(key)) {
            throw new KeyNotFoundException();
        }
        FreqLinkedNode node = keyMap.get(key);
        incrFreq(node);
        return node.value;
    }

    void incrFreq(FreqLinkedNode node) {
        LRUCache freqKCache = freqMap.get(node.freq);
        freqKCache.delete(node.key);
        if (freqKCache.isEmpty()) {
            freqMap.remove(node.freq);
        }
        if (freqKCache.isEmpty() && node.freq == minFreq) {
            minFreq++;
        }
        Integer newFreq = node.freq + 1;
        node.freq = newFreq;
        LRUCache freqKPlusOneCache = freqMap.get(newFreq);
        if (freqKPlusOneCache == null) {
            freqKPlusOneCache = new LRUCache(Integer.MAX_VALUE);
            freqMap.put(newFreq, freqKPlusOneCache);
        }
        freqKPlusOneCache.addNewNode(node);
    }

    @Override
    public void putKV(String key, String value) throws Exception {
        if (!keyMap.containsKey(key)) {
            if (keyMap.size() >= cacheSize) {
                throw new StorageFullException();
            }
            FreqLinkedNode node = new FreqLinkedNode(key, value);
            incrFreq(node);
        } else {
            FreqLinkedNode node = keyMap.get(key);
            node.value = value;
            incrFreq(node);
            if (minFreq == 0) {
                minFreq = 1;
            }
        }
    }

    @Override
    public void clear() {
        this.freqMap.clear();
        /*
         Put dummy freq level for ease of manipulation.
         Less code if freqLinkedNode can start with 0 freq.
         */
        this.freqMap.put(0, new LRUCache(1));
        this.keyMap.clear();
        this.minFreq = 0;
    }

    @Override
    public void delete(String key) {
        FreqLinkedNode node = keyMap.get(key);
        keyMap.remove(key);
        LRUCache freqKCache = freqMap.get(node.freq);
        freqKCache.delete(key);
        if (freqKCache.isEmpty()) {
            freqMap.remove(node.freq);
        }
        if (freqKCache.isEmpty() && node.freq == minFreq) {
            setNextMinFreq();
        }
    }

    @Override
    public String evict() {
        LRUCache freqKCache = freqMap.get(minFreq);
        return freqKCache.evict();
    }

    private void setNextMinFreq() {
        Iterator<Integer> freqIter = freqMap.keySet().iterator();
        minFreq = Integer.MAX_VALUE;
        while (freqIter.hasNext()) {
            int freq = freqIter.next();
            if (minFreq > freq) {
                minFreq = freq;
            }
        }
        if (minFreq == Integer.MAX_VALUE) {
            minFreq = 0;
        }
    }
}
