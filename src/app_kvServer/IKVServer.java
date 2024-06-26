package app_kvServer;

public interface IKVServer {
    public enum CacheStrategy {
        None,
        LRU,
        LFU,
        FIFO
    };

    /**
     * Get the port number of the server
     * @return  port number
     */
    public int getPort();

    /**
     * Get the hostname of the server
     * @return  hostname of server
     */
    public String getHostname();

    /**
     * Get the cache strategy of the server
     * @return  cache strategy
     */
    public CacheStrategy getCacheStrategy();

    /**
     * Get the cache size
     * @return  cache size
     */
    public int getCacheSize();

    /**
     * Check if key is in storage.
     * NOTE: does not modify any other properties
     * @return  true if key in storage, false otherwise
     */
    public boolean inStorage(String key);

    /**
     * Check if key is in storage.
     * NOTE: does not modify any other properties
     * @return  true if key in storage, false otherwise
     */
    public boolean inCache(String key);

    /**
     * Get the value associated with the key
     * @return  value associated with key
     * @throws Exception
     *      when key not in the key range of the server
     */
    public String getKV(String key) throws Exception;

    /**
     * Put the key-value pair into storage
     * @throws Exception
     *      when key not in the key range of the server
     */
    public void putKV(String key, String value) throws Exception;

    /**
     * Clear the local cache of the server
     */
    public void clearCache();

    /**
     * Clear the storage of the server
     */
    public void clearStorage();

    /**
     * Stops the KVServer, all client requests are rejected and
     * only ECS requests are processed
     */
    public void stop();

    /**
     * Starts the KVServer, all client requests and all ECS requests
     * are processed
     *
     */
    // TODO: Modify from run to start
    public void start();

    /**
     * Exits the KVServer application
     */
    // TODO: Modify from kill to shutdown
    public void shutDown();


    /**
     * Lock the KVServer for write operations
     */
    public void lockWrite();

    /**
     * Unlock the KVServer for write operations
     */
    public void unLockWrite();

    /**
     * Transfer a subset (range) of the KVServer’s data to
     * another KVServer (reallocation before removing this server
     * or adding a new KVServer to the ring);
     * send a notification to the ECS, if data transfer is completed
     * @param range
     * @param server
     */
    public void moveData(byte[] range, String server);

    /**
     * Update the metadata repository of this server
     * @param metadata
     */
    public void update(String metadata);

}
