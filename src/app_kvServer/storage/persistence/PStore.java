package app_kvServer.storage.persistence;

import java.io.*;
import java.util.*;

import app_kvServer.storage.persistence.IPersistence;
import org.json.*;

import static java.lang.System.exit;
import static java.lang.System.out;

public class PStore implements IPersistence {
    private String fileAddress = "./PStore.txt";

    public boolean contains(String key){
        JSONObject allPairs = readFile();
        return allPairs.has(key);
    }

    public String getKV(String key){
        if (!contains(key)){
            exit(1);
        }
        JSONObject allPairs = readFile();
        return allPairs.getString(key);
    }

    public void putKV(String key, String value){
        JSONObject allPairs = readFile();
        allPairs.put(key, value);
        writeFile(allPairs);
    }

    @Override
    public void delete(String key) throws Exception {
        JSONObject allPairs = readFile();
        allPairs.remove(key);
        writeFile(allPairs);
    }

    public JSONObject readFile(){
        File store = new File(fileAddress);
        // check if file does not exist
        if (!(store.exists())) {
            String str = "{}";
            JSONObject obj = new JSONObject(str);
            return obj;
        }
        String str = "{}";
        try {
            FileReader fr = new FileReader(store);
            BufferedReader br = new BufferedReader(fr);
            str = br.readLine();
            br.close();
            fr.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Cannot read");
        }
        JSONObject allPairs = new JSONObject(str);
        return allPairs;

    }

    public void writeFile(JSONObject pairs){
        File store = new File(fileAddress);
        // check if file does not exist
        if (store.exists()) {
            store.delete();
            try {
                store.createNewFile();
            } catch (IOException e) {
                System.out.println("Cannot create new file");
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
            System.out.println("Cannot write file");
        }
    }

    @Override
    public void clearStorage() throws Exception {
        File store = new File(fileAddress);
        store.delete();
    }
}