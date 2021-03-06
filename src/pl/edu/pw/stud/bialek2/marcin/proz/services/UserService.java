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
    private static final String DB_FILENAME_KEY = "db-filename";
    private static final String WINDOW_WIDTH_KEY = "window-width";
    private static final String WINDOW_HEIGHT_KEY = "window-height";
    private static final String PRIVATE_KEY_KEY = "private-key";
    private static final String PUBLIC_KEY_KEY = "public-key";

    private UserServiceDelegate delegate;
    private Preferences preferences;
    private User user;

    public UserService(UserServiceDelegate delegate, String preferencesPath) {
        this.delegate = delegate;
        this.preferences = Preferences.userRoot().node(preferencesPath);
    }

    public void createUser(final String nick, final char[] password, final int port, final String dbFilename) {
        final SecretKey secretKey = SecurityService.generateSecretKey(password);
        final KeyPair keyPair = SecurityService.generateKeyPair();
        final PrivateKey privateKey = keyPair.getPrivate();
        final PublicKey publicKey = keyPair.getPublic();
        final Dimension windowSize = new Dimension(App.DEFAULT_WINDOW_WIDTH, App.DEFAULT_WINDOW_HEIGHT);

        this.user = new User(secretKey, privateKey, publicKey, nick, port, dbFilename, windowSize);
        this.saveUser();
        this.delegate.userServiceDidCreateUser(this.user);
    }

    public boolean loadUser(final char[] password) {
        if(this.preferences.get(NICK_KEY, null) == null) {
            this.delegate.userServiceNeedsUser();
            return true;
        }

        final byte[] secretKeyRaw = this.preferences.getByteArray(SECRET_KEY_KEY, null);
        SecretKey secretKey;

        if(secretKeyRaw == null) {
            if(password == null) {
                this.delegate.userServiceNeedsPassword();
                return true;
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
            final String dbFilename = this.getSecureString(DB_FILENAME_KEY, App.DEFAULT_DATABASE_FILE_NAME, secretKey);
            final int windowWidth = this.getSecureInt(WINDOW_WIDTH_KEY, App.DEFAULT_WINDOW_WIDTH, secretKey);
            final int windowHeight = this.getSecureInt(WINDOW_HEIGHT_KEY, App.DEFAULT_WINDOW_HEIGHT, secretKey);
            final Dimension windowSize = new Dimension(windowWidth, windowHeight);
            final byte[] privateKeyRaw = this.getSecureByteArray(PRIVATE_KEY_KEY, null, secretKey);
            final byte[] publicKeyRaw = this.getSecureByteArray(PUBLIC_KEY_KEY, null, secretKey);

            if(databaseId == -1 || nick == null || privateKeyRaw == null || publicKeyRaw == null) {
                this.delegate.userServiceNeedsUser();
                return true;
            }

            final KeyPair keyPair = SecurityService.generateKeyPair(privateKeyRaw, publicKeyRaw);
            final PrivateKey privateKey = keyPair.getPrivate();
            final PublicKey publicKey = keyPair.getPublic();
            this.user = new User(secretKey, privateKey, publicKey, nick, port, dbFilename, windowSize);
            this.user.setId(databaseId);
            this.delegate.userServiceDidLoadUser(this.user);
        }
        catch(WrongPasswordException e) {
            this.delegate.userServiceWrongPassword();
            return false;
        }

        return true;
    }

    public void saveUser() {
        if(this.user == null) {
            return;
        }

        try {
            final SecretKey secretKey = this.user.getSecretKey();
            this.putSecureInt(DATABASE_ID_KEY, this.user.getId(), secretKey);
            this.putSecureString(NICK_KEY, this.user.getNick(), secretKey);
            this.putSecureInt(PORT_KEY, this.user.getPort(), secretKey);
            this.putSecureString(DB_FILENAME_KEY, this.user.getDBFilename(), secretKey);
            this.putSecureInt(WINDOW_WIDTH_KEY, this.user.getWindowSize().width, secretKey);
            this.putSecureInt(WINDOW_HEIGHT_KEY, this.user.getWindowSize().height, secretKey);
            this.putSecureByteArray(PRIVATE_KEY_KEY, this.user.getPrivateKey().getEncoded(), secretKey);
            this.putSecureByteArray(PUBLIC_KEY_KEY, this.user.getPublicKey().getEncoded(), secretKey);
        }
        catch(WrongPasswordException e) {
            this.delegate.userServiceWrongPassword();
        }
    }

    public void deleteUser() {
        try {
            this.preferences.removeNode();
            this.preferences.flush();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return this.user;
    }

    public static boolean isValidNick(final String nick) {
        final String t = nick.trim();
        final int c = t.replaceAll("\\s+", "").length(); 
        return nick.length() == t.length() && c >= 6;
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

