package hash;

import lombok.Getter;
import lombok.NonNull;
import java.security.*;

public class Hashing {
    // Logger pri hashovaní nepoužívam z logického dôvodu
    // komenty ako náhrada
    @Getter
    private byte[] salt;

    public String hashIt(@NonNull String password) {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            final MessageDigest md = MessageDigest.getInstance("MD5");
            // Add password bytes to digest
            salt = generateSalt();
            // Hashing
            md.update(salt); // Combinations of passToHash and salt
            // Get the hash's bytes
            byte[] bytes = md.digest(password.getBytes());
            // This bytes[] has bytes in decimal format;
            // Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            // Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    public byte[] generateSalt() throws NoSuchAlgorithmException {
        // SHA1PRNG algorithm is used as cryptographically strong pseudo-random number generator based on the SHA-1
        // Always use a SecureRandom generator
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        // Create array for salt
        byte[] salt = new byte[16];
        // Get a random salt
        sr.nextBytes(salt);
        // return salt
        return salt;
    }
}
