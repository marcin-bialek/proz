package pl.edu.pw.stud.bialek2.marcin.proz.views.home;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.models.MessageType;
import pl.edu.pw.stud.bialek2.marcin.proz.models.TextMessage;
import pl.edu.pw.stud.bialek2.marcin.proz.views.RoundedView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class MessageRowView extends JPanel {
    private static final long serialVersionUID = 2205687909573788708L;
    private static final Color MESSAGE_1_COLOR = new Color(0, 152, 255);
    private static final Color MESSAGE_2_COLOR = new Color(50, 50, 50);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Font TEXT_FONT = new Font("Segoe Script", Font.PLAIN, 13);
    private static final Font NICK_FONT = new Font("Segoe Script", Font.PLAIN, 11);

    private Message message;

    public MessageRowView(Message message) {
        this.message = message;
        this.setOpaque(false);
        this.initComponents();
    }

    private void initComponents() {
        JTextArea text = new JTextArea();

        if(this.message.getType() == MessageType.TEXT_MESSAGE) {
            text.setText(((TextMessage)this.message).getText());
        }
        else {
            text.setText("Nieobsługiwany typ wiadomości.");
        }

        text.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        text.setFont(TEXT_FONT);
        text.setForeground(TEXT_COLOR);
        text.setOpaque(false);
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);

        // final int a = text.getFontMetrics(TEXT_FONT).stringWidth(this.message.getText());
        // final int b = (int)text.getFontMetrics(TEXT_FONT).getStringBounds(this.message.getText(), null).getWidth() + 20;
        // System.out.println("a: " + a + ", b: " + b);

        RoundedView bubble = new RoundedView(15);
        
        bubble.setBackground(this.message.getIsSentByUser() ? MESSAGE_1_COLOR : MESSAGE_2_COLOR);

        bubble.getSafeArea().setLayout(new BorderLayout());
        bubble.getSafeArea().add(text, BorderLayout.CENTER);
        bubble.getSafeArea().setOpaque(false);

        JPanel bubbleWrapper = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                final Dimension size = super.getPreferredSize();
                final int a = 4 * this.getParent().getSize().width / 5;
                size.width = a; //Math.min(a, b);
                return size;
            }
        };

        bubbleWrapper.setOpaque(false);
        bubbleWrapper.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        bubbleWrapper.setLayout(new BorderLayout());
        bubbleWrapper.add(bubble, BorderLayout.CENTER);

        if(!this.message.getIsSentByUser()) {
            JLabel nick = new JLabel(this.message.getPeer().getNick());
            nick.setFont(NICK_FONT);
            nick.setForeground(Color.BLACK);
            nick.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
            nick.setOpaque(false);
            bubbleWrapper.add(nick, BorderLayout.NORTH);
        }

        this.setLayout(new FlowLayout(this.message.getIsSentByUser() ? FlowLayout.RIGHT : FlowLayout.LEFT));
        this.add(bubbleWrapper);
    }

    // @Override
    // public Dimension getMaximumSize() {
    //     final Dimension size = super.getMaximumSize();
    //     size.height = this.getPreferredSize().height;
    //     return size;
    // }
}
