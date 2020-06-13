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
import pl.edu.pw.stud.bialek2.marcin.proz.models.User;
import pl.edu.pw.stud.bialek2.marcin.proz.services.DatabaseService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.DatabaseServiceDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.services.HttpService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.P2PService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.P2PServiceDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.services.SecurityService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.SecurityServiceStaticDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.services.UserService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.UserServiceDelegate;
import pl.edu.pw.stud.bialek2.marcin.proz.views.PeerConnectingWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.PortTakenWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.HomeWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.PasswordWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.SetupWindow;

import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


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
    public static final Color GREEN_COLOR = new Color(108, 172, 78);
    public static final Color RED_COLOR = new Color(187, 22, 29);
    public static final Color LIGHT_RED_COLOR = new Color(251, 172, 168);

    public static final Font BIG_BOLD_FONT = new Font("Verdana", Font.BOLD, 18);
    public static final Font BIG_FONT = new Font("Verdana", Font.PLAIN, 18);
    public static final Font NORMAL_FONT = new Font("Verdana", Font.PLAIN, 13);
    public static final Font NORMAL_BOLD_FONT = new Font("Verdana", Font.BOLD, 13);
    public static final Font NORMAL_ITALIC_FONT = new Font("Verdana", Font.ITALIC, 13);
    public static final Font SMALL_FONT = new Font("Verdana", Font.PLAIN, 9);

    public static final int DEFAULT_PORT = 52597;
    public static final int DEFAULT_WINDOW_WIDTH = 700;
    public static final int DEFAULT_WINDOW_HEIGHT = 500;
    public static final String DEFAULT_DATABASE_FILE_NAME = "chat.db";
    public static final String MY_IP_SERVICE_URL = "http://bot.whatismyipaddress.com";
    public static final int MAX_UPLOAD_IMAGE_SIZE = 1200;

    private final BlockingQueue<Runnable> taskQueue;
    private final UserService userService; 
    private final DatabaseService databaseService;
    private final P2PService p2pService;
    private HomeController homeController;
    private HashMap<String, Peer> peers;

    public App() {
        this.taskQueue = new LinkedBlockingQueue<>();
        this.peers = new HashMap<>();

        SecurityService.setStaticDelegate(this);
        this.userService = new UserService(this, this.getClass().getName());
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
        System.exit(0);
    }

    public void deleteDataAndExit() {
        this.p2pService.stopListening();
        this.databaseService.close();
        final File databaseFile = new File(this.userService.getUser().getDBFilename());
        databaseFile.delete();
        this.userService.deleteUser();
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
        this.databaseService.load(user.getDBFilename(), user.getSecretKey());
        final ArrayList<Peer> peers = this.databaseService.getPeers();

        for(Peer peer : peers) {
            this.peers.put(peer.getPublicKeyAsString(), peer);
            peer.getMessages().addAll(this.databaseService.getMessagesFor(peer));
        }

        this.runP2PServer(user, user.getPort());
    }

    private void setUserInfo(HomeController homeController) {
        final String nick = this.userService.getUser().getNick();
        final String localAddress = this.p2pService.getLocalHostAddress();
        final String port = "" + this.p2pService.getPort();
        final String publicKey = SecurityService.keyToString(this.userService.getUser().getPublicKey());

        SwingUtilities.invokeLater(() -> {
            homeController.setUserInfo(nick, localAddress, "...", port, publicKey);
        });

        HttpService.asyncGet(MY_IP_SERVICE_URL, response -> {
            final String externalAddress = response.trim();

            SwingUtilities.invokeLater(() -> {
                homeController.setUserInfo(nick, localAddress, externalAddress, port, publicKey);
            });
            
            return null;
        });
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
        System.err.println("[Error] No such algorithm.");
        this.exit();
    }

    @Override
    public void databaseServiceSQLError() {
        System.out.println("[Error] SQL error.");
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

                this.invokeLater(() -> {
                    this.setUserInfo(homeController);
                });
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
                this.peers.put(peer.getPublicKeyAsString(), peer);

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
                    p.setSession(peer.getSession());
                    p.setAddress(peer.getAddress());
                    p.setPort(peer.getPort());
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
    public void setupControllerDidSetup(SetupController sender, String nick, char[] password, int port, String dbFilename) {
        invokeLater(() -> {
            sender.closeWindow();
            userService.createUser(nick, password, port, dbFilename);
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
            final User user = this.userService.getUser();
            user.setPort(port);
            this.runP2PServer(user, port);
        });
    }

    @Override
    public void homeControllerDidExit(HomeController sender) {
        invokeLater(() -> {
            exit();
        });
    }

    @Override
    public void homeControllerWindowDidResize(HomeController sender, Dimension windowSize) {
        this.invokeLater(() -> {
            this.userService.getUser().setWindowSize(windowSize);
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
    public void homeControllerDeleteData(HomeController sender) {
        sender.closeWindow();

        this.invokeLater(() -> {
            this.deleteDataAndExit();
        });
    }

    @Override
    public void homeControllerDeletePeer(HomeController sender, Peer peer) {
        this.invokeLater(() -> {
            this.peers.remove(peer.getPublicKeyAsString());
            this.p2pService.rejectConnection(peer);
            this.databaseService.deleteMessagesFor(peer);
            this.databaseService.deletePeer(peer);
        });
    }

    @Override
    public void peerConnectingControllerDidAccept(PeerConnectingController sender, Peer peer) {
        sender.closeWindow();

        this.invokeLater(() -> {
            this.p2pService.acceptConnection(peer);
            this.databaseService.insertPeer(peer);
            this.peers.put(peer.getPublicKeyAsString(), peer);

            SwingUtilities.invokeLater(() -> {
                homeController.newPeer(peer);
                homeController.updatePeerStatus(peer, PeerStatus.ONLINE);
            });
        });
    }

    @Override
    public void peerConnectingControllerDidReject(PeerConnectingController sender, Peer peer) {
        sender.closeWindow();
        
        this.invokeLater(() -> {
            this.p2pService.rejectConnection(peer);
        });
    }

    public static void main(String[] args) {
        UIManager.getDefaults().put("Button.disabledText", Color.WHITE);
        
        final App app = new App();
        app.userService.loadUser(null);
        app.runTaskExecutorLoop();
        app.exit();
    }
}

