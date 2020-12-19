package client;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Arrays;

public class PassHash {
    @Getter
    private byte[] salt;
    private final Logger logger = LogManager.getLogger(this.getClass());

    public String hashIt(@NonNull String passwordToHash) {
        //SHA3-256
        String generatedPassword = null;
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA3-256");
            logger.info("Adding salt");
            logger.info(salt);
            if (salt == null) logger.info("SALT IS NULL WTF");
            md.update(salt); //combinations of passToHash and salt
            final byte[] hashbytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            generatedPassword = bytesToHex(hashbytes);
        }
        catch (NoSuchAlgorithmException nsae) {
            logger.error("Cryptographic algorithm is requested but is not available in the environment", nsae);
        }
        logger.info("Returning generated password");
        return generatedPassword;
    }

    private String bytesToHex(byte[] bytes) {
        logger.info("Bytes to hex");
        logger.info(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        logger.info("Hex: " + sb.toString());
        return sb.toString();
    }

    public void setSalt(@NonNull JSONObject jsonObject) {
        logger.info("Setting salt");
        logger.info(jsonObject.toString());
        logger.info(jsonObject.getJSONObject("Data"));
        logger.info(jsonObject.getJSONObject("Data").toString().getBytes(StandardCharsets.UTF_8));
        salt = jsonObject.getJSONObject("Data").toString().getBytes(StandardCharsets.UTF_8);
        logger.info("Salt was set: " + salt);
    }

    /*private byte[] setSalt() throws NoSuchAlgorithmException {
        //SHA1PRNG algorithm is used as cryptographically strong pseudo-random number generator based on the SHA-1
        //Always use a SecureRandom generator
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        //Create array for salt
        byte[] salt = new byte[16];
        //Get a random salt
        sr.nextBytes(salt);
        //return salt
        return salt;
    }*/
}


