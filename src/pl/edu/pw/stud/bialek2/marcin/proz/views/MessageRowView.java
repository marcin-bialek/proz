package pl.edu.pw.stud.bialek2.marcin.proz.views;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.models.ImageMessage;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.models.MessageType;
import pl.edu.pw.stud.bialek2.marcin.proz.models.TextMessage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextPane;


public class MessageRowView extends JPanel {
    private static final long serialVersionUID = 2205687909573788708L;
    private static final Color MESSAGE_1_COLOR = new Color(0, 152, 255);
    private static final Color MESSAGE_2_COLOR = new Color(50, 50, 50);

    private Message message;

    public MessageRowView(Message message) {
        this.message = message;
        this.setLayout(new BorderLayout());
        this.setOpaque(false);
        this.initComponents();
    }

    private void initComponents() {
        JPanel content = new JPanel();

        if(this.message.getType() == MessageType.TEXT_MESSAGE) {
            content = this.makeTextMessagePanel((TextMessage)message);
        }
        else if(this.message.getType() == MessageType.IMAGE_MESSAGE) {
            content = this.makeImageMessagePanel((ImageMessage)message);
        } 

        this.add(content, this.message.isIncoming() ? BorderLayout.WEST : BorderLayout.EAST);
    }

    private JPanel makeTextMessagePanel(TextMessage message) {
        final JTextPane text = new JTextPane();
        text.setEditable(false);
        text.setFont(App.NORMAL_FONT);
        text.setForeground(Color.WHITE);
        text.setOpaque(false);
        text.setText(((TextMessage)this.message).getText());

        final MessageRowView row = this;

        final RoundedView bubble = new RoundedView(15) {
            private static final long serialVersionUID = -6769918848717339799L;

            @Override
            public Dimension getPreferredSize() {
                final Dimension size = super.getPreferredSize();
                final Dimension textSize = text.getPreferredSize();
                size.width = Math.min((row.getWidth() - 20) * 3 / 4, textSize.width + 45);
                return size;
            }
        };

        bubble.setBackground(this.message.isIncoming() ? MESSAGE_2_COLOR : MESSAGE_1_COLOR);
        bubble.getSafeArea().setLayout(new BorderLayout());
        bubble.getSafeArea().add(text, BorderLayout.CENTER);
        bubble.getSafeArea().setOpaque(false);
        bubble.getSafeArea().setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        bubble.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        return bubble;
    }

    private JPanel makeImageMessagePanel(ImageMessage message) {
        final MessageRowView row = this;

        final JPanel panel = new RoundedImageView(message.getImage(), 15) {
            private static final long serialVersionUID = 6321639807996528955L;

            @Override
            public Dimension getPreferredSize() {
                final Dimension size = super.getPreferredSize();
                final double width = row.getWidth() * 3 / 4 - 20;
                final double scale = width / size.getWidth();
                final double height = size.getHeight() * scale;
                return new Dimension((int)width, (int)height);
            }
        };

        panel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        return panel;
    }
}
