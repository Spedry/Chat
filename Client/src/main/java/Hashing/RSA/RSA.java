package Hashing.RSA;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSA {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final Decrypt decrypt;
    private final Encrypt encrypt;
    private PublicKey othersSidePublicKey;

    public RSA() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024*5);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
        encrypt = new Encrypt();
        decrypt = new Decrypt();
    }

    public String getBase64EncodedPublicKey() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

//    public String test() {
//        return Base64.getEncoder().encodeToString(othersSidePublicKey.getEncoded());
//    }

    public void setOthersSidePublicKey(String base64PublicKey) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            othersSidePublicKey = keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt(String data) {
        byte[] encrypted = new byte[0];
        try {
            encrypted = encrypt.encrypt(data, othersSidePublicKey);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return encrypted;
    }

    public String decrypt(String data) {
        String decrypted = null;
        try {
            decrypted = decrypt.decrypt(data, privateKey);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return decrypted;
    }

    public boolean isOthersSidePublicKeyNull() {
        return othersSidePublicKey == null;
    }
}
