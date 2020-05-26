package pl.edu.pw.stud.bialek2.marcin.proz.views.home;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Chatroom;

import java.awt.BorderLayout;
import javax.swing.JPanel;


public class ChatView extends JPanel {
    private static final long serialVersionUID = -3518986174551400504L;
    private final JPanel topPanel = new JPanel();
    private final MessageListView centerPanel = new MessageListView();
    private final MessageInputView bottomPanel = new MessageInputView();

    public ChatView() {
        this.setLayout(new BorderLayout());
        this.initComponents();
    }   
    
    private void initComponents() {
        this.add(this.topPanel, BorderLayout.NORTH);
        this.add(this.centerPanel, BorderLayout.CENTER);
        this.add(this.bottomPanel, BorderLayout.SOUTH);

        for(int i = 0; i < Chatroom.FAKE_MESSAGES.length; i++) {
            this.centerPanel.appendMessage(Chatroom.FAKE_MESSAGES[i]);
        }
    }
}