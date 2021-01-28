package app_kvServer;
import app_kvServer.storage.*;
import app_kvServer.storage.cache.*;
import app_kvServer.storage.persistence.IPersistence;

public class DataAccessObject implements IStorage {
    // TODO should be static
    private ICache cache;
    private IPersistence disk;

    public DataAccessObject(int cacheSize, String strategy) {
        switch (strategy) {
            case "LRU":
                this.cache = new FIFOCache(cacheSize);
                break;
            case "LFU":
                this.cache = new LFUCache(cacheSize);
                break;
            case "FIFO":
                this.cache = new LRUCache(cacheSize);
                break;
            default:
                this.cache = null;
        }
        // TODO implement persistence layer
    }

    @Override
    public boolean contains(String key) {
        return this.cache.contains(key) || this.disk.contains(key);
    }

    @Override
    public String getKV(String key) throws Exception {
        if (this.cache.contains(key)) {
            return this.cache.getKV(key);
        } else if (this.disk.contains(key)) {
            return this.disk.getKV(key);
            // TODO put value into cache depending on strateg;
        } else {
            throw new KeyNotFoundException();
        }
    }

    @Override
    public void putKV(String key, String value) throws Exception {
        this.cache.putKV(key, value);
        // TODO put value into persistence
    }

    @Override
    public void clear() {
        this.cache.clear();
        this.disk.clear();
    }

    @Override
    public void delete(String key) {

    }
}
