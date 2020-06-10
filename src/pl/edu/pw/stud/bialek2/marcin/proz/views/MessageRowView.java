package pl.edu.pw.stud.bialek2.marcin.proz.views;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.models.MessageType;
import pl.edu.pw.stud.bialek2.marcin.proz.models.TextMessage;
import pl.edu.pw.stud.bialek2.marcin.proz.services.SecurityService;
import pl.edu.pw.stud.bialek2.marcin.proz.views.RoundedView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextPane;


public class MessageRowView extends JPanel {
    private static final long serialVersionUID = 2205687909573788708L;
    private static final Color MESSAGE_1_COLOR = new Color(0, 152, 255);
    private static final Color MESSAGE_2_COLOR = new Color(50, 50, 50);

    private Message message;
    private Container container;

    public MessageRowView(Message message, Container container) {
        this.message = message;
        this.container = container;
        this.setLayout(new BorderLayout());
        this.setOpaque(false);
        this.initComponents();
    }

    private void initComponents() {
        final JTextPane text = new JTextPane();
        text.setEditable(false);
        text.setFont(App.NORMAL_FONT);
        text.setForeground(Color.WHITE);
        text.setOpaque(false);

        if(this.message.getType() == MessageType.TEXT_MESSAGE) {
            text.setText(((TextMessage)this.message).getText());
        }
        else {
            text.setText("Nieobsługiwany typ wiadomości.");
        }

        RoundedView bubble = new RoundedView(15);
        bubble.setBackground(this.message.isIncoming() ? MESSAGE_2_COLOR : MESSAGE_1_COLOR);
        bubble.getSafeArea().setLayout(new BorderLayout());
        bubble.getSafeArea().add(text, BorderLayout.CENTER);
        bubble.getSafeArea().setOpaque(false);
        bubble.getSafeArea().setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        final MessageRowView row = this;
        JPanel bubbleWrapper = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                final Dimension size = super.getPreferredSize();
                final Dimension textSize = text.getPreferredSize();
                size.width = Math.min((row.getWidth() - 20) * 3 / 4, textSize.width + 45);
                return size;
            }
        };

        bubbleWrapper.setOpaque(false);
        bubbleWrapper.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        bubbleWrapper.setLayout(new BorderLayout());
        bubbleWrapper.add(bubble, BorderLayout.CENTER);
        this.add(bubbleWrapper, this.message.isIncoming() ? BorderLayout.WEST : BorderLayout.EAST);
    }
}
