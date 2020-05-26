package pl.edu.pw.stud.bialek2.marcin.proz.views.home;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.views.RoundedView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class MessageListRowView extends JPanel {
    private static final long serialVersionUID = 2205687909573788708L;
    private static final Color MESSAGE_1_COLOR = new Color(0, 152, 255);
    private static final Color MESSAGE_2_COLOR = new Color(50, 50, 50);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Font TEXT_FONT = new Font("Segoe Script", Font.PLAIN, 13);
    private static final Font NICK_FONT = new Font("Segoe Script", Font.PLAIN, 11);

    private Message message;

    public MessageListRowView(Message message) {
        this.message = message;

        this.setOpaque(false);
        this.setLayout(new BorderLayout());
        this.initComponents();
    }

    private void initComponents() {
        JTextArea text = new JTextArea(this.message.getText());
        text.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        text.setFont(TEXT_FONT);
        text.setForeground(TEXT_COLOR);
        text.setOpaque(false);
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);

        RoundedView bubble = new RoundedView(15);
        bubble.setBackground(message.isSentByMe() ? MESSAGE_1_COLOR : MESSAGE_2_COLOR);
        bubble.getSafeArea().setLayout(new BorderLayout());
        bubble.getSafeArea().add(text, BorderLayout.CENTER);
        bubble.getSafeArea().setOpaque(false);

        JPanel bubbleWrapper = new JPanel();
        bubbleWrapper.setOpaque(false);
        bubbleWrapper.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        bubbleWrapper.setLayout(new BorderLayout());
        bubbleWrapper.add(bubble, BorderLayout.CENTER);
        this.add(bubbleWrapper, BorderLayout.CENTER);

        JPanel spacePanel = new JPanel() {
            @Override 
            public Dimension getPreferredSize() {
                Dimension dimension = super.getPreferredSize();
                dimension.width = this.getParent().getSize().width / 5;
                return dimension;
            }
        };

        spacePanel.setOpaque(false);
        this.add(spacePanel, this.message.isSentByMe() ? BorderLayout.WEST : BorderLayout.EAST);

        if(!this.message.isSentByMe()) {
            JLabel nick = new JLabel("Janusz Kowalski");
            nick.setFont(NICK_FONT);
            nick.setForeground(Color.BLACK);
            nick.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
            nick.setOpaque(false);
            bubbleWrapper.add(nick, BorderLayout.NORTH);
        }
    }
}
