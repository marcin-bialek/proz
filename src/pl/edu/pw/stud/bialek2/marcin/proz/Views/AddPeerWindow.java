package pl.edu.pw.stud.bialek2.marcin.proz.views;

import pl.edu.pw.stud.bialek2.marcin.proz.App;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class AddPeerWindow extends JFrame {
    private AddPeerWindowListener listener;

    public AddPeerWindow() {
        super(App.APP_DISPLAY_NAME);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(300, 200));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridBagLayout());
        this.initComponents();
        this.setVisible(true);
    }

    public void setListener(AddPeerWindowListener listener) {
        this.listener = listener;
    }

    private void initComponents() {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        final JTextField addressField = new JTextField();
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        this.add(addressField, constraints);

        final JTextField portField = new JTextField();
        constraints.gridx = 2;
        constraints.gridwidth = 1;
        this.add(portField, constraints);

        final JButton addButton = new JButton("Dodaj");
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.CENTER;
        this.add(addButton, constraints);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    final String address = addressField.getText();
                    final int port = Integer.parseInt(portField.getText());
                    listener.addPeerWindowDidAddPeer(address, port);
                }
            }
        });
    }
}
