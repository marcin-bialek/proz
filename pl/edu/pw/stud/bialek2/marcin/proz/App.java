package pl.edu.pw.stud.bialek2.marcin.proz;

import pl.edu.pw.stud.bialek2.marcin.proz.models.User;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.SwingUtilities;


public final class App implements UserServiceListener, SecurityServiceStaticListener {
    public static final Color BACKGROUND_COLOR = new Color(30, 31, 38);
    public static final Color PRIMARY_COLOR = new Color(40, 54, 85);
    public static final Color SECONDARY_COLOR = new Color(77, 100, 141);
    public static final Color ACCENT_COLOR = new Color(208, 225, 249);
    public static final int DEFAULT_PORT = 8765;
    public static final int DEFAULT_WINDOW_WIDTH = 700;
    public static final int DEFAULT_WINDOW_HEIGHT = 500;

    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final SecurityService securityService;
    private final UserService userService; 

    public App() {
        SecurityService.setStaticListener(this);
        this.securityService = new SecurityService();
        this.userService = new UserService(this);
    }

    public void runTaskExecutorLoop() {
        try {
            while(true) {
                this.taskQueue.take().run();
            }
        } 
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void invokeLater(Runnable runnable) {
        try {
            this.taskQueue.put(runnable);
        } 
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        System.exit(0);
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
            PasswordWindow passwordWindow = new PasswordWindow();

            passwordWindow.setListener(new PasswordWindowListener() {
                @Override
                public void passwordWindowDidSubmit(char[] password) {
                    invokeLater(() -> {
                        userService.loadUser(password);
                    });

                    //String pass = new String(password);
                    // if(pass.equals("test1234")) {
                    //     SwingUtilities.invokeLater(() -> {
                    //         passwordWindow.setVisible(false);
                    //         passwordWindow.dispose();
                    //         HomeWindow homeWindow = new HomeWindow();
                    //     });
                    // }
                    // else {
                    //     SwingUtilities.invokeLater(() -> {
                    //         passwordWindow.setPasswordCorrect(false);
                    //     });
                    // }
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
        System.out.println("Wrong password");
    }

    @Override 
    public void userServiceDidCreateUser(User user) {
        System.out.println("Nick:   " + user.getNick());
        System.out.println("Port:   " + user.getPort());
        System.out.println("Width:  " + user.getWindowSize().width);
        System.out.println("Height: " + user.getWindowSize().height);
        System.out.println("Private key: " + (new String(SecurityService.bytes2Hex(user.getPrivateKey().getEncoded()))).substring(0, 32) + "...");
        System.out.println("Public key:  " + (new String(SecurityService.bytes2Hex(user.getPublicLKey().getEncoded()))).substring(0, 32) + "...");
    }

    @Override 
    public void userServiceDidLoadUser(User user) {
        System.out.println("Nick:   " + user.getNick());
        System.out.println("Port:   " + user.getPort());
        System.out.println("Width:  " + user.getWindowSize().width);
        System.out.println("Height: " + user.getWindowSize().height);
        System.out.println("Private key: " + (new String(SecurityService.bytes2Hex(user.getPrivateKey().getEncoded()))).substring(0, 32) + "...");
        System.out.println("Public key:  " + (new String(SecurityService.bytes2Hex(user.getPublicLKey().getEncoded()))).substring(0, 32) + "...");
    }

    @Override
    public void securityServiceNoSuchAlgorithm() {
        System.err.println("No such algorithm");
        System.exit(1);
    }

    public static void main(String[] args) {
        final App app = new App();
        app.userService.loadUser(null);
        app.runTaskExecutorLoop();
    }
}

