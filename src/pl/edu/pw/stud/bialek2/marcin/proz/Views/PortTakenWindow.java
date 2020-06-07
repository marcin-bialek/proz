package pl.edu.pw.stud.bialek2.marcin.proz.views;

import pl.edu.pw.stud.bialek2.marcin.proz.App;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class PortTakenWindow extends JFrame {
    private PortTakenWindowListener listener;
    private JTextArea infoLabel;

    public PortTakenWindow() {
        super(App.APP_DISPLAY_NAME);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(400, 300));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridBagLayout());
        this.initComponents();
        this.setVisible(true);
    }

    public void setListener(PortTakenWindowListener listener) {
        this.listener = listener;
    }

    private void initComponents() {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        this.infoLabel = new JTextArea();
        this.infoLabel.setEditable(false);
        this.infoLabel.setLineWrap(true);
        this.infoLabel.setWrapStyleWord(true);
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        this.add(this.infoLabel, constraints);

        final JLabel newPortLabel = new JLabel("Nowy port:");
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        this.add(newPortLabel, constraints);

        final JTextField portField = new JPasswordField();
        constraints.gridx = 1;
        this.add(portField, constraints);

        final JButton onlyOnceButton = new JButton("Użyj tego portu tylko teraz");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.CENTER;
        this.add(onlyOnceButton, constraints);

        final JButton savePortButton = new JButton("Ustaw ten port jako dymyślny");
        constraints.gridx = 1;
        this.add(savePortButton, constraints);

        onlyOnceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.portTakenWindowDidClickUseOnceButton(portField.getText());
                }
            }
        });

        savePortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.portTakenWindowDidClickUseAlwaysButton(portField.getText());
                }
            }
        });
    }

    public void setCurrentPort(int port) {
        this.infoLabel.setText("Port " + port + " jest już używany przez inną aplikację. Zamknij ją lub podaj nowy port. Pamiętaj, że zmiana portu może uniemożliwić twoim znajomym połączenie się z tobą.");
        this.infoLabel.revalidate();
    }
}
