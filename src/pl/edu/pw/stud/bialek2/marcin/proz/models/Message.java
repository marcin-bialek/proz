package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.time.LocalDateTime;


public abstract class Message {
    private int id;
    private Chatroom chatroom;
    private Peer peer;
    private MessageType type;
    private LocalDateTime timestamp;
    private boolean isSentByUser;

    public Message(int id, Chatroom chatroom, Peer peer, MessageType type, LocalDateTime timestamp) {
        this.id = id;
        this.chatroom = chatroom;
        this.peer = peer;
        this.type = type;
        this.timestamp = timestamp;
        this.isSentByUser = false;
    }

    public Message(Chatroom chatroom, Peer peer, MessageType type) {
        this(0, chatroom, peer, type, LocalDateTime.now());
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public Chatroom getChatroom() {
        return this.chatroom;
    }

    public Peer getPeer() {
        return this.peer;
    }

    public MessageType getType() {
        return this.type;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public void setIsSentByUser(boolean isSentByUser) {
        this.isSentByUser = isSentByUser;
    }

    public boolean getIsSentByUser() {
        return this.isSentByUser;
    }

    abstract public String getValueAsString();
    abstract public byte[] getValueAsBytes();
}
