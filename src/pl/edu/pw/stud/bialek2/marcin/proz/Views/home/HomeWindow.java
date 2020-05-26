package pl.edu.pw.stud.bialek2.marcin.proz.views.home;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Chatroom;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;


public class HomeWindow extends JFrame {
    private static final long serialVersionUID = -7341554873868191886L;
    private HomeWindowListener listener;
    private final ChatroomListView chatroomListView = new ChatroomListView();
    private final ChatView chatView = new ChatView();

    public HomeWindow(Dimension windowSize) {
        super("Chat");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(windowSize);
        this.setMinimumSize(new Dimension(550, 350));
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(App.BACKGROUND_COLOR);
        this.initComponents();
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(listener != null) {
                    listener.homeWindowDidClose();
                }
            }
        });

        this.chatroomListView.setListener(new ChatroomListViewListener(){
            @Override
            public void chatroomListViewDidClickNewChatroomButton() {
                if(listener != null) {
                    listener.homeWindowDidClickNewChatroomButton();
                }
            }
        });
    }

    public HomeWindow() {
        this(new Dimension(700, 500));
    }

    public void setListener(HomeWindowListener listener) {
        this.listener = listener;
    }

    private void initComponents() {
        this.add(this.chatroomListView, BorderLayout.WEST);
        this.add(this.chatView, BorderLayout.CENTER);
    }

    public void updateChatrooms(final ArrayList<Chatroom> chatrooms) {
        this.chatroomListView.clearChatrooms();
        
        for(Chatroom chatroom : chatrooms) {
            this.chatroomListView.appendChatroom(chatroom);
        }
    }
}