package pl.edu.pw.stud.bialek2.marcin.proz.views.home;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Chatroom;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;

import java.awt.Dimension;


public interface HomeWindowListener {
    public void homeWindowDidResize(Dimension windowSize);
    public void homeWindowDidClose();
    public void homeWindowDidClickAddChatroomButton();   
    public void homeWindowDidChangeChatroom(Chatroom chatroom);
    public void homeWindowDidEnterMessage(String value); 
}
