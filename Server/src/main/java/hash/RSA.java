package hash;

import lombok.Getter;

import java.security.*;

public class RSA {
    @Getter
    private PrivateKey privateKey;
    @Getter
    private PublicKey publicKey;

    public RSA() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }
}
