package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.time.LocalDateTime;


public class NullMessage extends Message {
    public NullMessage(int id, Peer peer, LocalDateTime timestamp) {
        super(id, peer, MessageType.NULL_MESSAGE, false, timestamp);
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
