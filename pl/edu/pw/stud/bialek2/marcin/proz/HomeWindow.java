package pl.edu.pw.stud.bialek2.marcin.proz;

import javax.swing.JFrame;
import java.awt.BorderLayout;


public class HomeWindow extends JFrame {
    private final ChatroomListView chatroomListView = new ChatroomListView();
    private final ChatView chatView = new ChatView();

    public HomeWindow() {
        super("Chat");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(700, 500);
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