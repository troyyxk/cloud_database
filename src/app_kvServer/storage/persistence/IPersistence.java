package app_kvServer.storage.persistence;

import app_kvServer.storage.IStorage;
import app_kvServer.storage.KeyNotFoundException;

import java.io.IOException;

/**
 * A persistence storage interface. Represents
 * data store for disk.
 */
public interface IPersistence extends IStorage {
    public void clearStorage() throws Exception;

}
