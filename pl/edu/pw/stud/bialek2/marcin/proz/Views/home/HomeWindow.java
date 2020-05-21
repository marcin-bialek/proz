package pl.edu.pw.stud.bialek2.marcin.proz.views.home;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Chatroom;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Dimension;


public class HomeWindow extends JFrame {
    private static final long serialVersionUID = -7341554873868191886L;
    private final ChatroomListView chatroomListView = new ChatroomListView();
    private final ChatView chatView = new ChatView();

    public HomeWindow() {
        super("Chat");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(new Dimension(700, 500));
        this.setMinimumSize(new Dimension(550, 350));
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(App.BACKGROUND_COLOR);
        this.initComponents();
        this.setVisible(true);
    }

    private void initComponents() {
        this.add(this.chatroomListView, BorderLayout.WEST);
        this.add(this.chatView, BorderLayout.CENTER);

        for(int i = 1; i <= 8; i++) {
            this.chatroomListView.appendChatroom(new Chatroom("Chat " + i));
        }
    }
}