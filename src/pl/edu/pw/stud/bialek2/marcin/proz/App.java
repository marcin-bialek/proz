package pl.edu.pw.stud.bialek2.marcin.proz;

import pl.edu.pw.stud.bialek2.marcin.proz.controllers.HomeController;
import pl.edu.pw.stud.bialek2.marcin.proz.controllers.HomeControllerListener;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Chatroom;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;
import pl.edu.pw.stud.bialek2.marcin.proz.models.User;
import pl.edu.pw.stud.bialek2.marcin.proz.services.DatabaseService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.DatabaseServiceListener;
import pl.edu.pw.stud.bialek2.marcin.proz.services.SecurityService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.SecurityServiceStaticListener;
import pl.edu.pw.stud.bialek2.marcin.proz.services.UserService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.UserServiceListener;
import pl.edu.pw.stud.bialek2.marcin.proz.views.home.HomeWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.password.PasswordWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.password.PasswordWindowListener;
import pl.edu.pw.stud.bialek2.marcin.proz.views.setup.SetupWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.setup.SetupWindowListener;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.SwingUtilities;


public final class App implements UserServiceListener, SecurityServiceStaticListener, DatabaseServiceListener, HomeControllerListener {
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

    private PasswordWindow passwordWindow;

    private ArrayList<Chatroom> chatrooms;
    private ArrayList<Peer> peers;

    public App() {
        SecurityService.setStaticListener(this);
        this.securityService = new SecurityService();
        this.userService = new UserService(this);
        this.databaseService = new DatabaseService(this);
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
        System.out.println("Nick:   " + user.getNick());
        System.out.println("Port:   " + user.getLastPort());
        System.out.println("Width:  " + user.getWindowSize().width);
        System.out.println("Height: " + user.getWindowSize().height);
        System.out.println("Secret key:  " + (new String(SecurityService.bytes2Hex(user.getSecretKey().getEncoded()))).substring(0, 32) + "...");
        System.out.println("Private key: " + (new String(SecurityService.bytes2Hex(user.getPrivateKey().getEncoded()))).substring(0, 32) + "...");
        System.out.println("Public key:  " + (new String(SecurityService.bytes2Hex(user.getPublicKey().getEncoded()))).substring(0, 32) + "...");
    }

    private void userLoaded(User user) {
        //this.printUser(user);

        this.chatrooms = this.databaseService.getChatrooms();

        for(Chatroom chatroom : chatrooms) {
            chatroom.setPeers(this.databaseService.getPeersFor(chatroom));
        }

        SwingUtilities.invokeLater(() -> {
            final HomeWindow view = new HomeWindow(user.getWindowSize());
            final HomeController homeController = new HomeController(view, user, chatrooms);
            homeController.setListener(this);
        });
    }

    @Override
    public void userServiceNeedsUser() {
        SwingUtilities.invokeLater(() -> {
            SetupWindow setupWindow = new SetupWindow();

            setupWindow.setListener(new SetupWindowListener() {
                @Override
                public void setupWindowDidSubmit(String nick, char[] password) {
                    invokeLater(() -> {
                        userService.createUser(nick, DEFAULT_PORT, password);
                    });
                }

                @Override
                public void setupWindowDidClose() {
                    invokeLater(() -> { 
                        exit(); 
                    });
                }
            });
        });
    }

    @Override
    public void userServiceNeedsPassword() {
        SwingUtilities.invokeLater(() -> {
            passwordWindow = new PasswordWindow();

            passwordWindow.setListener(new PasswordWindowListener() {
                @Override
                public void passwordWindowDidSubmit(char[] password) {
                    invokeLater(() -> {
                        userService.loadUser(password);
                    });
                }

                @Override
                public void passwordWindowDidClose() {
                    invokeLater(() -> { 
                        System.out.println("password window exit");
                        exit(); 
                    });
                }
            });
        });
    }

    @Override
    public void userServiceWrongPassword() {
        if(this.passwordWindow != null) {
            SwingUtilities.invokeLater(() -> {
                passwordWindow.setPasswordCorrect(false);
            });
        }
    }

    @Override 
    public void userServiceDidCreateUser(User user) {
        if(this.passwordWindow != null) {
            this.passwordWindow.setVisible(false);
            this.passwordWindow.dispose();
            this.passwordWindow = null;
        }

        this.databaseService.load(DATABASE_FILE_NAME);
        this.databaseService.insertPeer(user);
        this.userService.saveUser();
        this.userLoaded(user);
    }

    @Override 
    public void userServiceDidLoadUser(User user) {
        if(this.passwordWindow != null) {
            this.passwordWindow.setVisible(false);
            this.passwordWindow.dispose();
            this.passwordWindow = null;
        }

        this.databaseService.load(DATABASE_FILE_NAME);
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
    public void homeControllerDidExit(HomeController sender) {
        invokeLater(() -> {
            exit();
        });
    }

    @Override
    public void homeControllerDidEnterMessage(HomeController sender, Message message) {
        invokeLater(() -> {
            databaseService.insertMessage(message);
        });
    }

    @Override
    public void homeControllerDidCreateChatroom(HomeController sender, Chatroom chatroom) {
        invokeLater(() -> {
            databaseService.insertChatroom(chatroom);
            databaseService.bindPeerAndChatroom(userService.getUser(), chatroom);
        });
    }

    @Override
    public void homeControllerLoadMessages(HomeController sender, Chatroom chatroom) {
        invokeLater(() -> {
            chatroom.setMessages(databaseService.getMessagesFor(chatroom));

            SwingUtilities.invokeLater(() -> {
                sender.loadedMessagesFor(chatroom);
            });
        });
    }

    public static void main(String[] args) {
        final App app = new App();
        app.userService.loadUser(null);
        app.runTaskExecutorLoop();
    }
}

