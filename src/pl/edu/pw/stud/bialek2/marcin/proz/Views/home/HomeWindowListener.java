package pl.edu.pw.stud.bialek2.marcin.proz.views.home;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;

import java.awt.Dimension;


public interface HomeWindowListener {
    public void homeWindowDidResize(Dimension windowSize);
    public void homeWindowDidClose();
    public void homeWindowDidClickAddPeerButton();   
    public void homeWindowDidClickSettingsButton();   
    public void homeWindowDidClickDeleteDataButton();   
    public void homeWindowDidChangePeer(Peer peer);
    public void homeWindowDidEnterMessage(String value); 
}
