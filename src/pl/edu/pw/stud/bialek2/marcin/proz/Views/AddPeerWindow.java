package pl.edu.pw.stud.bialek2.marcin.proz.views;

import pl.edu.pw.stud.bialek2.marcin.proz.App;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class AddPeerWindow extends JFrame {
    private AddPeerWindowListener listener;
    private RoundedTextFieldView addressFieldView;
    private RoundedTextFieldView portFieldView;
    private RoundedButtonView addButtonView;

    public AddPeerWindow() {
        super(App.APP_DISPLAY_NAME);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(350, 300));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridBagLayout());
        this.getContentPane().setBackground(App.BACKGROUND_COLOR);
        this.initComponents();
        this.setVisible(true);
    }

    public void setListener(AddPeerWindowListener listener) {
        this.listener = listener;
    }

    private void initComponents() {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.insets = new Insets(20, 20, 20, 20);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;

        final JTextArea infoArea = new JTextArea("Podaj adres i port znajomego. Jeśli Twoje połączenie zostanie zaakceptowane, nowy znajomy pojawi się na pasku po lewej stronie.");
        infoArea.setForeground(Color.WHITE);
        infoArea.setOpaque(false);
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weighty = 0.5;
        this.add(infoArea, constraints);

        final JLabel addressLabel = new JLabel("Adres:");
        addressLabel.setForeground(Color.WHITE);
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0.1;
        constraints.weighty = 1;
        constraints.insets = new Insets(20, 20, 0, 20);
        this.add(addressLabel, constraints);

        final JLabel portLabel = new JLabel("Port:");
        portLabel.setForeground(Color.WHITE);
        constraints.gridy = 2;
        this.add(portLabel, constraints);

        this.addressFieldView = new RoundedTextFieldView();
        constraints.gridy = 1;
        constraints.gridx = 1;
        constraints.weightx = 0.9;
        this.add(this.addressFieldView, constraints);

        this.portFieldView = new RoundedTextFieldView();
        constraints.gridy = 2;
        this.add(this.portFieldView, constraints);

        this.addButtonView = new RoundedButtonView("Dodaj");
        this.addButtonView.disableButton();
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.weightx = 1;
        constraints.weighty = 0.5;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(40, 20, 20, 20);
        constraints.fill = GridBagConstraints.VERTICAL;
        this.add(this.addButtonView, constraints);

        this.addressFieldView.getTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if(listener != null) {
                    listener.addPeerWindowDidAddressChange(addressFieldView.getTextField().getText());
                }
            }
        });

        this.portFieldView.getTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if(listener != null) {
                    listener.addPeerWindowDidPortChange(portFieldView.getTextField().getText());
                }
            }
        });

        this.addButtonView.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    final String address = addressFieldView.getTextField().getText();
                    final String port = portFieldView.getTextField().getText();
                    listener.addPeerWindowDidAddPeer(address, port);
                }
            }
        });
    }

    public void setAddressFieldBackground(Color color) {
        this.addressFieldView.setBackground(color);
    }

    public void setPortFieldBackground(Color color) {
        this.portFieldView.setBackground(color);
    }

    public void setAddButtonEnabled(boolean enabled) {
        if(enabled) {
            this.addButtonView.enableButton();
        }
        else {
            this.addButtonView.disableButton();
        }
    }
}
