package pl.edu.pw.stud.bialek2.marcin.proz.services;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;


public interface P2PServiceDelegate {
    public void p2pServiceServerError(int port);
    public void p2pServiceReady();
    public void p2pServicePeerDidAccept(Peer peer);
    public void p2pServiceIncomingConnection(Peer peer);
    public void p2pServicePeerDisconnected(Peer peer);
    public void p2pServiceDidReceiveMessage(Message message);
}
