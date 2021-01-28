package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

public class PStore {
    private static String fileAddress = "./PStore.txt";
    private static String delimiter = ",";

    public PStore() {
    }

    public static int contain(String key) {
        File store = new File(fileAddress);
        if (!store.exists()) {
            return -1;
        } else {
            int byteLocation = 0;

            try {
                FileReader fr = new FileReader(store);
                BufferedReader br = new BufferedReader(fr);

                for(String line = br.readLine(); line != null; line = br.readLine()) {
                    line = line.strip();
                    List<String> lines = Arrays.asList(line.split(delimiter));
                    if (((String)lines.get(0)).equals(key)) {
                        return byteLocation;
                    }

                    byteLocation += line.getBytes().length + 1;
                }
            } catch (FileNotFoundException var7) {
                var7.printStackTrace();
            } catch (IOException var8) {
                var8.printStackTrace();
            }

            return -1;
        }
    }

    public static String getKV(String key) {
        File store = new File(fileAddress);
        if (!store.exists()) {
            System.exit(1);
        }

        try {
            FileReader fr = new FileReader(store);
            BufferedReader br = new BufferedReader(fr);

            for(String line = br.readLine(); line != null; line = br.readLine()) {
                line = line.strip();
                List<String> lines = Arrays.asList(line.split(delimiter));
                if (((String)lines.get(0)).equals(key)) {
                    return (String)lines.get(1);
                }
            }
        } catch (FileNotFoundException var6) {
            var6.printStackTrace();
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        System.exit(1);
        return "Not Found";
    }

    public static void putKV(String key, String value) {
        File store = new File(fileAddress);
        if (!store.exists()) {
            try {
                store.createNewFile();
            } catch (IOException var9) {
                var9.printStackTrace();
            }
        }

        int loc = contain(key);
        if (loc >= 0) {
            System.out.println(loc);

            try {
                RandomAccessFile rf = new RandomAccessFile(fileAddress, "rw");
                rf.seek((long)loc);
                byte[] bytes = new byte[2];
                rf.write(bytes);
            } catch (FileNotFoundException var7) {
                var7.printStackTrace();
            } catch (IOException var8) {
                var8.printStackTrace();
            }
        }

        try {
            FileWriter fw = new FileWriter(store, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(key + delimiter + value + "\n");
            bw.close();
            fw.close();
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    public static void clear() {
        File store = new File(fileAddress);
        store.delete();
    }

}
