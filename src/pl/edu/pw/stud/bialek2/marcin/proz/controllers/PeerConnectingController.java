package pl.edu.pw.stud.bialek2.marcin.proz.controllers;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;
import pl.edu.pw.stud.bialek2.marcin.proz.services.SecurityService;
import pl.edu.pw.stud.bialek2.marcin.proz.views.PeerConnectingWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.PeerConnectingWindowListener;


public class PeerConnectingController implements PeerConnectingWindowListener {
    private PeerConnectingControllerDelegate delegate;
    private PeerConnectingWindow view;
    private Peer peer;

    public PeerConnectingController(PeerConnectingWindow view, Peer peer) {
        this.view = view;
        this.peer = peer;
        this.view.setListener(this);

        final String nick = peer.getNick();
        final String address = peer.getAddress();
        final String publicKey = new String(SecurityService.keyToString(peer.getPublicKey()));
        this.view.updatePeerInfo(nick, address, publicKey);
    }

    public void setDelegate(PeerConnectingControllerDelegate delegate) {
        this.delegate = delegate;
    }

    public void closeWindow() {
        this.view.setVisible(false);
        this.view.dispose();
    }

    @Override
    public void peerConnectingWindowDidAccept() {
        if(this.delegate != null) {
            this.delegate.peerConnectingControllerDidAccept(this, this.peer);
        }
    }

    @Override
    public void peerConnectingWindowDidReject() {
        if(this.delegate != null) {
            this.delegate.peerConnectingControllerDidReject(this, this.peer);
        }
    }
}