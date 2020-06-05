package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class Chatroom {
    private int id;
    private UUID uuid;
    private String name;
    private HashMap<Integer, Peer> peers;
    private ArrayList<Message> messages;

    public Chatroom(int id, UUID uuid, String name) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.peers = new HashMap<>();
    }

    public Chatroom(UUID uuid, String name) {
        this(0, uuid, name);
    }

    public Chatroom(String name) {
        this(UUID.randomUUID(), name);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public void addPeer(Peer peer) {
        this.peers.put(peer.getId(), peer);
    }

    public Peer getPeer(int id) {
        return this.peers.get(id);
    }

    public HashMap<Integer, Peer> getPeers() {
        return this.peers;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public ArrayList<Message> getMessages() {
        return this.messages;
    }

    public Message getLastMessage() {
        if(this.messages.size() != 0) {
            return this.messages.get(this.messages.size() - 1);
        }

        return null;
    }
}
