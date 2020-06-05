package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.security.PublicKey;
import java.util.HashMap;


public class Peer {
    private int id;
    private String nick;
    private String lastAddress;
    private int lastPort;
    private PublicKey publicKey;
    private HashMap<Integer, Chatroom> chatrooms;
    private P2PSession session;

    public Peer(int id, String nick, String lastAddress, int lastPort, PublicKey publicKey) {
        this.id = id;
        this.nick = nick;
        this.lastAddress = lastAddress;
        this.lastPort = lastPort;
        this.publicKey = publicKey;
        this.chatrooms = new HashMap<>();
        this.session = new P2PSession(this);
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

    public void addChatroom(Chatroom chatroom) {
        this.chatrooms.put(chatroom.getId(), chatroom);
    }

    public HashMap<Integer, Chatroom> getChatrooms() {
        return this.chatrooms;
    }

    public P2PSession getSession() {
        return this.session;
    }
}
