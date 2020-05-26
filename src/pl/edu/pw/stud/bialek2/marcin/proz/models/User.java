package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.awt.Dimension;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;


public class User {
    private SecretKey secretKey;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String nick;
    private int port;
    private Dimension windowSize;

    public User(SecretKey secretKey, PrivateKey privateKey, PublicKey publicKey, String nick, int port, Dimension windowSize) {
        this.secretKey = secretKey;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.nick = nick;
        this.port = port;
        this.windowSize = windowSize;
    }

    public SecretKey getSecretKey() {
        return this.secretKey;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicLKey() {
        return this.publicKey;
    }

    public String getNick() {
        return this.nick;
    }

    public int getPort() {
        return this.port;
    }

    public Dimension getWindowSize() {
        return this.windowSize;
    }
}
