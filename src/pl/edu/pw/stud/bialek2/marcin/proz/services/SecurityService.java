package pl.edu.pw.stud.bialek2.marcin.proz.services;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
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
    private static final String ASYMMETRIC_CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static SecurityServiceStaticListener staticListener;

    public static void setStaticListener(SecurityServiceStaticListener staticListener) {
        SecurityService.staticListener = staticListener;
    }

    public static char[] bytes2Hex(final byte[] input) {
        final char[] output = new char[input.length * 2];

        for (int i = 0, j = 0; i < input.length; i++, j += 2) {
            output[j] = HEX_DIGITS[(input[i] >> 4) & 0xf];
            output[j + 1] = HEX_DIGITS[input[i] & 0xf];
        }

        return output;
    }

    public static byte[] hashSHA256(final char[] input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            ByteBuffer buffer = StandardCharsets.UTF_8.encode(CharBuffer.wrap(input));
            return messageDigest.digest(buffer.array());
        } catch (NoSuchAlgorithmException e) {
            staticListener.securityServiceNoSuchAlgorithm();
        }

        return null;
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
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            staticListener.securityServiceNoSuchAlgorithm();
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

    private static byte[] symmetricCrypto(final byte[] input, final SecretKey key, final int mode) throws WrongPasswordException {
        try {
            final Cipher cipher = Cipher.getInstance(SYMMETRIC_CIPHER_TRANSFORMATION);
            cipher.init(mode, key);
            return cipher.doFinal(input);
        }
        catch(NoSuchAlgorithmException e) {
            staticListener.securityServiceNoSuchAlgorithm();
        }
        catch(NoSuchPaddingException e) {
            e.printStackTrace();
        }
        catch(InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new WrongPasswordException();
        }

        return null;
    }

    public static byte[] symmetricEncrypt(final byte[] input, final SecretKey key) throws WrongPasswordException {
        return symmetricCrypto(input, key, Cipher.ENCRYPT_MODE);
    }

    public static byte[] symmetricDecrypt(final byte[] input, final SecretKey key) throws WrongPasswordException {
        return symmetricCrypto(input, key, Cipher.DECRYPT_MODE);
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
        return password.length >= 8;
    }
}
