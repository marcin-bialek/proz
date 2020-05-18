package pl.edu.pw.stud.bialek2.marcin.proz.Views;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class MessageInputView extends JPanel {
    public MessageInputView() {
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setLayout(new BorderLayout());
        this.initComponents();
    }

    private void initComponents() {
        JTextArea messageInputArea = new JTextArea();
        messageInputArea.setLineWrap(true);

        RoundedView messageInputView = new RoundedView(15);
        messageInputView.setBackground(Color.WHITE);
        messageInputView.getSafeArea().setLayout(new BorderLayout());
        messageInputView.getSafeArea().add(messageInputArea, BorderLayout.CENTER);

        this.add(messageInputView, BorderLayout.CENTER);
    }
}   