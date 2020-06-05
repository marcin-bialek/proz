package pl.edu.pw.stud.bialek2.marcin.proz.services;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;


public interface P2PServiceDelegate {
    public void p2pServiceServerError();
    public void p2pServiceReady();
    public void p2pServiceIncomingConnection(Peer peer);
    public void p2pServicePeerDisconnected(Peer peer);
}
