package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


public class TextMessage extends Message {
    private String text;

    public TextMessage(int id, Peer peer, boolean incoming, LocalDateTime timestamp, String text) {
        super(id, peer, MessageType.TEXT_MESSAGE, incoming, timestamp);
        this.text = text;
    }

    public TextMessage(int id, Peer peer, boolean incoming, LocalDateTime timestamp, byte[] value) {
        this(id, peer, incoming, timestamp, StandardCharsets.UTF_8.decode(ByteBuffer.wrap(value)).toString());
    }

    public TextMessage(Peer peer, boolean incoming, String text) {
        this(0, peer, incoming, LocalDateTime.now(), text);
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
        final ByteBuffer buffer = StandardCharsets.UTF_8.encode(this.text);
        final byte[] encoded = new byte[buffer.limit()];
        buffer.get(encoded);
        return encoded;
    }
}
