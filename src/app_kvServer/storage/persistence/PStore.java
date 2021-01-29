package app_kvServer.storage.persistence;

import java.io.*;
import java.util.*;

import static java.lang.System.exit;
import static java.lang.System.out;



public class PStore implements IPersistence {
    private static String fileAddress = "./PStore.txt";
    private static String delimiter = ",";

    public boolean contains(String key) {
        int loc = find_loc(key);
        if (loc < 0) {
            return false;
        }
        return true;
    }

    /**
     * Check if key is in storage
     * -1 for not found
     * return is the byte location
     *
     * @param key
     * @return byte location of the key
     */
    public int find_loc(String key) {
        File store = new File(fileAddress);
        // check if file does not exist
        if (!(store.exists())) {
            return -1;
        }
        int byteLocation = 0;
        try {
            FileReader fr = new FileReader(store);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                line = line.strip();
                List<String> lines = Arrays.asList(line.split(delimiter));
                // out.println(lines);
                if (lines.get(0).equals(key)) {
                    return byteLocation;
                }
                // because 1 byte for every character
                byteLocation += line.getBytes().length + 1;
//                out.println(byteLocation);
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get the value for the key
     *
     * @param key
     */
    public String getKV(String key) {
        File store = new File(fileAddress);
        // check if file does not exist
        if (!(store.exists())) {
            exit(1);
        }
        try {
            FileReader fr = new FileReader(store);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                line = line.strip();
                List<String> lines = Arrays.asList(line.split(delimiter));
                if (lines.get(0).equals(key)) {
                    return lines.get(1);
                }
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        exit(1);
        return "Not Found";
    }

    /**
     * Put the key-value pair into storage
     * If the storage file does not exist, create the file
     * @param key
     */
    public void delete(String key) {
        File store = new File(fileAddress);
        // check if file does not exist
        if (!(store.exists())) {
            try {
                store.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // delete the og from file
        int loc = find_loc(key);
        if (loc >= 0){
            out.println(loc);
            try {
                RandomAccessFile rf = new RandomAccessFile(fileAddress, "rw");
                rf.seek(loc);
                byte[] bytes = new byte[2];
                rf.write(bytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Put the key-value pair into storage
     * If the storage file does not exist, create the file
     * @param key
     * @param value
     */
    public void putKV(String key, String value) {
        File store = new File(fileAddress);
        // check if file does not exist
        if (!(store.exists())) {
            try {
                store.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // delete the og from file
        int loc = find_loc(key);
        if (loc >= 0){
            out.println(loc);
            try {
                RandomAccessFile rf = new RandomAccessFile(fileAddress, "rw");
                rf.seek(loc);
                byte[] bytes = new byte[2];
                rf.write(bytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // write to file
        try {
            FileWriter fw = new FileWriter(store, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(key + delimiter + value + '\n');
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete the persistent storage
     */
    public void clear(){
        File store = new File(fileAddress);
        store.delete();
    }

}