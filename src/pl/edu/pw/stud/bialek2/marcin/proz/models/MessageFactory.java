package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.time.LocalDateTime;


public class MessageFactory {
    private MessageFactory() {}

    public static Message createMessage(MessageType type, int id, Chatroom chatroom, Peer peer, LocalDateTime timestamp, byte[] value) {
        if(type == MessageType.TEXT_MESSAGE) {
            return new TextMessage(id, chatroom, peer, timestamp, value);
        }

        return new NullMessage(id, chatroom, peer, timestamp);
    }
}
