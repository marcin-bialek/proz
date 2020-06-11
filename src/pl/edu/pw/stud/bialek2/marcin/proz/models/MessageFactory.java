package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.time.LocalDateTime;


public class MessageFactory {
    private MessageFactory() {}

    public static Message createMessage(MessageType type, int id, Peer peer, boolean incoming, LocalDateTime timestamp, byte[] value) {
        if(type == MessageType.TEXT_MESSAGE) {
            return new TextMessage(id, peer, incoming, timestamp, value);
        }
        else if(type == MessageType.IMAGE_MESSAGE) {
            return new ImageMessage(id, peer, incoming, timestamp, value);
        }

        return new NullMessage(id, peer, timestamp);
    }
}
