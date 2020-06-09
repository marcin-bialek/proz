package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.awt.Dimension;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;


public class User extends Peer {
    private SecretKey secretKey;
    private PrivateKey privateKey;
    private String dbFilename;
    private Dimension windowSize;

    public User(SecretKey secretKey, PrivateKey privateKey, PublicKey publicKey, String nick, int port, String dbFilename, Dimension windowSize) {
        super(0, nick, "0.0.0.0", port, publicKey);
        this.dbFilename = dbFilename;
        this.secretKey = secretKey;
        this.privateKey = privateKey;
        this.windowSize = windowSize;
    }

    public SecretKey getSecretKey() {
        return this.secretKey;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public String getDBFilename() {
        return this.dbFilename;
    }

    public void setWindowSize(Dimension windowSize) {
        this.windowSize = windowSize;
    }

    public Dimension getWindowSize() {
        return this.windowSize;
    }
}
