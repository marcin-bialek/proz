package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


public class TextMessage extends Message {
    private String text;

    public TextMessage(int id, Chatroom chatroom, Peer peer, LocalDateTime timestamp, String text) {
        super(id, chatroom, peer, MessageType.TEXT_MESSAGE, timestamp);
        this.text = text;
    }

    public TextMessage(int id, Chatroom chatroom, Peer peer, LocalDateTime timestamp, byte[] value) {
        this(id, chatroom, peer, timestamp, StandardCharsets.UTF_8.decode(ByteBuffer.wrap(value)).toString());
    }

    public TextMessage(Chatroom chatroom, Peer peer, String text) {
        this(0, chatroom, peer, LocalDateTime.now(), text);
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String getValueAsString() {
        return this.text;
    }

    @Override
    public byte[] getValueAsBytes() {
        return StandardCharsets.UTF_8.encode(this.text).array();
    }
}
