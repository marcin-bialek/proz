package pl.edu.pw.stud.bialek2.marcin.proz.models;


public class P2PSession {
    private Peer peer;
    private State state;

    public P2PSession(Peer peer, State state) {
        this.peer = peer;
        this.state = state;
    }

    public P2PSession(Peer peer) {
        this(peer, State.DISCONNECTED);
    }

    public Peer getPeer() {
        return this.peer;
    }
    
    public State getState() {
        return this.state;
    }

    public enum State {
        DISCONNECTED,
        CLIENT_HELLO,
        SERVER_HELLO
    }     
}
