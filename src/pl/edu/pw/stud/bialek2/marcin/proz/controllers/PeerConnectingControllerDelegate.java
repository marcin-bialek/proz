package pl.edu.pw.stud.bialek2.marcin.proz.controllers;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;


public interface PeerConnectingControllerDelegate {
    public void peerConnectingControllerDidAccept(PeerConnectingController sender, Peer peer);
    public void peerConnectingControllerDidReject(PeerConnectingController sender, Peer peer);
}
