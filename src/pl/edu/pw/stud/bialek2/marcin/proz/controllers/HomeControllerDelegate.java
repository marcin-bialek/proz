package pl.edu.pw.stud.bialek2.marcin.proz.controllers;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;


public interface HomeControllerDelegate {
    public void homeControllerDidExit(HomeController sender);
    public void homeControllerDidEnterMessage(HomeController sender, Message message);
    public void homeControllerDidAddPeer(HomeController sender, Peer peer);
    public void homeControllerLoadMessages(HomeController sender, Peer peer);
}

