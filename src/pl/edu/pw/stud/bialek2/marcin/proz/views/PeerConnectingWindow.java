package pl.edu.pw.stud.bialek2.marcin.proz.views;

import pl.edu.pw.stud.bialek2.marcin.proz.App;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    private PeerConnectingWindowListener listener;
    private JLabel nickValueLabel;
    private JLabel addressValueLabel;
    private JLabel publicKeyValueLabel;
    private String fullPublicKey;

    public PeerConnectingWindow() {
        super(App.APP_DISPLAY_NAME);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(400, 250));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridBagLayout());
        this.getContentPane().setBackground(App.BACKGROUND_COLOR);
        this.initComponents();
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(listener != null) {
                    listener.peerConnectingWindowDidReject();
                }
            }
        });
    }

    public void setListener(PeerConnectingWindowListener listener) {
        this.listener = listener;
    }

    private void initComponents() {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(20, 20, 20, 20);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        
        final JLabel infoLabel = new JLabel("Czy chcesz zaakceptować połączenie?");
        infoLabel.setFont(App.NORMAL_BOLD_FONT);
        infoLabel.setForeground(Color.WHITE);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(20, 20, 0, 20);
        this.add(infoLabel, constraints);

        final JLabel nickLabel = new JLabel("Nick:");
        nickLabel.setFont(App.NORMAL_FONT);
        nickLabel.setForeground(Color.WHITE);
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0.1;
        this.add(nickLabel, constraints);

        final JLabel addressLabel = new JLabel("Adres:");
        addressLabel.setFont(App.NORMAL_FONT);
        addressLabel.setForeground(Color.WHITE);
        constraints.gridy = 2;
        this.add(addressLabel, constraints);

        final JLabel publicKeyLabel = new JLabel("Klucz publiczny:");
        publicKeyLabel.setFont(App.NORMAL_FONT);
        publicKeyLabel.setForeground(Color.WHITE);
        constraints.gridy = 3;
        this.add(publicKeyLabel, constraints);

        this.nickValueLabel = new JLabel();
        nickValueLabel.setFont(App.NORMAL_FONT);
        nickValueLabel.setForeground(Color.WHITE);
        constraints.gridy = 1;
        constraints.gridx = 1;
        constraints.weightx = 0.9;
        constraints.insets = new Insets(20, 0, 0, 20);
        this.add(this.nickValueLabel, constraints);

        this.addressValueLabel = new JLabel();
        addressValueLabel.setFont(App.NORMAL_FONT);
        addressValueLabel.setForeground(Color.WHITE);
        constraints.gridy = 2;
        this.add(this.addressValueLabel, constraints);

        this.publicKeyValueLabel = new JLabel();
        publicKeyValueLabel.setToolTipText("Kliknij, aby zobaczyć cały klucz.");
        publicKeyValueLabel.setFont(App.NORMAL_FONT);
        publicKeyValueLabel.setForeground(Color.WHITE);
        constraints.gridy = 3;
        this.add(this.publicKeyValueLabel, constraints);

        final JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setOpaque(false);
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.insets = new Insets(40, 20, 20, 20);
        this.add(buttonsPanel, constraints);

        final RoundedButtonView acceptButtonView = new RoundedButtonView("Zaakceptuj");
        acceptButtonView.setPreferredSize(new Dimension(120, 30));
        constraints.gridwidth = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.insets = new Insets(0, 0, 0, 0);
        buttonsPanel.add(acceptButtonView, constraints);

        final RoundedButtonView rejectButtonView = new RoundedButtonView("Odrzuć");
        rejectButtonView.setPreferredSize(new Dimension(120, 30));
        rejectButtonView.setBackground(App.RED_COLOR);
        constraints.gridx = 1;
        buttonsPanel.add(rejectButtonView, constraints);

        final PeerConnectingWindow owner = this;

        publicKeyValueLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new PublicKeyDialog(owner, "Klucz publiczny " + nickValueLabel.getText(), fullPublicKey);
            }
        });

        acceptButtonView.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.peerConnectingWindowDidAccept();
                }
            }
        });

        rejectButtonView.getButton().addActionListener(new ActionListener() {
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
        this.publicKeyValueLabel.setText(publicKey.substring(0, 29) + "...");
        this.fullPublicKey = publicKey;
        this.revalidate();
    }
}

