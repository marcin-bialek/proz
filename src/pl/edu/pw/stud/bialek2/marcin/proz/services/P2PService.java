package pl.edu.pw.stud.bialek2.marcin.proz.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;


public class P2PService extends Thread {
    private P2PServiceDelegate delegate;
    private int port;
    private Selector selector;
    private ServerSocketChannel serverSocket;
    private AtomicBoolean isListening;
    private ByteBuffer buffer;
    private ByteArrayOutputStream message;
    private Set<SocketChannel> connecting;

    public P2PService(P2PServiceDelegate delegate, int port) {
        this.delegate = delegate;
        this.port = port;
        this.isListening = new AtomicBoolean(false);
        this.buffer = ByteBuffer.allocate(128);
        this.message = new ByteArrayOutputStream();
        this.connecting = new HashSet<SocketChannel>();
    }

    private void checkIfConnected() {
        for(SocketChannel socket : this.connecting) {
            try {
                if(socket.finishConnect()) {
                    System.out.println("connected: " + socket.socket().getRemoteSocketAddress());
                    
                    synchronized(this.connecting) {
                        this.connecting.remove(socket);
                    }
                }
            }
            catch(IOException e) {
                System.out.println("not connected: " + socket.socket().getRemoteSocketAddress());

                synchronized(this.connecting) {
                    this.connecting.remove(socket);
                }
            }
        }
    }

    private void parseMessage(byte[] message) {
        System.out.println("Received: \"" + new String(message) + "\"");
    }

    private void handleSelectedKey(SelectionKey key) throws IOException {
        if(key.isAcceptable()) {
            final SocketChannel client = this.serverSocket.accept();
            client.configureBlocking(false);
            client.register(this.selector, SelectionKey.OP_READ);
        }

        if(key.isReadable()) {
            final SocketChannel client = (SocketChannel)key.channel();

            while(client.read(this.buffer) > 0) {
                buffer.flip();
                this.message.write(this.buffer.array());
                buffer.clear();
            }

            if(this.message.size() > 0) {
                this.parseMessage(this.message.toByteArray());
            }
            else {
                client.close();
            }

            this.message.reset();
        }
    }

    @Override
    public void run() {
        try {
            this.selector = Selector.open();
            this.serverSocket = ServerSocketChannel.open();
            this.serverSocket.bind(new InetSocketAddress(this.port));
            this.serverSocket.configureBlocking(false);
            this.serverSocket.register(this.selector, SelectionKey.OP_ACCEPT);
            isListening.set(true);

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
        }
    }

    public void stopListening() {
        this.isListening.set(false);
    }
    
    public void connect(Peer peer) {
        try {
            final SocketChannel socket = SocketChannel.open();
            socket.configureBlocking(false);
            
            if(socket.connect(new InetSocketAddress(peer.getLastAddress(), peer.getLastPort())) == false) {
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
}
