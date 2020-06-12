package pl.edu.pw.stud.bialek2.marcin.proz;

import pl.edu.pw.stud.bialek2.marcin.proz.services.SecurityService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.WrongPasswordException;

import org.junit.Test;
import org.junit.Assert;
import java.security.KeyPair;
import javax.crypto.SecretKey;


public class SecurityServiceTest {
    
    @Test
    public void testByteArray2HexString() {
        final byte[] bytes = new byte[] { 
            (byte)0x01, (byte)0x23, (byte)0x45, (byte)0x67, 
            (byte)0x89, (byte)0xab, (byte)0xcd, (byte)0xef 
        };

        final String hexString = SecurityService.byteArray2HexString(bytes);
        Assert.assertEquals("01:23:45:67:89:ab:cd:ef", hexString);
    }

    @Test
    public void testHashSHA256() {
        final char[] input = new char[] { 'a', 'b', 'c', 'd' };
        final String output = SecurityService.byteArray2HexString(SecurityService.hashSHA256(input));
        final String expected = "88:d4:26:6f:d4:e6:33:8d:13:b8:45:fc:f2:89:57:9d:20:9c:89:78:23:b9:21:7d:a3:e1:61:93:6f:03:15:89";
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testIntCoding() {
        final int number = 34523452;
        final byte[] encoded = SecurityService.encodeInt(number);
        final int decoded = SecurityService.decodeInt(encoded);
        Assert.assertEquals(number, decoded);
    }

    @Test
    public void testStringCoding() {
        final String text = "Anim culpa aliquip adipisicing dolore laboris eiusmod reprehenderit laboris eu ut et fugiat dolore dolore.";
        final byte[] encoded = SecurityService.encodeString(text);
        final String decoded = SecurityService.decodeString(encoded);
        Assert.assertEquals(text, decoded);
    }

    @Test
    public void testSymmetricCrypto() {
        final SecretKey key = SecurityService.generateSecretKey();
        final String text = "Dolor nostrud esse ullamco non reprehenderit ipsum.";
        final byte[] encoded = SecurityService.encodeString(text);
        String text2 = "";
        
        try {
            final byte[] encrypted = SecurityService.symmetricEncrypt(encoded, key);
            final byte[] decrypted = SecurityService.symmetricDecrypt(encrypted, key);
            text2 = SecurityService.decodeString(decrypted);
        }
        catch(WrongPasswordException e) {}

        Assert.assertEquals(text, text2);
    }

    @Test
    public void testAsymmetricCrypto() {
        final KeyPair pair = SecurityService.generateKeyPair();
        final String text = "Deserunt culpa cupidatat Lorem enim do adipisicing labore ea.";
        final byte[] encoded = SecurityService.encodeString(text);
        String text2 = "";
        
        try {
            final byte[] encrypted = SecurityService.asymmetricEncrypt(encoded, pair.getPublic());
            final byte[] decrypted = SecurityService.asymmetricDecrypt(encrypted, pair.getPrivate());
            text2 = SecurityService.decodeString(decrypted);
        }
        catch(WrongPasswordException e) {}

        Assert.assertEquals(text, text2);
    }

}
