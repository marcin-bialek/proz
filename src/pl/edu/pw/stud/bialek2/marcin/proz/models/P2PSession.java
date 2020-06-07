package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.nio.channels.SocketChannel;

import javax.crypto.SecretKey;


public class P2PSession {
    private SocketChannel channel;
    private Peer peer;
    private State state;
    private SecretKey key;

    public P2PSession() {
        this.state = State.DISCONNECTED;
    }

    public SocketChannel getChannel() {
        return this.channel;
    }

    public void setPeer(Peer peer) {
        this.peer = peer;
    }

    public Peer getPeer() {
        return this.peer;
    }
    
    public State getState() {
        return this.state;
    }

    public SecretKey getKey() {
        return this.key;
    }

    public void setDisconnected() {
        this.state = State.DISCONNECTED;
    }

    public void setConnecting(SocketChannel channel, Peer peer) {
        this.channel = channel;
        this.peer = peer;
        this.state = State.CONNECTING;
    }

    public void setConnecting(SocketChannel channel) {
        this.channel = channel;
        this.state = State.CONNECTING;
    }

    public void setSentClientHello() {
        this.state = State.SENT_CLIENT_HELLO;
    }

    public void setReceivedClientHello() {
        this.state = State.RECEIVED_CLIENT_HELLO;
    }

    public void setSentServerHello() {
        this.state = State.SENT_SERVER_HELLO;
    }

    public void setReceivedServerHello() {
        this.state = State.RECEIVED_SERVER_HELLO;
    }

    public void setConnected(SecretKey key) {
        this.key = key;
        this.state = State.CONNECTED;
    }

    public enum State {
        DISCONNECTED,
        CONNECTING,
        SENT_CLIENT_HELLO,
        RECEIVED_CLIENT_HELLO,
        SENT_SERVER_HELLO,
        RECEIVED_SERVER_HELLO,
        CONNECTED
    }     
}
