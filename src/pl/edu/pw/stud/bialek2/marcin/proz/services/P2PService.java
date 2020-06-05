package pl.edu.pw.stud.bialek2.marcin.proz.services;

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
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;


public class P2PService extends Thread {
    private P2PServiceDelegate delegate;
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private AtomicBoolean isListening;
    private ByteBuffer buffer;
    private ByteArrayOutputStream message;
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
        this.buffer = ByteBuffer.allocate(128);
        this.message = new ByteArrayOutputStream();
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
        } 
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void sendServerHello(P2PSession session) {
        // TODO
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

            if(this.delegate != null) {
                this.delegate.p2pServiceIncomingConnection(peer);
            }
        }   
        catch(Exception e) {
            System.out.println("broken client hello");
        }
    }

    private void handleServerHello(P2PSession session, ByteBuffer payload) {

    }

    private void handleMessage(P2PSession session, byte[] message) {
        final String encoded = new String(message);
        System.out.println("received: " + encoded);
        
        final ByteBuffer payload = ByteBuffer.wrap(SecurityService.base64Decode(encoded));
        final int type = payload.getInt();
        System.out.println("message type: " + type);

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
            default:
                break;
        }
    }

    private void checkIfConnected() {
        for(SocketChannel socket : this.connecting) {
            try {
                if(socket.finishConnect()) {
                    synchronized(this.connecting) {
                        this.connecting.remove(socket);
                    }

                    final P2PSession session = this.sessions.get(socket);
                    this.sendClientHello(session); 
                }
            }
            catch(IOException e) {
                synchronized(this.connecting) {
                    this.connecting.remove(socket);
                }
            }
        }
    }

    private void handleSelectedKey(SelectionKey key) throws IOException {
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
            final SocketChannel client = (SocketChannel)key.channel();
            final P2PSession session = this.sessions.get(client);
            int read, totalRead = 0;

            while((read = client.read(this.buffer)) > 0) {
                totalRead += read;
                buffer.flip();
                this.message.write(this.buffer.array());
                buffer.clear();
            }

            if(this.message.size() > 0) {
                byte[] sliced = Arrays.copyOfRange(message.toByteArray(), 0, totalRead);
                this.handleMessage(session, sliced);
            }
            else {
                client.close();
                session.setDisconnected();
                this.sessions.remove(client);

                if(this.delegate != null) {
                    this.delegate.p2pServicePeerDisconnected(session.getPeer());
                }
            }

            this.message.reset();
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

            if(this.delegate != null) {
                this.delegate.p2pServiceReady();
            }

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

            if(this.delegate != null) {
                this.delegate.p2pServiceServerError();
            }
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

    public void acceptConnection() {

    }
}
