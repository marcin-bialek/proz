package pl.edu.pw.stud.bialek2.marcin.proz.Views;

import pl.edu.pw.stud.bialek2.marcin.proz.*;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ChatroomListRowView extends JPanel implements MouseListener {
    private Chatroom chatroom;
    private JPanel content  = new JPanel();

    public ChatroomListRowView(Chatroom chatroom) {
        this.chatroom = chatroom;

        this.setHeight(75);
        this.setLayout(new BorderLayout());
        this.setBackground(App.BACKGROUND_COLOR);
        this.initBorder();
        this.initComponents();
    }

    private void setHeight(int height) {
        Dimension preferredSize = this.getPreferredSize();
        preferredSize.height = height;
        this.setPreferredSize(preferredSize);

        Dimension minimumSize = this.getMinimumSize();
        minimumSize.height = height;
        this.setMinimumSize(minimumSize);

        Dimension maximumSize = this.getMaximumSize();
        maximumSize.height = height;
        this.setMaximumSize(maximumSize);
    }

    private void initBorder() {
        this.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void initComponents() {
        content.setOpaque(false);
        content.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;

        JLabel nameLabel = new JLabel(this.chatroom.getName());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_START;
        this.content.add(nameLabel, constraints);

        JLabel dateLabel = new JLabel("12:31");
        dateLabel.setForeground(App.ACCENT_COLOR);
        dateLabel.setFont(new Font("Verdana", Font.PLAIN, 10));
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_END;
        this.content.add(dateLabel, constraints);

        JLabel lastMessageLabel = new JLabel(this.chatroom.getLastMessage().getText());
        lastMessageLabel.setForeground(App.ACCENT_COLOR);
        lastMessageLabel.setFont(new Font("Verdana", Font.PLAIN, 10));
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.LINE_START;
        this.content.add(lastMessageLabel, constraints);

        this.addMouseListener(this);
        this.add(this.content, BorderLayout.CENTER);
    }

    public void mouseEntered(MouseEvent event) {
        this.setBackground(App.PRIMARY_COLOR);
        this.revalidate();
    }

    public void mouseExited(MouseEvent event) {
        this.setBackground(App.BACKGROUND_COLOR);
        this.revalidate();
    }

    public void mouseClicked(MouseEvent event) {
        // System.out.println("mouse clicked");
    }

    public void mousePressed(MouseEvent event) {}
    public void mouseReleased(MouseEvent event) {}  
}
