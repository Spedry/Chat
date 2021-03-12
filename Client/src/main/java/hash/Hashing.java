package hash;

import lombok.Getter;
import lombok.NonNull;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {
    // Logger pri hashovaní nepoužívam z logického dôvodu
    // komenty ako náhrada
    @Getter
    private byte[] salt;

    // This hashes users password
    public String hashIt(@NonNull String passwordToHash) {
        // SHA3-256
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for SHA3-256
            final MessageDigest md = MessageDigest.getInstance("SHA3-256");
            //Hashing
            md.update(salt); // Combinations of passwordToHash and salt
            final byte[] hashbytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            // This bytes[] has bytes in decimal format;
            generatedPassword = bytesToHex(hashbytes);
        }
        catch (NoSuchAlgorithmException nsae) {
            System.out.println("Cryptographic algorithm is requested but is not available in the environment:\n" + nsae);
        }
        // Returning generated password
        return generatedPassword;
    }

    private String bytesToHex(byte[] bytes) { // This bytes[] has bytes in decimal format;
        // Bytes to hex
        StringBuilder sb = new StringBuilder();
        // Convert it to hexadecimal format
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public void setSalt(@NonNull JSONObject jsonObject) {
        // Setting salt
        String getBackEncodedString = jsonObject.getJSONObject("Data").getString("Key");
        this.salt = org.apache.commons.codec.binary.Base64.decodeBase64(getBackEncodedString);
        // Salt was set = toString(salt)
    }
}


