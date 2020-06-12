package pl.edu.pw.stud.bialek2.marcin.proz;

import pl.edu.pw.stud.bialek2.marcin.proz.models.ImageMessage;
import pl.edu.pw.stud.bialek2.marcin.proz.models.MessageType;

import org.junit.Assert;
import org.junit.Test;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;


public class ImageMessageTest {

    @Test
    public void testType() {
        final ImageMessage message = new ImageMessage(null, false, null);
        Assert.assertEquals(MessageType.IMAGE_MESSAGE, message.getType());
    }    

    @Test
    public void testCoding() {
        final BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 128, 64);
        graphics.setColor(Color.RED);
        graphics.fillRect(0, 64, 128, 64);
        graphics.dispose();

        final ImageMessage message = new ImageMessage(null, false, image);
        final byte[] encoded = message.getValueAsBytes();
        final ImageMessage decodedMessage = new ImageMessage(0, null, false, null, encoded);
        final BufferedImage decodedImage = decodedMessage.getImage();

        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getHeight(); y++) {
                Assert.assertEquals(image.getRGB(x, y), decodedImage.getRGB(x, y));
            }
        }
    }

}
