package pl.edu.pw.stud.bialek2.marcin.proz.models;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

import javax.imageio.ImageIO;


public class ImageMessage extends Message {
    private BufferedImage image;

    public ImageMessage(int id, Peer peer, boolean incoming, LocalDateTime timestamp, BufferedImage image) {
        super(id, peer, MessageType.IMAGE_MESSAGE, incoming, timestamp);
        this.image = image;
    }

    public ImageMessage(int id, Peer peer, boolean incoming, LocalDateTime timestamp, byte[] value) {
        super(id, peer, MessageType.IMAGE_MESSAGE, incoming, timestamp);

        try {
            this.image = ImageIO.read(new ByteArrayInputStream(value));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public ImageMessage(Peer peer, boolean incoming, BufferedImage image) {
        this(0, peer, incoming, LocalDateTime.now(), image);
    }

    public BufferedImage getImage() {
        return this.image;
    }

    @Override
    public String getValueAsString() {
        return "obrazek";
    }

    @Override
    public byte[] getValueAsBytes() {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        
        try {
            ImageIO.write(this.image, "png", stream);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return stream.toByteArray();
    }   
}
