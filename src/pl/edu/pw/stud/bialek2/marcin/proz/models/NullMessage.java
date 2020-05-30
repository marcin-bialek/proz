package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.time.LocalDateTime;


public class NullMessage extends Message {
    public NullMessage(int id, Chatroom chatroom, Peer peer, LocalDateTime timestamp) {
        super(id, chatroom, peer, MessageType.NULL_MESSAGE, timestamp);
    }

    @Override
    public String getValueAsString() {
        return "";
    }

    @Override
    public byte[] getValueAsBytes() {
        return new byte[0];
    }
}
