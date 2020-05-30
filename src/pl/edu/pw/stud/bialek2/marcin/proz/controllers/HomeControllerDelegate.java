package pl.edu.pw.stud.bialek2.marcin.proz.controllers;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Chatroom;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;


public interface HomeControllerDelegate {
    public void homeControllerDidExit(HomeController sender);
    public void homeControllerDidEnterMessage(HomeController sender, Message message);
    public void homeControllerDidCreateChatroom(HomeController sender, Chatroom chatroom);
    public void homeControllerLoadMessages(HomeController sender, Chatroom chatroom);
}

