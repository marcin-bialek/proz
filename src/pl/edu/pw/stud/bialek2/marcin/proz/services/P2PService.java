package pl.edu.pw.stud.bialek2.marcin.proz.services;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.models.MessageFactory;
import pl.edu.pw.stud.bialek2.marcin.proz.models.MessageType;
import pl.edu.pw.stud.bialek2.marcin.proz.models.P2PSession;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.crypto.SecretKey;


public class P2PService extends Thread {
    private static final int CLIENT_HELLO_CODE = 3;
    private static final int SERVER_HELLO_CODE = 4;
    private static final int CHAT_MESSAGE_CODE = 5;

    private P2PServiceDelegate delegate;
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private AtomicBoolean isListening;
    private Set<SocketChannel> connecting;
    private HashMap<SocketChannel, P2PSession> sessions;
    private int port;
    private byte[] encodedNick;
    private byte[] encodedPublicKey;
    private PrivateKey privateKey;

    public P2PService(P2PServiceDelegate delegate) {
        this.delegate = delegate;
        this.isListening = new AtomicBoolean(false);
        this.connecting = new HashSet<SocketChannel>();
        this.sessions = new HashMap<>();
        this.start();
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        try {
            return ((InetSocketAddress)this.serverSocket.getLocalAddress()).getPort();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public String getLocalHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch(UnknownHostException e) {
            e.printStackTrace();
        }

        return "?";
    }

    public void setCredentials(String nick, PublicKey publicKey, PrivateKey privateKey) {
        this.encodedNick = SecurityService.encodeString(nick);
        this.encodedPublicKey = publicKey.getEncoded();
        this.privateKey = privateKey;
    }

    private void writeAll(SocketChannel channel, ByteBuffer buffer, int size) throws Exception {
        int w = 0;
        int tw = 0;

        while(tw < size) {
            if((w = channel.write(buffer)) > 0) {
                tw += w;
                // System.out.println("sent: " + w + " (" + tw + "/" + size + ")");
            }
            else {
                try {
                    Thread.sleep(10);
                }
                catch(InterruptedException e) {}
            }
        }
    }

    private void send(P2PSession session, byte[] message) throws Exception {
        final ByteBuffer buffer1 = ByteBuffer.allocate(4);
        buffer1.putInt(4 + message.length);
        buffer1.rewind();
        final ByteBuffer buffer2 = ByteBuffer.wrap(message);
        this.writeAll(session.getChannel(), buffer1, 4);
        this.writeAll(session.getChannel(), buffer2, message.length);
    }

    private void sendClientHello(P2PSession session) {
        try {
            final int size = 17 + this.encodedNick.length + this.encodedPublicKey.length;
            final ByteBuffer payload = ByteBuffer.allocate(size);
            payload.putInt(CLIENT_HELLO_CODE);
            payload.putInt(this.port);
            payload.putInt(this.encodedNick.length);
            payload.put(this.encodedNick);
            payload.putInt(this.encodedPublicKey.length);
            payload.put(this.encodedPublicKey);
            payload.put((byte)0);

            this.send(session, payload.array());
            session.setSentClientHello();
        } 
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void sendServerHello(P2PSession session) {
        try {
            final SecretKey sessionKey = SecurityService.generateSecretKey();
            final byte[] encodedSessionKey = sessionKey.getEncoded();
            final byte[] encryptedSessionKey = SecurityService.asymmetricEncrypt(encodedSessionKey, session.getPeer().getPublicKey());

            final int size = 21 + this.encodedNick.length + this.encodedPublicKey.length + encryptedSessionKey.length;
            final ByteBuffer payload = ByteBuffer.allocate(size);
            payload.putInt(SERVER_HELLO_CODE);
            payload.putInt(this.encodedNick.length);
            payload.put(this.encodedNick);
            payload.putInt(this.encodedPublicKey.length);
            payload.put(this.encodedPublicKey);
            payload.putInt(encodedSessionKey.length);
            payload.putInt(encryptedSessionKey.length);
            payload.put(encryptedSessionKey);
            payload.put((byte)0);

            this.send(session, payload.array());
            session.setConnected(sessionKey);
        } 
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        final P2PSession session = message.getPeer().getSession();

        if(session.getState() == P2PSession.State.CONNECTED) {
            try {
                final byte[] encodedMessage = message.getValueAsBytes();
                final byte[] encryptedMessage = SecurityService.symmetricEncrypt(encodedMessage, session.getKey());
                final int size = 17 + encryptedMessage.length;
                final ByteBuffer payload = ByteBuffer.allocate(size);
                payload.putInt(CHAT_MESSAGE_CODE);
                payload.putInt(message.getType().getValue());
                payload.putInt(encodedMessage.length);
                payload.putInt(encryptedMessage.length);
                payload.put(encryptedMessage);
                payload.put((byte)0);
                this.send(session, payload.array());
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleClientHello(P2PSession session, ByteBuffer payload) {
        try {
            final int port = payload.getInt();

            final int nickSize = payload.getInt();
            byte[] encodedNick = new byte[nickSize];
            payload.get(encodedNick, 0, nickSize);

            final int keySize = payload.getInt();
            byte[] encodedKey = new byte[keySize];
            payload.get(encodedKey, 0, keySize);
            
            final String nick = SecurityService.decodeString(encodedNick);
            final String address = ((InetSocketAddress)session.getChannel().getRemoteAddress()).getHostString();
            final PublicKey publicKey = SecurityService.generatePublicKey(encodedKey);
            final Peer peer = new Peer(nick, address, port, publicKey);
            peer.setSession(session);
            session.setPeer(peer);

            session.setReceivedClientHello();
            this.delegate.p2pServiceIncomingConnection(peer);
        }   
        catch(Exception e) {
            System.out.println("[Warning] Broken client hello.");
        }
    }

    private void handleServerHello(P2PSession session, ByteBuffer payload) {
        try {
            final int nickSize = payload.getInt();
            final byte[] encodedNick = new byte[nickSize];
            payload.get(encodedNick, 0, nickSize);

            final int serverKeySize = payload.getInt();
            final byte[] encodedServerKey = new byte[serverKeySize];
            payload.get(encodedServerKey, 0, serverKeySize);

            final int encodedSessionKeySize = payload.getInt();
            final int encryptedSessionKeySize = payload.getInt();
            final byte[] encryptedSessionKey = new byte[encryptedSessionKeySize];
            payload.get(encryptedSessionKey, 0, encryptedSessionKeySize);
            final byte[] decryptedSessionKey = SecurityService.asymmetricDecrypt(encryptedSessionKey, this.privateKey);
            final byte[] encodedSessionKey = Arrays.copyOfRange(decryptedSessionKey, 0, encodedSessionKeySize);

            final String nick = SecurityService.decodeString(encodedNick);
            final PublicKey serverKey = SecurityService.generatePublicKey(encodedServerKey);
            final SecretKey sessionKey = SecurityService.generateSecretKey(encodedSessionKey);

            final Peer peer = session.getPeer();

            if(peer.getPublicKeyAsString() == null) {
                peer.setNick(nick);
                peer.setPublicKey(serverKey);
                session.setConnected(sessionKey);
                this.delegate.p2pServicePeerDidAccept(peer);
            }
            else if(peer.getPublicKeyAsString().equals(SecurityService.keyToString(serverKey))) {
                session.setConnected(sessionKey);
                this.delegate.p2pServicePeerDidAccept(peer);
            }
            else {
                this.sessions.remove(session.getChannel());
                session.getChannel().close();
                session.setDisconnected();

                System.out.println("[Warning] Server's public key is different than expected. Connection closed.");
            }
        }
        catch(Exception e) {
            System.out.println("[Warning] Broken server hello.");
        }
    }

    private void handleChatMessage(P2PSession session, ByteBuffer payload) {
        try {
            final MessageType type = MessageType.fromValue(payload.getInt());
            final int encodedMessageSize = payload.getInt();
            final int encryptedMessageSize = payload.getInt();
            byte[] encryptedMessage = new byte[encryptedMessageSize];
            payload.get(encryptedMessage, 0, encryptedMessageSize);

            final byte[] decryptedMessage = SecurityService.symmetricDecrypt(encryptedMessage, session.getKey());
            final byte[] encodedMessage = Arrays.copyOfRange(decryptedMessage, 0, encodedMessageSize);
            final Message message = MessageFactory.createMessage(type, 0, session.getPeer(), true, LocalDateTime.now(), encodedMessage);

            this.delegate.p2pServiceDidReceiveMessage(message);
        }
        catch(Exception e) {
            System.out.println("[Warning] Broken chat message.");
        }
    }

    private void handleMessage(P2PSession session, byte[] message) {
        ByteBuffer payload = ByteBuffer.wrap(message);
        int type = 0;

        try {
            type = payload.getInt();
        }
        catch(Exception e) {
            e.printStackTrace();
            return;
        }

        switch(type) {
            case CLIENT_HELLO_CODE: 
                this.handleClientHello(session, payload);
                break;

            case SERVER_HELLO_CODE: 
                this.handleServerHello(session, payload);
                break;

            case CHAT_MESSAGE_CODE:
                this.handleChatMessage(session, payload);
                break;

            default:
                break;
        }
    }

    private void checkIfConnected() {
        synchronized(this.connecting) {
            this.connecting.removeIf(socket -> {
                try {
                    if(socket.finishConnect()) {
                        final P2PSession session = this.sessions.get(socket);
                        this.sendClientHello(session); 
                        return true;
                    }
                }
                catch(Exception e) {
                    return true;
                }

                return false;
            });
        }
    }

    private void handleSelectedKey(SelectionKey key) {
        try {
            if(key.isAcceptable()) {
                final SocketChannel client = this.serverSocket.accept();
                client.configureBlocking(false);
                client.register(this.selector, SelectionKey.OP_READ);

                final P2PSession session = new P2PSession();
                session.setConnecting(client);

                synchronized(this.sessions) {
                    this.sessions.put(client, session);
                }
            }

            if(key.isReadable()) {
                final ByteBuffer buffer = ByteBuffer.allocate(1024);
                final ByteArrayOutputStream message = new ByteArrayOutputStream();
                final SocketChannel client = (SocketChannel)key.channel();
                final P2PSession session = this.sessions.get(client);
                int read, totalRead = 0;

                if((totalRead = client.read(buffer)) >= 4) {
                    buffer.flip();
                    final int packetSize = buffer.getInt();
                    message.write(buffer.array(), 0, totalRead);
                    buffer.clear();

                    while(packetSize > totalRead) {
                        if((read = client.read(buffer)) > 0) {
                            totalRead += read;
                            buffer.flip();
                            message.write(buffer.array(), 0, read);
                            buffer.clear();
                        }
                        else {
                            try {
                                Thread.sleep(10);
                            }
                            catch(InterruptedException e) {}
                        }
                    }

                    byte[] sliced = Arrays.copyOfRange(message.toByteArray(), 4, totalRead);
                    this.handleMessage(session, sliced);
                }
                else {
                    client.close();
                    session.setDisconnected();
                    this.sessions.remove(client);
                    this.delegate.p2pServicePeerDisconnected(session.getPeer());
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();

            try {
                final SocketChannel client = (SocketChannel)key.channel();
                final P2PSession session = this.sessions.get(client);
                session.setDisconnected();
                this.sessions.remove(client);
                client.close();
                this.delegate.p2pServicePeerDisconnected(session.getPeer());
            }
            catch(Exception f) {
                f.printStackTrace();
            }
        } 
    }

    private void startServer() {
        try {
            this.selector = Selector.open();
            this.serverSocket = ServerSocketChannel.open();
            this.serverSocket.bind(new InetSocketAddress(this.port));
            this.serverSocket.configureBlocking(false);
            this.serverSocket.register(this.selector, SelectionKey.OP_ACCEPT);
            isListening.set(true);
            this.delegate.p2pServiceReady();

            while(isListening.get()) {
                synchronized(this.selector) {
                    this.selector.select(100);
                }

                if(this.connecting.size() > 0) {
                    this.checkIfConnected();
                }
                
                final Set<SelectionKey> selectedKeys = this.selector.selectedKeys();

                if(selectedKeys.size() > 0) {
                    final Iterator<SelectionKey> iterator = selectedKeys.iterator();

                    while(iterator.hasNext()) {
                        this.handleSelectedKey(iterator.next());
                        iterator.remove();
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            this.delegate.p2pServiceServerError(this.port);
        }
    }

    private Object m = new Object();

    @Override
    public void run() {
        try {
            while(true) {
                synchronized(m) {
                    this.m.wait();
                    this.startServer();
                }
            }
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void startListening() {
        synchronized(m) {
            this.m.notify();
        }
    }

    public void stopListening() {
        this.isListening.set(false);
    }
    
    public void connect(Peer peer) {
        if(peer.getSession().getState() != P2PSession.State.DISCONNECTED) {
            return;
        }

        try {
            final SocketChannel socket = SocketChannel.open();
            socket.configureBlocking(false);
            
            final P2PSession session = peer.getSession();
            session.setConnecting(socket, peer);

            synchronized(this.sessions) {
                this.sessions.put(socket, session);
            }

            if(socket.connect(new InetSocketAddress(peer.getAddress(), peer.getPort()))) {
                this.sendClientHello(session); 
            }
            else {
                synchronized(this.connecting) {
                    this.connecting.add(socket);
                }
            }
            
            synchronized(this.selector) {
                socket.register(this.selector, SelectionKey.OP_READ);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void acceptConnection(Peer peer) {
        this.sendServerHello(peer.getSession());
    }

    public void rejectConnection(Peer peer) {
        try {
            final P2PSession session = peer.getSession();
            final SocketChannel channel = session.getChannel();
            session.setDisconnected();
            this.sessions.remove(channel);
            channel.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isValidPort(int port) {
        return port > 0 && port < 65536;
    }
}

