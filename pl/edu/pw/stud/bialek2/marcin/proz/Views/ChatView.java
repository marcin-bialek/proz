package pl.edu.pw.stud.bialek2.marcin.proz.Views;

import java.awt.Color;
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.Chatroom;


public class ChatView extends JPanel {
	private final JPanel topPanel = new JPanel();
    private final MessageListView centerPanel = new MessageListView();
    private final MessageInputView bottomPanel = new MessageInputView();

    public ChatView() {
        this.setBackground(Color.LIGHT_GRAY);
        this.setLayout(new BorderLayout());
        this.initComponents();
    }   
    
    private void initComponents() {
        this.topPanel.setOpaque(false);
        this.add(this.topPanel, BorderLayout.NORTH);

        this.centerPanel.setOpaque(false);
        this.add(this.centerPanel, BorderLayout.CENTER);

        this.bottomPanel.setOpaque(false);
        this.add(this.bottomPanel, BorderLayout.SOUTH);

        
        for(int i = 0; i < Chatroom.FAKE_MESSAGES.length; i++) {
            this.centerPanel.appendMessage(Chatroom.FAKE_MESSAGES[i]);
        }
    }
}