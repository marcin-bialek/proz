package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.awt.Dimension;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;


public class User extends Peer {
    private SecretKey secretKey;
    private PrivateKey privateKey;
    private int port;
    private Dimension windowSize;

    public User(SecretKey secretKey, PrivateKey privateKey, PublicKey publicKey, String nick, int port, Dimension windowSize) {
        super(0, nick, "0.0.0.0", port, publicKey);
        this.secretKey = secretKey;
        this.privateKey = privateKey;
        this.port = port;
        this.windowSize = windowSize;
    }

    public SecretKey getSecretKey() {
        return this.secretKey;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public Dimension getWindowSize() {
        return this.windowSize;
    }
}
