package pl.edu.pw.stud.bialek2.marcin.proz.views;

import pl.edu.pw.stud.bialek2.marcin.proz.App;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagConstraints;
import javax.swing.JDialog;
import javax.swing.JTextArea;


public class DeletePeerWindow extends JDialog {
    private static final long serialVersionUID = -827166748800312446L;
    
    private DeletePeerWindowListener listener;
    private String peerNick;

    public DeletePeerWindow(String peerNick) {
        super();
        this.peerNick = peerNick;
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(300, 180));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridBagLayout());
        this.getContentPane().setBackground(App.BACKGROUND_COLOR);
        this.initComponents();
        this.setVisible(true);
    }   

    public void setListener(DeletePeerWindowListener listener) {
        this.listener = listener;
    }

    private void initComponents() {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.insets = new Insets(20, 20, 20, 20);

        final JTextArea infoArea = new JTextArea("Czy na pewno chcesz usunąć znajomego " + this.peerNick + " wraz z wiadomościami?");
        infoArea.setForeground(Color.WHITE);
        infoArea.setOpaque(false);
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weighty = 0.7;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;
        this.add(infoArea, constraints);

        final RoundedButtonView cancelButtonView = new RoundedButtonView("Nie");
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weighty = 0.3;
        constraints.fill = GridBagConstraints.VERTICAL;
        this.add(cancelButtonView, constraints);

        final RoundedButtonView confirmButtonView = new RoundedButtonView("Tak");
        confirmButtonView.setBackground(App.RED_COLOR);
        constraints.gridx = 1;
        this.add(confirmButtonView, constraints);

        cancelButtonView.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.deletePeerWindowDidCancel();
                }
            }
        });

        confirmButtonView.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.deletePeerWindowDidConfirm();
                }
            }
        });
    }
}
