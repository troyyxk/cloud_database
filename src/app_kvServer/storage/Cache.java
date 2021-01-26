package app_kvServer.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache implements ICache {
    private int cacheSize;
	private CacheStrategy strategy;
	private Map<String, String> map = new ConcurrentHashMap<String, String>();

    /**
     * An object to temporarily store data.
     * @param cacheSize specifies how many key-value pairs the server is allowed
     *           to keep in-memory
     * @param strategy specifies the cache replacement strategy in case the cache
     *           is full and there is a GET- or PUT-request on a key that is
     *           currently not contained in the cache. Options are "FIFO", "LRU",
     *           and "LFU".
     */

	public Cache(int cacheSize, String strategy) {
	    this.cacheSize = cacheSize;
		switch (strategy) {
			case "LRU":
				this.strategy = CacheStrategy.LRU;
				break;
			case "LFU":
				this.strategy = CacheStrategy.LFU;
				break;
			case "FIFO":
				this.strategy = CacheStrategy.FIFO;
				break;
			default:
				this.strategy = CacheStrategy.None;
		}
		// TODO Implement cache using strategy pattern
    }

    @Override
    public CacheStrategy getCacheStrategy() {
        return this.strategy;
    }

    @Override
    public int getCacheSize() {
        return this.cacheSize;
    }

    @Override
    public boolean contains(String key) {
        return this.map.containsKey(key);
    }

    @Override
    public String getKV(String key){
        return map.get(key);
    }

	@Override
    public void putKV(String key, String value) throws Exception{
		// TODO implement cache eviction
		if (map.size() > cacheSize) {
			map.clear();
		}
		map.put(key, value);
	}

    @Override
    public void clear() {
        map.clear();
    }
}
