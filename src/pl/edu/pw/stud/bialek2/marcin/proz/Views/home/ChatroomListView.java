package pl.edu.pw.stud.bialek2.marcin.proz.views.home;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Chatroom;
import pl.edu.pw.stud.bialek2.marcin.proz.views.RoundedScrollView;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;


public class ChatroomListView extends JPanel {
    private static final long serialVersionUID = 614351659406015451L;
    private ChatroomListViewListener listener;
    private final JPanel newChatroomPanel = new JPanel();
    private final JPanel listPanel = new JPanel();

    public ChatroomListView() {
        Dimension dimension = this.getPreferredSize();
        dimension.width = 250;
        this.setPreferredSize(dimension);
        this.setLayout(new BorderLayout());
        this.setBackground(App.BACKGROUND_COLOR);
        this.initComponents();
    }    

    public void setListener(ChatroomListViewListener listener) {
        this.listener = listener;
    }

    private void initComponents() {
        JButton addChatroomButton = new JButton("nowy chat");
        addChatroomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.chatroomListViewDidClickNewChatroomButton();
                }
            }
        });

        this.newChatroomPanel.setOpaque(false);
        this.newChatroomPanel.add(addChatroomButton);
        this.add(this.newChatroomPanel, BorderLayout.NORTH);

        this.listPanel.setBackground(App.BACKGROUND_COLOR);
        this.listPanel.setLayout(new BoxLayout(this.listPanel, BoxLayout.Y_AXIS));

        RoundedScrollView listScrollPane = new RoundedScrollView(this.listPanel);
        listScrollPane.setBackground(App.BACKGROUND_COLOR);
        this.add(listScrollPane, BorderLayout.CENTER);
    }

    public void clearChatrooms() {
        this.listPanel.removeAll();
        this.listPanel.revalidate();
    }

    public void appendChatroom(Chatroom chatroom) {
        this.listPanel.add(new ChatroomListRowView(chatroom));
        this.listPanel.revalidate();
    }
}