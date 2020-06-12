package pl.edu.pw.stud.bialek2.marcin.proz.views;

import pl.edu.pw.stud.bialek2.marcin.proz.App;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;


public class PortTakenWindow extends JFrame {
    private static final long serialVersionUID = -1349009766878591872L;
    
    private PortTakenWindowListener listener;
    private JTextArea infoArea;
    private RoundedTextFieldView portFieldView;
    private RoundedButtonView onlyOnceButtonView;
    private RoundedButtonView saveButtonView;

    public PortTakenWindow() {
        super(App.APP_DISPLAY_NAME);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(320, 350));
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
                    listener.portTakenWindowDidClose();
                }
            }
        });
    }

    public void setListener(PortTakenWindowListener listener) {
        this.listener = listener;
    }

    private void initComponents() {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;

        this.infoArea = new JTextArea();
        this.infoArea.setForeground(Color.WHITE);
        this.infoArea.setOpaque(false);
        this.infoArea.setEditable(false);
        this.infoArea.setLineWrap(true);
        this.infoArea.setWrapStyleWord(true);
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(20, 20, 0, 20);
        this.add(this.infoArea, constraints);

        final JLabel newPortLabel = new JLabel("Nowy port:");
        newPortLabel.setForeground(Color.WHITE);
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0.05;
        constraints.insets = new Insets(40, 20, 20, 20);
        this.add(newPortLabel, constraints);

        this.portFieldView = new RoundedTextFieldView();
        constraints.gridx = 1;
        constraints.weightx = 0.95;
        constraints.insets = new Insets(40, 0, 20, 20);
        this.add(this.portFieldView, constraints);

        this.onlyOnceButtonView = new RoundedButtonView("Użyj tego portu tylko teraz");
        this.onlyOnceButtonView.disableButton();
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(20, 20, 0, 20);
        this.add(this.onlyOnceButtonView, constraints);

        this.saveButtonView = new RoundedButtonView("Ustaw ten port jako dymyślny");
        this.saveButtonView.disableButton();
        constraints.gridy = 3;
        constraints.insets = new Insets(20, 20, 20, 20);
        this.add(this.saveButtonView, constraints);

        this.portFieldView.getTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if(listener != null) {
                    listener.portTakenWindowDidPortChange(portFieldView.getTextField().getText());
                }
            }
        });

        this.onlyOnceButtonView.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.portTakenWindowDidClickUseOnceButton(portFieldView.getTextField().getText());
                }
            }
        });

        this.saveButtonView.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.portTakenWindowDidClickUseAlwaysButton(portFieldView.getTextField().getText());
                }
            }
        });
    }

    public void setCurrentPort(int port) {
        this.infoArea.setText("Nie udało się uruchomić serwera na porcie " + port + ". Być może jest on już używany przez inną aplikację. Podaj inny port lub wyłącz aplikację, która go używa. Pamiętaj, że zmiana portu może uniemożliwić Twoim zajomym połączenie się z Tobą.");
        this.infoArea.revalidate();
    }

    public void setPortFieldBackgroundColor(Color color) {
        this.portFieldView.setBackground(color);
    }

    public void setButtonsEnabled(boolean enabled) {
        if(enabled) {
            this.onlyOnceButtonView.enableButton();
            this.saveButtonView.enableButton();
        }
        else {
            this.onlyOnceButtonView.disableButton();
            this.saveButtonView.disableButton();
        }
    }
}
