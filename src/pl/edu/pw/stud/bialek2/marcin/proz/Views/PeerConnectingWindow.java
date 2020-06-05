package pl.edu.pw.stud.bialek2.marcin.proz.views;

import pl.edu.pw.stud.bialek2.marcin.proz.App;

import java.awt.Insets;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JFrame;
import javax.swing.JLabel;


public class PeerConnectingWindow extends JFrame {
    private PeerConnectingWindowListener listener;
    private JLabel nickValueLabel;
    private JLabel addressValueLabel;
    private JLabel publicKeyValueLabel;

    public PeerConnectingWindow() {
        super(App.APP_DISPLAY_NAME);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(300, 200));
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
        constraints.gridx = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        final JLabel infoLabel = new JLabel("Czy chcesz zaakceptować połączenie?");
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        this.add(infoLabel, constraints);

        final JLabel nickLabel = new JLabel("Nick:");
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        this.add(nickLabel, constraints);

        this.nickValueLabel = new JLabel();
        constraints.gridx = 1;
        constraints.gridwidth = 2;
        this.add(this.nickValueLabel, constraints);

        final JLabel addressLabel = new JLabel("Adres:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        this.add(addressLabel, constraints);

        this.addressValueLabel = new JLabel();
        constraints.gridx = 1;
        constraints.gridwidth = 2;
        this.add(this.addressValueLabel, constraints);

        final JLabel publicKeyLabel = new JLabel("Klucz publiczny:");
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        this.add(publicKeyLabel, constraints);

        this.publicKeyValueLabel = new JLabel();
        constraints.gridx = 1;
        constraints.gridwidth = 2;
        this.add(this.publicKeyValueLabel, constraints);
    }

    public void updatePeerInfo(String nick, String address, String publicKey) {
        this.nickValueLabel.setText(nick);
        this.addressValueLabel.setText(address);
        this.publicKeyValueLabel.setText(publicKey);
        this.revalidate();
    }
}

