package pl.edu.pw.stud.bialek2.marcin.proz;

import pl.edu.pw.stud.bialek2.marcin.proz.controllers.HomeController;
import pl.edu.pw.stud.bialek2.marcin.proz.controllers.HomeControllerDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.controllers.PasswordController;
import pl.edu.pw.stud.bialek2.marcin.proz.controllers.PasswordControllerDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.controllers.PeerConnectingController;
import pl.edu.pw.stud.bialek2.marcin.proz.controllers.PeerConnectingControllerDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.controllers.SetupController;
import pl.edu.pw.stud.bialek2.marcin.proz.controllers.SetupControllerDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;
import pl.edu.pw.stud.bialek2.marcin.proz.models.User;
import pl.edu.pw.stud.bialek2.marcin.proz.services.DatabaseService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.DatabaseServiceDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.services.P2PService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.P2PServiceDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.services.SecurityService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.SecurityServiceStaticDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.services.UserService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.UserServiceDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.views.PeerConnectingWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.home.HomeWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.password.PasswordWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.setup.SetupWindow;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.SwingUtilities;


public final class App implements UserServiceDelegate, 
                                  SecurityServiceStaticDelegate, 
                                  DatabaseServiceDelegate, 
                                  P2PServiceDelegate, 
                                  PasswordControllerDelegate, 
                                  SetupControllerDelegate, 
                                  HomeControllerDelegate, 
                                  PeerConnectingControllerDelegate {

    public static final String APP_DISPLAY_NAME = "Chat";
    public static final Color BACKGROUND_COLOR = new Color(30, 31, 38);
    public static final Color PRIMARY_COLOR = new Color(40, 54, 85);
    public static final Color SECONDARY_COLOR = new Color(77, 100, 141);
    public static final Color ACCENT_COLOR = new Color(208, 225, 249);
    public static final int DEFAULT_PORT = 8765;
    public static final int DEFAULT_WINDOW_WIDTH = 700;
    public static final int DEFAULT_WINDOW_HEIGHT = 500;
    public static final String DATABASE_FILE_NAME = "chat.db";

    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final SecurityService securityService;
    private final UserService userService; 
    private final DatabaseService databaseService;
    private final P2PService p2pService;

    private HashMap<Integer, Peer> peers;

    public App() {
        SecurityService.setStaticDelegate(this);
        this.securityService = new SecurityService();
        this.userService = new UserService(this);
        this.databaseService = new DatabaseService(this);
        this.p2pService = new P2PService(this);
    }

    public void runTaskExecutorLoop() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                invokeLater(() -> {
                    exit(); 
                });
            }
        });

        try {
            while(true) {
                this.taskQueue.take().run();
            }
        } 
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        this.p2pService.stopListening();
        this.databaseService.close();
        this.userService.saveUser();
        System.out.println("exit");
        System.exit(0);
    }

    private void invokeLater(Runnable runnable) {
        try {
            this.taskQueue.put(runnable);
        } 
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void printUser(User user) {
        System.out.println("Nick:        " + user.getNick());
        System.out.println("Port:        " + user.getPort());
        System.out.println("Width:       " + user.getWindowSize().width);
        System.out.println("Height:      " + user.getWindowSize().height);
        System.out.println("Secret key:  " + (new String(SecurityService.bytes2Hex(user.getSecretKey().getEncoded()))).substring(0, 32) + "...");
        System.out.println("Private key: " + (new String(SecurityService.bytes2Hex(user.getPrivateKey().getEncoded()))).substring(0, 32) + "...");
        System.out.println("Public key:  " + (new String(SecurityService.bytes2Hex(user.getPublicKey().getEncoded()))).substring(0, 32) + "...");
    }

    private void peersLoaded(User user, int port) {
        this.p2pService.setPort(port);
        this.p2pService.setCredentials(user.getNick(), user.getPublicKey(), user.getPrivateKey());
        this.p2pService.startListening();
    }

    private void userLoaded(User user) {
        this.printUser(user);

        this.databaseService.load(DATABASE_FILE_NAME);
        this.peers = this.databaseService.getPeers();

        this.peersLoaded(user, user.getPort());
    }

    @Override
    public void userServiceNeedsUser() {
        SwingUtilities.invokeLater(() -> {
            final SetupWindow view = new SetupWindow();
            final SetupController controller = new SetupController(view);
            controller.setDelegate(this);
        });
    }

    @Override
    public void userServiceNeedsPassword() {
        SwingUtilities.invokeLater(() -> {
            final PasswordWindow view = new PasswordWindow();
            final PasswordController controller = new PasswordController(view);
            controller.setDelegate(this);
        });
    }

    @Override
    public void userServiceWrongPassword() {
        
    }

    @Override 
    public void userServiceDidCreateUser(User user) {
        this.userLoaded(user);
    }

    @Override 
    public void userServiceDidLoadUser(User user) {
        this.userLoaded(user);
    }

    @Override
    public void securityServiceNoSuchAlgorithm() {
        System.err.println("No such algorithm");
        this.exit();
    }

    @Override
    public void databaseServiceSQLError() {
        System.out.println("sql error");
    }

    @Override
    public void p2pServiceServerError() {
        this.invokeLater(() -> {
            System.out.println("server error, trying another port");
            this.peersLoaded(this.userService.getUser(), 1234);
        });
    }

    @Override
    public void p2pServiceReady() {
        this.invokeLater(() -> {
            for(Map.Entry<Integer, Peer> entry : this.peers.entrySet()) {
                this.p2pService.connect(entry.getValue());
            }

            SwingUtilities.invokeLater(() -> {
                final HomeWindow view = new HomeWindow(userService.getUser().getWindowSize());
                final HomeController controller = new HomeController(view, peers);
                controller.setDelegate(this);
            });
        });
    }

    @Override
    public void p2pServiceIncomingConnection(Peer peer) {
        SwingUtilities.invokeLater(() -> {
            final PeerConnectingWindow view = new PeerConnectingWindow();
            final PeerConnectingController controller = new PeerConnectingController(view, peer);
            controller.setDelegate(this);
        });
    }

    @Override
    public void p2pServicePeerDisconnected(Peer peer) {
        System.out.println("disconnected: " + peer.getAddress() + ":" + peer.getPort());
    }

    @Override
    public void setupControllerDidExit(SetupController sender) {
        invokeLater(() -> {
            exit();
        });
    }

    @Override
    public void setupControllerDidSetup(SetupController sender, String nick, char[] password, int port) {
        invokeLater(() -> {
            sender.closeWindow();
            userService.createUser(nick, port, password);
        });
    }

    @Override
    public void passwordControllerDidExit(PasswordController sender) {
        invokeLater(() -> { 
            System.out.println("password window exit");
            exit(); 
        });
    }

    @Override
    public void passwordControllerDidEnterPassword(PasswordController sender, char[] password) {
        invokeLater(() -> {
            final boolean result = this.userService.loadUser(password);

            SwingUtilities.invokeLater(() -> {
                if(result) {
                    sender.closeWindow();
                }
                else {
                    sender.setPasswordIncorrect();
                }
            });
        });
    }

    @Override
    public void homeControllerDidExit(HomeController sender) {
        invokeLater(() -> {
            exit();
        });
    }

    @Override
    public void homeControllerDidAddPeer(HomeController sender, Peer peer) {
        invokeLater(() -> {
            this.p2pService.connect(peer);
        });
    }

    @Override
    public void homeControllerDidEnterMessage(HomeController sender, Message message) {
        invokeLater(() -> {
            this.databaseService.insertMessage(message);
        });
    }

    @Override
    public void homeControllerLoadMessages(HomeController sender, Peer peer) {
        invokeLater(() -> {
            peer.getMessages().addAll(this.databaseService.getMessagesFor(peer));

            SwingUtilities.invokeLater(() -> {
                sender.loadedMessagesFor(peer);
            });
        });
    }

    @Override
    public void peerConnectingControllerDidAccept(PeerConnectingController sender, Peer peer) {
        this.invokeLater(() -> {
            this.databaseService.insertPeer(peer);
        });
    }

    @Override
    public void peerConnectingControllerDidReject(PeerConnectingController sender, Peer peer) {
        
    }

    public static void main(String[] args) {
        final App app = new App();
        app.userService.loadUser(null);
        app.runTaskExecutorLoop();
    }
}

