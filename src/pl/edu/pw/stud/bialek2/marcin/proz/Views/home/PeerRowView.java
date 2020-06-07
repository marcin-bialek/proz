package pl.edu.pw.stud.bialek2.marcin.proz.views.home;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;

import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class PeerRowView extends JPanel {
    private static final long serialVersionUID = -1830652066555568399L;
    private static final Font PLAIN_FONT = new Font("Verdana", Font.PLAIN, 12);
    private static final Font BOLD_FONT = new Font("Verdana", Font.BOLD, 12);
    private static final Color OFFLINE_COLOR = new Color(187, 22, 29);
    private static final Color ONLINE_COLOR = new Color(108, 172, 78);
    private Peer peer;
    private JPanel content  = new JPanel();
    private JLabel statusLabel;
    private JLabel lastMessageLabel;

    public PeerRowView(Peer peer) {
        this.peer = peer;

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

        JLabel nickLabel = new JLabel(this.peer.getNick());
        nickLabel.setForeground(Color.WHITE);
        nickLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_START;
        this.content.add(nickLabel, constraints);

        this.statusLabel = new JLabel("•");
        this.statusLabel.setForeground(OFFLINE_COLOR);
        this.statusLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.LINE_END;
        this.content.add(this.statusLabel, constraints);

        final String lastMessage = this.peer.getLastMessage().getValueAsString();
        this.lastMessageLabel = new JLabel(lastMessage);
        this.lastMessageLabel.setForeground(App.ACCENT_COLOR);
        this.lastMessageLabel.setFont(PLAIN_FONT);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.LINE_START;
        this.content.add(this.lastMessageLabel, constraints);

        this.add(this.content, BorderLayout.CENTER);
    } 

    public void setPeerOnline() {
        this.statusLabel.setForeground(ONLINE_COLOR);
        this.revalidate();
    }

    public void setPeerOffline() {
        this.statusLabel.setForeground(OFFLINE_COLOR);
        this.revalidate();
    }

    public void updateLastMessage(boolean bold) {
        System.out.println("[updateLastMessage] peer: " + ((this.peer == null) ? "null" : "nie null"));
        System.out.println("[updateLastMessage] msg: " + ((this.peer.getLastMessage() == null) ? "null" : "nie null"));

        this.lastMessageLabel.setFont(bold ? BOLD_FONT : PLAIN_FONT);
        this.lastMessageLabel.setText(this.peer.getLastMessage().getValueAsString());
        this.revalidate();
    }
}
