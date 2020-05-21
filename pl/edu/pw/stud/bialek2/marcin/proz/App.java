package pl.edu.pw.stud.bialek2.marcin.proz;

import pl.edu.pw.stud.bialek2.marcin.proz.models.User;
import pl.edu.pw.stud.bialek2.marcin.proz.services.UserService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.UserServiceListener;
import pl.edu.pw.stud.bialek2.marcin.proz.views.home.HomeWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.setup.SetupWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.setup.SetupWindowListener;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public final class App implements UserServiceListener {
    public static final Color BACKGROUND_COLOR = new Color(30, 31, 38);
    public static final Color PRIMARY_COLOR = new Color(40, 54, 85);
    public static final Color SECONDARY_COLOR = new Color(77, 100, 141);
    public static final Color ACCENT_COLOR = new Color(208, 225, 249);

    private UserService userService = new UserService();

    public App() {
        userService.setListener(this);
        userService.loadUser();
    }

    @Override
    public void userServiceNeedsUser() {
        class SetupResult {
            private String nick;
            public void setNick(String nick) { this.nick = nick; }
            public String getNick() { return this.nick; }
        }

        final SetupResult result = new SetupResult();

        try {
            SwingUtilities.invokeAndWait(() -> {
                SetupWindow setupWindow = new SetupWindow();

                setupWindow.setListener(new SetupWindowListener() {
                    @Override
                    public void setupWindowDidSubmit(String nick) {
                        result.setNick(nick);
                        setupWindow.dispose();;
                    }

                    @Override
                    public void setupWindowDidClose() {
                        System.exit(0);
                    }
                });
            });
        } 
        catch(Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("Nick: " + result.getNick());
    }

    @Override
    public void userServiceNeedsPassword() {

    }

    public static void main(String[] args) {
        new App();
    }
}

