package app_kvServer.storage.persistence;

import app_kvServer.storage.KeyNotFoundException;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;


import static java.lang.System.out;


/**
 * A persistence storage class.
 * Put takes O(1) and Get takes O(n).
 */
public class PStore implements IPersistence {
    private String fileAddress;
    private String delimiter = ",";

    private static Logger logger = Logger.getRootLogger();


    public PStore(String fileAddress) {
         this.fileAddress = fileAddress;
    }

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
        File store;
        try {
            store = new File(fileAddress);
            // check if file does not exist
            if (!(store.exists())) {
                return -1;
            }
        } catch (Exception ex) {
            logger.error("Invalid file address");
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
            logger.error("Cannot find file");
        } catch (IOException e) {
            logger.error("Cannot read from file");
        }
        return -1;
    }

    public String getKV(String key) throws KeyNotFoundException, IOException {
        File store;
        try {
            store = new File(fileAddress);
        } catch (Exception ex) {
            logger.error("Invalid file address");
            throw new IOException();
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
            throw new KeyNotFoundException();
        } catch (FileNotFoundException e) {
            logger.error("Cannot find file");
            throw new IOException();
        } catch (IOException e) {
            logger.error("Cannot read from file");
            throw new IOException();
        }
    }

    /**
     * Put the key-value pair into storage
     * If the storage file does not exist, create the file
     * @param key
     */
    public void delete(String key) throws IOException, KeyNotFoundException {
        // delete the og from file
        int loc = find_loc(key);
        if (loc < 0){
            throw new KeyNotFoundException();
        }
        try {
            RandomAccessFile rf = new RandomAccessFile(fileAddress, "rw");
            rf.seek(loc);
            byte[] bytes = new byte[2];
            rf.write(bytes);
        } catch (FileNotFoundException e) {
            logger.error("Cannot find file");
            throw new IOException();
        } catch (IOException e) {
            logger.error("Cannot read from file");
            throw new IOException();
        }
    }

    /**
     * Put the key-value pair into storage
     * If the storage file does not exist, create the file
     * @param key
     * @param value
     */
    public void putKV(String key, String value) throws IOException {

        File store;
        try {
            store = new File(fileAddress);
            // check if file does not exist
            if (!(store.exists())) {
                try {
                    store.createNewFile();
                } catch (IOException e) {
                    logger.error("Cannot create new persistent file");
                    throw new IOException();
                }
            }
        } catch (Exception ex) {
            logger.error("Invalid file address");
            throw new IOException();
        }

        // delete the og from file
        int loc = find_loc(key);
        // key already in file, do update
        if (loc >= 0){
            try {
                RandomAccessFile rf = new RandomAccessFile(fileAddress, "rw");
                rf.seek(loc);
                byte[] bytes = new byte[2];
                rf.write(bytes);
            } catch (FileNotFoundException e) {
                logger.error("Cannot find file");
                throw new IOException();
            } catch (IOException e) {
                logger.error("Cannot write to file");
                throw new IOException();
            }
            return;
        }

        // write to file
        try {
            FileWriter fw = new FileWriter(store, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(key + delimiter + value + '\n');
            bw.close();
            fw.close();
        } catch (IOException e) {
            logger.error("Cannot write to file");
            throw new IOException();
        }
    }

    /**
     * Delete the persistent storage
     */
    @Override
    public void clearStorage() throws IOException{
        try {
            File store = new File(fileAddress);
            store.delete();
        } catch (Exception ex) {
            logger.error("Cannot clear file");
            throw new IOException();
        }

    }

}