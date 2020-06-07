package pl.edu.pw.stud.bialek2.marcin.proz.services;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.models.MessageFactory;
import pl.edu.pw.stud.bialek2.marcin.proz.models.MessageType;
import pl.edu.pw.stud.bialek2.marcin.proz.models.P2PSession;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.SecretKey;


public class P2PService extends Thread {
    private P2PServiceDelegate delegate;
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private AtomicBoolean isListening;
    private Set<SocketChannel> connecting;
    private HashMap<SocketChannel, P2PSession> sessions;
    private int port;
    private byte[] encodedNick;
    private byte[] encodedPublicKey;
    private PublicKey publicKey;
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

    public void setCredentials(String nick, PublicKey publicKey, PrivateKey privateKey) {
        this.encodedNick = SecurityService.encodeString(nick);
        this.encodedPublicKey = publicKey.getEncoded();
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    private void send(P2PSession session, byte[] message) throws IOException {
        final String encoded = SecurityService.base64Encode(message);
        final ByteBuffer buffer = ByteBuffer.wrap(encoded.getBytes());
        session.getChannel().write(buffer);
    }

    private void sendPing(P2PSession session) {

    }

    private void sendPong(P2PSession session) {
        try {
            final ByteBuffer payload = ByteBuffer.allocate(4);
            payload.putInt(2);
            payload.put((byte)0);
            this.send(session, payload.array());
        } 
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void sendClientHello(P2PSession session) {
        try {
            final int size = 17 + this.encodedNick.length + this.encodedPublicKey.length;
            final ByteBuffer payload = ByteBuffer.allocate(size);
            payload.putInt(3);
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
            //final byte[] encryptedSessionKey = SecurityService.en

            final int size = 17 + this.encodedNick.length + this.encodedPublicKey.length + encodedSessionKey.length;
            final ByteBuffer payload = ByteBuffer.allocate(size);
            payload.putInt(4);
            payload.putInt(this.encodedNick.length);
            payload.put(this.encodedNick);
            payload.putInt(this.encodedPublicKey.length);
            payload.put(this.encodedPublicKey);
            payload.putInt(encodedSessionKey.length);
            payload.put(encodedSessionKey);
            payload.put((byte)0);

            this.send(session, payload.array());
            session.setConnected(sessionKey);
        } 
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void handlePong(P2PSession session) {

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
            final String address = ((InetSocketAddress)session.getChannel().getRemoteAddress()).getHostName();
            final PublicKey publicKey = SecurityService.generatePublicKey(encodedKey);
            final Peer peer = new Peer(nick, address, port, publicKey);
            peer.setSession(session);
            session.setPeer(peer);

            session.setReceivedClientHello();
            this.delegate.p2pServiceIncomingConnection(peer);
        }   
        catch(Exception e) {
            System.out.println("broken client hello");
        }
    }

    private void handleServerHello(P2PSession session, ByteBuffer payload) {
        try {
            final int nickSize = payload.getInt();
            byte[] encodedNick = new byte[nickSize];
            payload.get(encodedNick, 0, nickSize);

            final int serverKeySize = payload.getInt();
            byte[] encodedServerKey = new byte[serverKeySize];
            payload.get(encodedServerKey, 0, serverKeySize);

            final int sessionKeySize = payload.getInt();
            byte[] encodedSessionKey = new byte[sessionKeySize];
            payload.get(encodedSessionKey, 0, sessionKeySize);

            final String nick = SecurityService.decodeString(encodedNick);
            final PublicKey serverKey = SecurityService.generatePublicKey(encodedServerKey);
            final SecretKey sessionKey = SecurityService.generateSecretKey(encodedSessionKey);

            final Peer peer = session.getPeer();
            peer.setNick(nick);
            peer.setPublicKey(serverKey);
            session.setConnected(sessionKey);
            this.delegate.p2pServicePeerDidAccept(peer);
        }
        catch(Exception e) {
            System.out.println("broken server hello");
        }
    }

    private void handleChatMessage(P2PSession session, ByteBuffer payload) {
        try {
            final MessageType type = MessageType.fromValue(payload.getInt());

            final int messageSize = payload.getInt();
            byte[] encryptedMessage = new byte[messageSize];
            payload.get(encryptedMessage, 0, messageSize);

            final byte[] encodedMessage = SecurityService.symmetricDecrypt(encryptedMessage, session.getKey());
            final Message message = MessageFactory.createMessage(type, 0, session.getPeer(), true, LocalDateTime.now(), encodedMessage);

            this.delegate.p2pServiceDidReceiveMessage(message);
        }
        catch(Exception e) {
            System.out.println("broken chat message hello");
        }
    }

    private void handleMessage(P2PSession session, byte[] message) {
        ByteBuffer payload;
        int type;

        try {
            final String encoded = new String(message);
            System.out.println("received: " + encoded);
            
            payload = ByteBuffer.wrap(SecurityService.base64Decode(encoded));
            type = payload.getInt();
            System.out.println("message type: " + type);
        }
        catch(Exception e) {
            e.printStackTrace();
            return;
        }

        switch(type) {
            case 1: // ping
                this.sendPong(session);
                break;
            
            case 2: // pong 
                this.handlePong(session);
                break;

            case 3: // client hello 
                this.handleClientHello(session, payload);
                break;

            case 4: // server hello 
                this.handleServerHello(session, payload);
                break;

            case 5: // message 
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
                catch(IOException e) {
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
                final ByteBuffer buffer = ByteBuffer.allocate(128);
                final ByteArrayOutputStream message = new ByteArrayOutputStream();
                final SocketChannel client = (SocketChannel)key.channel();
                final P2PSession session = this.sessions.get(client);
                int read, totalRead = 0;

                while((read = client.read(buffer)) > 0) {
                    totalRead += read;
                    buffer.flip();
                    message.write(buffer.array());
                    buffer.clear();
                }

                if(message.size() > 0) {
                    byte[] sliced = Arrays.copyOfRange(message.toByteArray(), 0, totalRead);
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
        catch(IOException e) {
            e.printStackTrace();

            try {
                final SocketChannel client = (SocketChannel)key.channel();
                final P2PSession session = this.sessions.get(client);
                session.setDisconnected();
                this.sessions.remove(client);
                client.close();
                this.delegate.p2pServicePeerDisconnected(session.getPeer());
            }
            catch(IOException f) {
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
        catch(IOException e) {
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
                    System.out.println("waiting...");
                    this.m.wait();
                    System.out.println("starting server...");
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
        if(!peer.getSession().getState().equals(P2PSession.State.DISCONNECTED)) {
            return;
        }

        System.out.println("connecting to " + peer.getNick());

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
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptConnection(Peer peer) {
        final P2PSession.State state = peer.getSession().getState();
        this.sendServerHello(peer.getSession());
    }

    public void sendMessage(Message message) {
        final P2PSession session = message.getPeer().getSession();

        if(session.getState() == P2PSession.State.CONNECTED) {
            try {
                final byte[] encodedMessage = message.getValueAsBytes();
                final byte[] encryptedMessage = SecurityService.symmetricEncrypt(encodedMessage, session.getKey());
                final int size = 13 + encryptedMessage.length;
                final ByteBuffer payload = ByteBuffer.allocate(size);
                payload.putInt(5);
                payload.putInt(message.getType().getValue());
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
}
