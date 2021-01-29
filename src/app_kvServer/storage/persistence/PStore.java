package app_kvServer.storage.persistence;

import java.io.*;
import java.security.KeyException;
import java.util.*;

import app_kvServer.storage.KeyNotFoundException;
import app_kvServer.storage.persistence.IPersistence;

import org.json.*;

/**
 * A persistence storage implemented using JSON libraries
 */
public class PStore implements IPersistence {
    private String fileAddress;
    public PStore(String fileAddress) {
        this.fileAddress = fileAddress;
    }

    public boolean contains(String key){
        try {
            JSONObject allPairs = readFile();
            return allPairs.has(key);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public String getKV(String key) throws KeyNotFoundException, IOException{
        if (!contains(key)){
            throw new KeyNotFoundException();
        }
        try {
            JSONObject allPairs = readFile();
            return allPairs.getString(key);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void putKV(String key, String value) throws IOException{
        try {
            JSONObject allPairs = readFile();
            allPairs.put(key, value);
            writeFile(allPairs);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void delete(String key) throws IOException {
        try {
            JSONObject allPairs = readFile();
            allPairs.remove(key);
            writeFile(allPairs);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    public JSONObject readFile() throws IOException {
        File store = new File(fileAddress);
        // check if file does not exist
        if (!(store.exists())) {
            String str = "{}";
            return new JSONObject(str);
        }
        String str = "{}";
        try {
            FileReader fr = new FileReader(store);
            BufferedReader br = new BufferedReader(fr);
            str = br.readLine();
            br.close();
            fr.close();
            return new JSONObject(str);
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    public void writeFile(JSONObject pairs) throws IOException{
        File store = new File(fileAddress);
        // check if file does not exist
        if (store.exists()) {
            store.delete();
            try {
                store.createNewFile();
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
        // write to file
        String allPairs = pairs.toString();
        try {
            FileWriter fw = new FileWriter(store, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(allPairs);
            bw.close();
            fw.close();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void clearStorage() throws IOException {
        try {
            File store = new File(fileAddress);
            store.delete();
        } catch (Exception ex) {
            throw new IOException(ex);
        }

    }
}