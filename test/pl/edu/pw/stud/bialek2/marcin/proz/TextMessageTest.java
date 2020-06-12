package pl.edu.pw.stud.bialek2.marcin.proz;

import pl.edu.pw.stud.bialek2.marcin.proz.models.TextMessage;
import pl.edu.pw.stud.bialek2.marcin.proz.models.MessageType;

import org.junit.Assert;
import org.junit.Test;


public class TextMessageTest {

    @Test
    public void testType() {
        final TextMessage message = new TextMessage(null, false, "");
        Assert.assertEquals(MessageType.TEXT_MESSAGE, message.getType());
    }
    
    @Test
    public void testCoding() {
        final String text = "Labore commodo consequat quis nisi do Lorem aute adipisicing nulla elit sunt ex consectetur nostrud.";
        
        final TextMessage message = new TextMessage(null, false, text);
        final byte[] encoded = message.getValueAsBytes();
        final TextMessage decodedMessage = new TextMessage(0, null, false, null, encoded);

        Assert.assertEquals(text, decodedMessage.getText());
    }

}
