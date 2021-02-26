package app_kvServer.storage.cache;

import app_kvServer.IKVServer;
import app_kvServer.storage.KeyNotFoundException;
import app_kvServer.storage.StorageFullException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LFUCache implements ICache {
    private int cacheSize;
    /**
     * Head of the minFreqQueue is the minimum frequency,
     * tail of the minFreqQueue is the largest frequency,
     * makes eviction O(1)
     */
    private int minFreq;
    private HashMap<String, String> keyToVal;
    private HashMap<String, Integer> keyToCount;
    private HashMap<Integer, LinkedHashSet<String>> countToLRUKeys;

    class   FreqLinkedNode extends LinkedNode {
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
        this.minFreq = -1;
        this.cacheSize = cacheSize;
        this.keyToVal = new HashMap<>();
        this.keyToCount = new HashMap<>();
        this.countToLRUKeys = new HashMap<>();
    }

    @Override
    public IKVServer.CacheStrategy getCacheStrategy() {
        return IKVServer.CacheStrategy.LFU;
    }

    @Override
    public int getCacheSize() {
        return this.keyToVal.size();
    }

    @Override
    public boolean contains(String key) {
        return this.keyToVal.containsKey(key);
    }

    @Override
    public String getKV(String key) throws KeyNotFoundException {
        if(!keyToVal.containsKey(key)){
            throw  new KeyNotFoundException();
        }
        int count = keyToCount.get(key);
        keyToCount.put(key, count+1);
        countToLRUKeys.get(count).remove(key);
        if(count==minFreq && countToLRUKeys.get(count).size()==0) {
            minFreq++;
        }
        if(!countToLRUKeys.containsKey(count+1)) {
            countToLRUKeys.put(count + 1, new LinkedHashSet<>());
        }
        countToLRUKeys.get(count+1).add(key);
        return keyToVal.get(key);
    }


    @Override
    public String evict() {
        String evictString = countToLRUKeys.get(minFreq).iterator().next();
        // String evictString = (String) countToLRUKeys.get(minFreq).toArray()[countToLRUKeys.get(minFreq).size()-1];
        return evictString;
    }



    @Override
    public void putKV(String key, String value) throws StorageFullException {
        if(this.cacheSize <= 0){
            return;
        }
        if (keyToVal.containsKey(key)){
            keyToVal.put(key, value);
            try {
                getKV(key);
            } catch (KeyNotFoundException e) {
                System.out.println("Error finding key");
            }
        } else {
            if (keyToVal.size() >= this.cacheSize) {
                String evict_key = evict();
                countToLRUKeys.get(minFreq).remove(evict_key);
                keyToVal.remove(evict_key);
            }
            keyToVal.put(key, value);
            keyToCount.put(key, 1);
            minFreq = 1;
            if (!countToLRUKeys.containsKey(1)) {
                countToLRUKeys.put(1,new LinkedHashSet<>());
            }
            countToLRUKeys.get(1).add(key);
        }
    }

    @Override
    public void clearCache() {
        this.minFreq = -1;
        this.keyToVal.clear();
        this.keyToCount.clear();
        this.countToLRUKeys.clear();
    }

    @Override
    public void delete(String key) throws KeyNotFoundException{
        if (!keyToVal.containsKey(key)) {
            throw new KeyNotFoundException();
        }
        keyToVal.remove(key);
        int curFreq = keyToCount.get(key);
        keyToCount.remove(key);
        countToLRUKeys.get(curFreq).remove(key);
        if(curFreq == minFreq && countToLRUKeys.get(curFreq).size() == 0) {
            // find new minFreq
            setNextMinFreq();
        }
    }

    private void setNextMinFreq() {
        Iterator it = keyToCount.entrySet().iterator();
        minFreq = Integer.MAX_VALUE;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(minFreq > (int)pair.getValue()){
                minFreq = (int) pair.getValue();
            }
        }
        if (minFreq == Integer.MAX_VALUE) {
            minFreq = -1;
        }
    }

    @Override
    public boolean isEmpty() {
        return this.keyToVal.isEmpty();
    }
}
