package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.time.LocalDateTime;


public abstract class Message {
    private int id;
    private Peer peer;
    private MessageType type;
    private LocalDateTime timestamp;
    private boolean incoming;

    public Message(int id, Peer peer, MessageType type, boolean incoming, LocalDateTime timestamp) {
        this.id = id;
        this.peer = peer;
        this.type = type;
        this.incoming = incoming;
        this.timestamp = timestamp;
    }

    public Message(Peer peer, MessageType type, boolean incoming) {
        this(0, peer, type, incoming, LocalDateTime.now());
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
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

    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }

    public boolean isIncoming() {
        return this.incoming;
    }

    abstract public String getValueAsString();
    abstract public byte[] getValueAsBytes();
}
