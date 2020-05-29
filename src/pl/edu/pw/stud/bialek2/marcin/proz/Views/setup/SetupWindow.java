package pl.edu.pw.stud.bialek2.marcin.proz.views.setup;

import pl.edu.pw.stud.bialek2.marcin.proz.App;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowAdapter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class SetupWindow extends JFrame {
    private static final long serialVersionUID = 206072431562958302L;
    private SetupWindowListener listener;
    private JButton submitButton;

    public SetupWindow() {
        super(App.APP_DISPLAY_NAME);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(300, 200));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridBagLayout());
        this.initComponents();
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (listener != null) {
                    listener.setupWindowDidClose();
                }
            }
        });
    }

    public void setListener(SetupWindowListener listener) {
        this.listener = listener;
    }

    public void setSubmitButtonEnabled(boolean enabled) {
        this.submitButton.setEnabled(enabled);
    }

    private void initComponents() {
        JTextArea info = new JTextArea("Aliqua nulla eu ullamco reprehenderit. Voluptate fugiat velit cupidatat sint. Fugiat ad nulla consectetur est tempor Lorem dolore culpa in nulla sunt fugiat ea. Voluptate ex laboris enim duis ut.");
        info.setOpaque(false);
        info.setEditable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        GridBagConstraints infoConstraints = new GridBagConstraints();
        infoConstraints.gridx = 0;
        infoConstraints.gridy = 0;
        infoConstraints.weightx = 1;
        infoConstraints.weighty = 1;
        infoConstraints.gridheight = 2;
        infoConstraints.insets = new Insets(10, 10, 10, 10);
        infoConstraints.fill = GridBagConstraints.HORIZONTAL;
        this.add(info, infoConstraints);

        JTextField nickField = new JTextField();
        GridBagConstraints nickFieldConstraints = new GridBagConstraints();
        nickFieldConstraints.gridx = 0;
        nickFieldConstraints.gridy = 2;
        nickFieldConstraints.insets = new Insets(10, 10, 10, 10);
        nickFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        this.add(nickField, nickFieldConstraints);

        JPasswordField passwordField = new JPasswordField();
        GridBagConstraints passwordFieldConstraints = new GridBagConstraints();
        passwordFieldConstraints.gridx = 0;
        passwordFieldConstraints.gridy = 3;
        passwordFieldConstraints.insets = new Insets(10, 10, 10, 10);
        passwordFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        this.add(passwordField, passwordFieldConstraints);

        this.submitButton = new JButton("Zapisz");
        this.submitButton.setEnabled(false);
        GridBagConstraints submitButtonConstraints = new GridBagConstraints();
        submitButtonConstraints.gridx = 0;
        submitButtonConstraints.gridy = 4;
        submitButtonConstraints.insets = new Insets(10, 10, 10, 10);
        this.add(this.submitButton, submitButtonConstraints);

        nickField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if(listener != null) {
                    listener.setupWindowDidNickChange(nickField.getText());
                }
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if(listener != null) {
                    listener.setupWindowDidPasswordChange(passwordField.getPassword());
                }
            }
        });

        this.submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.setupWindowDidSubmit(nickField.getText(), passwordField.getPassword());
                }
            }
        });
    }
}
