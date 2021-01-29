package app_kvServer;
import app_kvServer.storage.*;
import app_kvServer.storage.cache.*;
import app_kvServer.storage.persistence.IPersistence;
import app_kvServer.storage.persistence.PStore;
import org.apache.log4j.Logger;

import java.io.IOException;

public class DataAccessObject implements IStorage {
    private ICache cache;
    private IPersistence disk;
    private static Logger logger = Logger.getRootLogger();

    public DataAccessObject(int cacheSize, String strategy) {
        switch (strategy) {
            case "LRU":
                this.cache = new FIFOCache(cacheSize);
                break;
            case "LFU":
                this.cache = new FIFOCache(cacheSize);
                break;
            case "FIFO":
                this.cache = new FIFOCache(cacheSize);
                break;
            default:
                this.cache = null;
        }
        this.disk = new PStore("./db.json");
    }

    @Override
    public boolean contains(String key) {
        if (hasCache()) {
            return this.cache.contains(key) || this.disk.contains(key);
        } else {
            return this.disk.contains(key);
        }

    }

    @Override
    public String getKV(String key) throws KeyNotFoundException {
        if (hasCache()) {
            try {
                return cache.getKV(key);
            } catch (Exception ex) {
                logger.debug("Cache miss for key: " + key);
            }
        }
        String value;
        try {
            value = disk.getKV(key);
        } catch (Exception ex) {
            logger.debug("Cannot find key in anywhere: " + key);
            throw new KeyNotFoundException();
        }
        // Add key-value pair back into cache
        try {
            this.putKV(key, value);
        } catch (Exception ex) {
            logger.error("Unable to add data back to cache");
        }

        return value;
    }

    @Override
    public void putKV(String key, String value) throws Exception {
        if (!hasCache()) {
            try {
                disk.putKV(key, value);
            } catch (Exception ex) {
                logger.error("Unable to put key in disk and there is no cache: " + key);
                throw new IOException(ex);
            }
            return;
        }

        try {
            cache.putKV(key, value);
        } catch (StorageFullException storeFullEx) {
            try {
                cacheEvict();
            } catch (IOException IOEx) {
                logger.error("Unable to evict cache: " + key);
                try {
                    disk.putKV(key, value);
                    logger.debug("Data put into disk due to cache eviction failur: " + key);
                    return;
                } catch (Exception ex) {
                    logger.error("Unable to put key in both disk and cache: " + key);
                    throw new IOException(ex);
                }
            }
            // Cache eviction succeeded, try put to cache again
            try {
                cache.putKV(key, value);
            } catch (Exception ex) {
                logger.error("Unable to add to cache even after eviction succeeded: " + key);
                try {
                    disk.putKV(key, value);
                    logger.debug("Unknown cache put failure: " + key);
                } catch (Exception cacheFailureEx) {
                    logger.error("Unable to put key in both disk and cache: " + key);
                    throw new IOException(cacheFailureEx);
                }
            }
        }

    }

    private void cacheEvict() throws IOException {
        String evictKey = cache.evict();
        String evictValue;
        try {
            evictValue = cache.getKV(evictKey);
        } catch (Exception ex) {
            logger.error("Unable to get value from stale key in cache: " + evictKey);
            throw new IOException();
        }

        // Put stale data into disk
        try {
            disk.putKV(evictKey, evictValue);
        } catch (Exception ex) {
            logger.error("Unable to store stale key into disk: " + evictKey);
            throw new IOException();
        }

        // Stale key stored into disk, delete from cache
        try {
            cache.delete(evictKey);
        } catch (Exception ex) {
            logger.error("Cannot delete stale key from cache: " + evictKey);
            throw new IOException();
        }
    }

    public void clearStorage(){
        try {
            this.disk.clearStorage();
        } catch (Exception ex) {
            logger.error("Unable to clear persistent storage");
        }
    }

    public void clearCache() {
        if (hasCache()) {
            cache.clearCache();
        }

    }

    @Override
    public void delete(String key) throws IOException {
        if (!contains(key)) {
            logger.error("Attemping to delete non-existing key: " + key);
            throw new IOException();
        }
        if (hasCache()) {
            try {
                cache.delete(key);
            } catch (KeyNotFoundException cacheKeyNotFoundEx) {
                logger.error("Unable to find key from cache: " + key);
            } catch (Exception ex) {
                logger.error("Unable to delete from cache");
                throw new IOException();
            }
        }
        try {
            disk.delete(key);
        } catch (KeyNotFoundException diskKeyNotFoundEx) {
            logger.error("Unable to find key from disk: " + key);
        } catch (Exception ioEx) {
            logger.error("Unable to delete from disk");
            throw new IOException();
        }
    }

    private boolean hasCache() {
        return cache != null;
    }

    /**
     * Get the cache strategy of the server
     * @return  cache strategy
     */
    public ICache.CacheStrategy getCacheStrategy() {
        if (hasCache()) {
            return cache.getCacheStrategy();
        } else {
            return ICache.CacheStrategy.None;
        }
    }

    /**
     * Get the cache size
     * @return  cache size
     */
    public int getCacheSize() {
        if (hasCache()) {
            return cache.getCacheSize();
        } else {
            // 0 representing no cache
            return 0;
        }
    }

}
