package app_kvServer.storage.persistence;

import app_kvServer.storage.IStorage;

public interface IPersistence extends IStorage {
    /**
     * Put the key-value pair into storage
     * @throws Exception
     *      when key not in the key range of the server
     */
    public void putAllKV(String key, String value) throws Exception;
}
