package pl.edu.pw.stud.bialek2.marcin.proz.views.addchat;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Chatroom;

import java.util.UUID;


public interface AddChatroomWindowListener {
    public void addChatroomWindowDidCreateChatroom(Chatroom chatroom);
    public void addChatroomWindowDidAddChatroom(UUID uuid, String address, int port);
}
