package app_kvServer;
import app_kvServer.storage.*;

public class DataAccessObject implements IStorage {
    // TODO should be static
    private static ICache cache;
    private IStorage disk;

    public DataAccessObject(int cacheSize, String strategy) {
        this.cache = new Cache(cacheSize, strategy);
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
}
