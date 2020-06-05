package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;


public class Peer {
    private int id;
    private String nick;
    private String address;
    private int port;
    private PublicKey publicKey;
    private ArrayList<Message> messages;
    private P2PSession session;

    public Peer(int id, String nick, String address, int port, PublicKey publicKey) {
        this.id = id;
        this.nick = nick;
        this.address = address;
        this.port = port;
        this.publicKey = publicKey;
        this.messages = new ArrayList<>();
        this.session = new P2PSession();
    }

    public Peer(String nick, String address, int port, PublicKey publicKey) {
        this(0, nick, address, port, publicKey);
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

    public String getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public ArrayList<Message> getMessages() {
        return this.messages;
    }

    public Message getLastMessage() {
        if(this.messages.size() == 0) {
            return new NullMessage(0, this, null);
        }

        return this.messages.get(this.messages.size() - 1);
    }

    public P2PSession getSession() {
        return this.session;
    }
}
