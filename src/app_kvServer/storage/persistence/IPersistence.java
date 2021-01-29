package app_kvServer.storage.persistence;

import app_kvServer.storage.IStorage;

import java.io.IOException;

public interface IPersistence extends IStorage {
    public void clearStorage() throws Exception;
}
