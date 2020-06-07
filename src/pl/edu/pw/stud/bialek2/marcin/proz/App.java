package pl.edu.pw.stud.bialek2.marcin.proz;

import pl.edu.pw.stud.bialek2.marcin.proz.controllers.HomeController;
import pl.edu.pw.stud.bialek2.marcin.proz.controllers.HomeControllerDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.controllers.PasswordController;
import pl.edu.pw.stud.bialek2.marcin.proz.controllers.PasswordControllerDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.controllers.PeerConnectingController;
import pl.edu.pw.stud.bialek2.marcin.proz.controllers.PeerConnectingControllerDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.controllers.PortTakenController;
import pl.edu.pw.stud.bialek2.marcin.proz.controllers.PortTakenControllerDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.controllers.SetupController;
import pl.edu.pw.stud.bialek2.marcin.proz.controllers.SetupControllerDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.models.P2PSession;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;
import pl.edu.pw.stud.bialek2.marcin.proz.models.PeerStatus;
import pl.edu.pw.stud.bialek2.marcin.proz.models.TextMessage;
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
import pl.edu.pw.stud.bialek2.marcin.proz.views.PortTakenWindow;
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
                                  PeerConnectingControllerDelegate,
                                  PortTakenControllerDelegate {

    public static final String APP_DISPLAY_NAME = "Chat";
    public static final Color BACKGROUND_COLOR = new Color(30, 31, 38);
    public static final Color PRIMARY_COLOR = new Color(40, 54, 85);
    public static final Color SECONDARY_COLOR = new Color(77, 100, 141);
    public static final Color ACCENT_COLOR = new Color(208, 225, 249);
    public static final int DEFAULT_PORT = 8765;
    public static final int DEFAULT_WINDOW_WIDTH = 700;
    public static final int DEFAULT_WINDOW_HEIGHT = 500;
    public static final String DATABASE_FILE_NAME = "chat.db";

    private final BlockingQueue<Runnable> taskQueue;
    private final SecurityService securityService;
    private final UserService userService; 
    private final DatabaseService databaseService;
    private final P2PService p2pService;

    private HomeController homeController;
    private HashMap<String, Peer> peers;

    public App() {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.peers = new HashMap<>();

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

    private void runP2PServer(User user, int port) {
        this.p2pService.setPort(port);
        this.p2pService.setCredentials(user.getNick(), user.getPublicKey(), user.getPrivateKey());
        this.p2pService.startListening();
    }

    private void loadPeersAndRunP2PServer(User user) {
        this.databaseService.load(DATABASE_FILE_NAME);
        final ArrayList<Peer> peers = this.databaseService.getPeers();

        for(Peer peer : peers) {
            this.peers.put(peer.getPublicKeyAsString(), peer);
            peer.getMessages().addAll(this.databaseService.getMessagesFor(peer));
        }

        this.runP2PServer(user, user.getPort());
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
        this.loadPeersAndRunP2PServer(user);
    }

    @Override 
    public void userServiceDidLoadUser(User user) {
        this.loadPeersAndRunP2PServer(user);
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
    public void p2pServiceServerError(int port) {
        SwingUtilities.invokeLater(() -> {
            final PortTakenWindow view = new PortTakenWindow();
            final PortTakenController controller = new PortTakenController(view, port);
            controller.setDelegate(this);
        });
    }

    @Override
    public void p2pServiceReady() {
        this.invokeLater(() -> {
            for(Map.Entry<String, Peer> entry : this.peers.entrySet()) {
                this.p2pService.connect(entry.getValue());
            }

            SwingUtilities.invokeLater(() -> {
                final HomeWindow view = new HomeWindow(userService.getUser().getWindowSize());
                homeController = new HomeController(view, peers);
                homeController.setDelegate(this);
            });
        });
    }

    @Override
    public void p2pServicePeerDidAccept(Peer peer) {
        this.invokeLater(() -> {
            if(this.peers.containsKey(peer.getPublicKeyAsString())) {
                SwingUtilities.invokeLater(() -> {
                    homeController.updatePeerStatus(peer, PeerStatus.ONLINE);
                });
            }
            else {
                this.databaseService.insertPeer(peer);

                SwingUtilities.invokeLater(() -> {
                    homeController.newPeer(peer);
                    homeController.updatePeerStatus(peer, PeerStatus.ONLINE);
                });
            }
        });
    }

    @Override
    public void p2pServiceIncomingConnection(Peer peer) {
        this.invokeLater(() -> {
            final Peer p = this.peers.get(peer.getPublicKeyAsString());

            if(p != null) {
                if(p.getSession().getState() != P2PSession.State.CONNECTED) {
                    this.p2pService.acceptConnection(peer);
                    p.update(peer);
                    peer.getSession().setPeer(p);
                    
                    SwingUtilities.invokeLater(() -> {
                        homeController.updatePeerStatus(p, PeerStatus.ONLINE);
                    });
                }
            }
            else {
                SwingUtilities.invokeLater(() -> {
                    final PeerConnectingWindow view = new PeerConnectingWindow();
                    final PeerConnectingController controller = new PeerConnectingController(view, peer);
                    controller.setDelegate(this);
                });
            }
        });
    }

    @Override
    public void p2pServicePeerDisconnected(Peer peer) {
        SwingUtilities.invokeLater(() -> {
            homeController.updatePeerStatus(peer, PeerStatus.OFFLINE);
        });
    }

    @Override
    public void p2pServiceDidReceiveMessage(Message message) {
        this.invokeLater(() -> {
            this.databaseService.insertMessage(message);
        });

        SwingUtilities.invokeLater(() -> {
            message.getPeer().getMessages().add(message);
            homeController.newMessage(message);
        });
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
    public void portTakenControllerDidExit(PortTakenController sender) {
        this.invokeLater(() -> {
            this.exit();
        });
    }

    @Override
    public void portTakenControllerUsePortOnce(PortTakenController sender, int port) {
        sender.closeWindow();

        this.invokeLater(() -> {
            this.runP2PServer(this.userService.getUser(),port);
        });
    }

    @Override
    public void portTakenControllerUsePortAlways(PortTakenController sender, int port) {
        sender.closeWindow();

        this.invokeLater(() -> {
            this.runP2PServer(this.userService.getUser(),port);
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
            this.p2pService.sendMessage(message);
        });
    }

    @Override
    public void peerConnectingControllerDidAccept(PeerConnectingController sender, Peer peer) {
        this.invokeLater(() -> {
            this.p2pService.acceptConnection(peer);
            this.databaseService.insertPeer(peer);

            SwingUtilities.invokeLater(() -> {
                homeController.newPeer(peer);
                homeController.updatePeerStatus(peer, PeerStatus.ONLINE);
            });
        });

        sender.closeWindow();
    }

    @Override
    public void peerConnectingControllerDidReject(PeerConnectingController sender, Peer peer) {
        sender.closeWindow();
    }

    public static void main(String[] args) {
        final App app = new App();
        app.userService.loadUser(null);
        app.runTaskExecutorLoop();
    }
}

