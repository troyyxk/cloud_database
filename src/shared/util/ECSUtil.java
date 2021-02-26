package shared.util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ECSUtil {
    public static String generateServerHashName(String host, int port) {
        return host + ":" + port;
    }

    public static String convertStringtoMD5Value(String rawVal) throws NoSuchAlgorithmException {
        // https://www.geeksforgeeks.org/md5-hash-in-java/
        MessageDigest md5Result = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md5Result.digest(rawVal.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        String hashVal = no.toString(16);
        while (hashVal.length() < 32) {
            hashVal = "0" + hashVal;
        }

        return hashVal.toUpperCase();
    }

    public static void execCommand(String command) throws IOException {
        Runtime runner = Runtime.getRuntime();
        runner.exec(command);
    }
}
