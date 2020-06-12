package pl.edu.pw.stud.bialek2.marcin.proz.services;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class SecurityService {
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
    private static final String SYMMETRIC_CIPHER_ALGORITHM = "AES";
    private static final String SYMMETRIC_CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String ASYMMETRIC_CIPHER_ALGORITHM = "RSA";
    private static final String ASYMMETRIC_CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static SecurityServiceStaticDelegate staticDelegate;

    public static void setStaticDelegate(SecurityServiceStaticDelegate staticDelegate) {
        SecurityService.staticDelegate = staticDelegate;
    }

    public static String byteArray2HexString(final byte[] input) {
        final char[] output = new char[input.length * 3 - 1];

        for(int i = 0, j = 0; i < input.length; i++, j += 3) {
            output[j] = HEX_DIGITS[(input[i] >> 4) & 0xf];
            output[j + 1] = HEX_DIGITS[input[i] & 0xf];
        }

        for(int i = 2; i < output.length; i += 3) {
            output[i] = ':';
        }

        return new String(output);
    }

    public static String keyToString(Key key) {
        return new String(byteArray2HexString(key.getEncoded()));
    }

    public static byte[] hashSHA256(final byte[] input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            return messageDigest.digest(input);
        } 
        catch(NoSuchAlgorithmException e) {
            staticDelegate.securityServiceNoSuchAlgorithm();
        }

        return null;
    }

    public static byte[] hashSHA256(final char[] input) {
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(input));
        return hashSHA256(buffer.array());
    }

    public static PublicKey generatePublicKey(final byte[] raw) {
        try {
            final KeyFactory keyFactory = KeyFactory.getInstance(ASYMMETRIC_CIPHER_ALGORITHM);
            return keyFactory.generatePublic(new X509EncodedKeySpec(raw));
        } 
        catch(InvalidKeySpecException e) {
            e.printStackTrace();
        }
        catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(ASYMMETRIC_CIPHER_ALGORITHM);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            staticDelegate.securityServiceNoSuchAlgorithm();
        }

        return null;
    }

    public static KeyPair generateKeyPair(final byte[] privateKeyRaw, final byte[] publicKeyRaw) {
        try {
            final KeyFactory keyFactory = KeyFactory.getInstance(ASYMMETRIC_CIPHER_ALGORITHM);
            final PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyRaw));
            final PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyRaw));
            return new KeyPair(publicKey, privateKey);
        } 
        catch(InvalidKeySpecException e) {
            e.printStackTrace();
        }
        catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static SecretKey generateSecretKey(byte[] raw) {
        return new SecretKeySpec(raw, SYMMETRIC_CIPHER_ALGORITHM);
    }

    public static SecretKey generateSecretKey(char[] password) {
        return generateSecretKey(hashSHA256(password));
    }

    public static SecretKey generateSecretKey() {
        SecureRandom random = new SecureRandom();
        final byte[] seed = new byte[256];
        random.nextBytes(seed);
        return generateSecretKey(hashSHA256(seed));
    }

    private static byte[] doCrypto(final byte[] input, final Key key, final String transformation, final int mode) throws WrongPasswordException {
        try {
            final Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(mode, key);
            return cipher.doFinal(input);
        }
        catch(NoSuchAlgorithmException | NoSuchPaddingException e) {
            staticDelegate.securityServiceNoSuchAlgorithm();
        }
        catch(InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new WrongPasswordException();
        }

        return null;
    }

    public static byte[] symmetricEncrypt(final byte[] input, final SecretKey key) throws WrongPasswordException {
        return doCrypto(input, key, SYMMETRIC_CIPHER_TRANSFORMATION, Cipher.ENCRYPT_MODE);
    }

    public static byte[] symmetricDecrypt(final byte[] input, final SecretKey key) throws WrongPasswordException {
        return doCrypto(input, key, SYMMETRIC_CIPHER_TRANSFORMATION, Cipher.DECRYPT_MODE);
    }

    public static byte[] asymmetricEncrypt(final byte[] input, final Key key) throws WrongPasswordException {
        return doCrypto(input, key, ASYMMETRIC_CIPHER_TRANSFORMATION, Cipher.ENCRYPT_MODE);
    }

    public static byte[] asymmetricDecrypt(final byte[] input, final Key key) throws WrongPasswordException {
        return doCrypto(input, key, ASYMMETRIC_CIPHER_TRANSFORMATION, Cipher.DECRYPT_MODE);
    }

    public static byte[] encodeString(final String input) {
        return input.getBytes(StandardCharsets.UTF_8);
    }

    public static String decodeString(final byte[] input) {
        return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(input)).toString();
    }

    public static byte[] encodeInt(final int input) {
        return ByteBuffer.allocate(4).putInt(input).array();
    }

    public static int decodeInt(final byte[] input) {
        return ByteBuffer.wrap(input).getInt();
    }

    public static boolean isValidPassword(char[] password) {
        return password.length >= 6;
    }
}
