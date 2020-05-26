package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.security.PublicKey;


public class Peer {
    private int id;
    private String nick;
    private String lastAddress;
    private int lastPort;
    private PublicKey publicKey;

    public Peer(int id, String nick, String lastAddress, int lastPort, PublicKey publicKey) {
        this.id = id;
        this.nick = nick;
        this.lastAddress = lastAddress;
        this.lastPort = lastPort;
        this.publicKey = publicKey;
    }

    public Peer(String nick, String lastAddress, int lastPort, PublicKey publicKey) {
        this(0, nick, lastAddress, lastPort, publicKey);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getNick() {
        return this.nick;
    }

    public String getLastAddress() {
        return this.lastAddress;
    }

    public int getLastPort() {
        return this.lastPort;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }
}
