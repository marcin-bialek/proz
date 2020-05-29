package pl.edu.pw.stud.bialek2.marcin.proz.services;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.models.User;

import java.awt.Dimension;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.prefs.Preferences;
import javax.crypto.SecretKey;


public class UserService {
    private static final String SECRET_KEY_KEY = "secret-key";
    private static final String DATABASE_ID_KEY = "database-id";
    private static final String NICK_KEY = "nick";
    private static final String PORT_KEY = "port";
    private static final String WINDOW_WIDTH_KEY = "window-width";
    private static final String WINDOW_HEIGHT_KEY = "window-height";
    private static final String PRIVATE_KEY_KEY = "private-key";
    private static final String PUBLIC_KEY_KEY = "public-key";

    private UserServiceListener listener;
    private Preferences preferences;
    private User user;

    public UserService(UserServiceListener listener) {
        this.listener = listener;
        this.preferences = Preferences.userRoot().node(this.getClass().getName());
        System.out.println(this.getClass().getName());
    }

    public void createUser(final String nick, final int port, final char[] password) {
        final SecretKey secretKey = SecurityService.generateSecretKey(password);
        final KeyPair keyPair = SecurityService.generateKeyPair();
        final PrivateKey privateKey = keyPair.getPrivate();
        final PublicKey publicKey = keyPair.getPublic();
        final Dimension windowSize = new Dimension(App.DEFAULT_WINDOW_WIDTH, App.DEFAULT_WINDOW_HEIGHT);

        this.user = new User(secretKey, privateKey, publicKey, nick, port, windowSize);
        this.saveUser();
        this.listener.userServiceDidCreateUser(this.user);
    }

    public void loadUser(final char[] password) {
        if(this.preferences.get(NICK_KEY, null) == null) {
            this.listener.userServiceNeedsUser();
            return;
        }

        final byte[] secretKeyRaw = this.preferences.getByteArray(SECRET_KEY_KEY, null);
        SecretKey secretKey;

        if(secretKeyRaw == null) {
            if(password == null) {
                this.listener.userServiceNeedsPassword();
                return;
            }
            
            secretKey = SecurityService.generateSecretKey(password);
        }
        else {
            secretKey = SecurityService.generateSecretKey(secretKeyRaw);
        }

        try {
            final int databaseId = this.getSecureInt(DATABASE_ID_KEY, -1, secretKey);
            final String nick = this.getSecureString(NICK_KEY, null, secretKey);
            final int port = this.getSecureInt(PORT_KEY, App.DEFAULT_PORT, secretKey);
            final int windowWidth = this.getSecureInt(WINDOW_WIDTH_KEY, App.DEFAULT_WINDOW_WIDTH, secretKey);
            final int windowHeight = this.getSecureInt(WINDOW_HEIGHT_KEY, App.DEFAULT_WINDOW_HEIGHT, secretKey);
            final Dimension windowSize = new Dimension(windowWidth, windowHeight);
            final byte[] privateKeyRaw = this.getSecureByteArray(PRIVATE_KEY_KEY, null, secretKey);
            final byte[] publicKeyRaw = this.getSecureByteArray(PUBLIC_KEY_KEY, null, secretKey);

            if(databaseId == -1 || nick == null || privateKeyRaw == null || publicKeyRaw == null) {
                this.listener.userServiceNeedsUser();
                return;
            }

            final KeyPair keyPair = SecurityService.generateKeyPair(privateKeyRaw, publicKeyRaw);
            final PrivateKey privateKey = keyPair.getPrivate();
            final PublicKey publicKey = keyPair.getPublic();
            this.user = new User(secretKey, privateKey, publicKey, nick, port, windowSize);
            this.user.setId(databaseId);
            this.listener.userServiceDidLoadUser(this.user);
        }
        catch(WrongPasswordException e) {
            this.listener.userServiceWrongPassword();
        }
    }

    public void saveUser() {
        try {
            final SecretKey secretKey = this.user.getSecretKey();
            this.putSecureInt(DATABASE_ID_KEY, this.user.getId(), secretKey);
            this.putSecureString(NICK_KEY, this.user.getNick(), secretKey);
            this.putSecureInt(PORT_KEY, this.user.getLastPort(), secretKey);
            this.putSecureInt(WINDOW_WIDTH_KEY, this.user.getWindowSize().width, secretKey);
            this.putSecureInt(WINDOW_HEIGHT_KEY, this.user.getWindowSize().height, secretKey);
            this.putSecureByteArray(PRIVATE_KEY_KEY, this.user.getPrivateKey().getEncoded(), secretKey);
            this.putSecureByteArray(PUBLIC_KEY_KEY, this.user.getPublicKey().getEncoded(), secretKey);
        }
        catch(WrongPasswordException e) {
            this.listener.userServiceWrongPassword();
        }
    }

    public User getUser() {
        return this.user;
    }

    public static boolean isValidNick(final String nick) {
        final String t = nick.trim();
        final int c = t.replaceAll("\\s+", "").length(); 
        return nick.length() == t.length() && c >= 8;
    }

    private void putSecureString(String key, String value, SecretKey secretKey) throws WrongPasswordException {
        final byte[] encoded = SecurityService.encodeString(value);
        final byte[] encrypted = SecurityService.symmetricEncrypt(encoded, secretKey);
        this.preferences.putByteArray(key, encrypted);
    }

    private String getSecureString(String key, String defaultValue, SecretKey secretKey) throws WrongPasswordException {
        final byte[] value = this.preferences.getByteArray(key, null);

        if(value == null) {
            return defaultValue;
        }

        byte[] decrypted = SecurityService.symmetricDecrypt(value, secretKey);
        return SecurityService.decodeString(decrypted);
    }

    private void putSecureInt(String key, int value, SecretKey secretKey) throws WrongPasswordException {
        final byte[] encoded = SecurityService.encodeInt(value);
        final byte[] encrypted = SecurityService.symmetricEncrypt(encoded, secretKey);
        this.preferences.putByteArray(key, encrypted);
    }

    private int getSecureInt(String key, int defaultValue, SecretKey secretKey) throws WrongPasswordException {
        final byte[] value = this.preferences.getByteArray(key, null);

        if(value == null) {
            return defaultValue;
        }

        final byte[] decrypted = SecurityService.symmetricDecrypt(value, secretKey);
        return SecurityService.decodeInt(decrypted);
    }

    private void putSecureByteArray(String key, byte[] value, SecretKey secretKey) throws WrongPasswordException {
        final byte[] encrypted = SecurityService.symmetricEncrypt(value, secretKey);
        this.preferences.putByteArray(key, encrypted);
    }

    private byte[] getSecureByteArray(String key, byte[] defaultValue, SecretKey secretKey) throws WrongPasswordException {
        final byte[] value = this.preferences.getByteArray(key, null);

        if(value == null) {
            return defaultValue;
        }

        return SecurityService.symmetricDecrypt(value, secretKey);
    }
}

