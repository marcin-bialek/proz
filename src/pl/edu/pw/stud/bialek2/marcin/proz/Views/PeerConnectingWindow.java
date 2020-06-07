package pl.edu.pw.stud.bialek2.marcin.proz.views;

import pl.edu.pw.stud.bialek2.marcin.proz.App;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class PeerConnectingWindow extends JFrame {
    private static final Color RED = new Color(187, 22, 29);
    private static final Color GREEN = new Color(108, 172, 78);

    private PeerConnectingWindowListener listener;
    private JLabel nickValueLabel;
    private JLabel addressValueLabel;
    private JTextArea publicKeyValueLabel;

    public PeerConnectingWindow() {
        super(App.APP_DISPLAY_NAME);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(500, 450));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridBagLayout());
        this.initComponents();
        this.setVisible(true);
    }

    public void setListener(PeerConnectingWindowListener listener) {
        this.listener = listener;
    }

    private void initComponents() {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.fill = GridBagConstraints.BOTH;
        
        final JLabel infoLabel = new JLabel("Czy chcesz zaakceptować połączenie?");
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        this.add(infoLabel, constraints);

        constraints.weightx = 0.33;

        final JLabel nickLabel = new JLabel("Nick:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        this.add(nickLabel, constraints);

        final JLabel addressLabel = new JLabel("Adres:");
        constraints.gridy = 2;
        this.add(addressLabel, constraints);

        final JLabel publicKeyLabel = new JLabel("Klucz publiczny:");
        constraints.gridy = 3;
        this.add(publicKeyLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 0.67;

        this.nickValueLabel = new JLabel();
        constraints.gridy = 1;
        this.add(this.nickValueLabel, constraints);

        this.addressValueLabel = new JLabel();
        constraints.gridy = 2;
        this.add(this.addressValueLabel, constraints);

        this.publicKeyValueLabel = new JTextArea();
        this.publicKeyValueLabel.setOpaque(false);
        this.publicKeyValueLabel.setEditable(false);
        this.publicKeyValueLabel.setLineWrap(true);
        constraints.gridy = 3;
        this.add(this.publicKeyValueLabel, constraints);

        final JPanel buttonsPanel = new JPanel(new GridBagLayout());
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.gridy = 4;
        this.add(buttonsPanel, constraints);

        final JButton rejectButton = new JButton("Odrzuć");
        rejectButton.setForeground(RED);
        constraints.gridwidth = 1;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        buttonsPanel.add(rejectButton, constraints);

        final JButton acceptButton = new JButton("Zaakceptuj");
        acceptButton.setForeground(GREEN);
        constraints.gridx = 0;
        buttonsPanel.add(acceptButton, constraints);

        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.peerConnectingWindowDidAccept();
                }
            }
        });

        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.peerConnectingWindowDidReject();
                }
            }
        });
    }

    public void updatePeerInfo(String nick, String address, String publicKey) {
        this.nickValueLabel.setText(nick);
        this.addressValueLabel.setText(address);
        this.publicKeyValueLabel.setText(publicKey);
        this.revalidate();
    }
}

