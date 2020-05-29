package pl.edu.pw.stud.bialek2.marcin.proz.views.password;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

import pl.edu.pw.stud.bialek2.marcin.proz.App;


public class PasswordWindow extends JFrame {
    private static final long serialVersionUID = -1741907328559266181L;
    private PasswordWindowListener listener;
    private JPasswordField passwordField;

    public PasswordWindow() {
        super(App.APP_DISPLAY_NAME);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(300, 130));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridBagLayout());
        this.initComponents();
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(listener != null) {
                    listener.passwordWindowDidClose();
                }
            }
        });
    }

    public void setListener(PasswordWindowListener listener) {
        this.listener = listener;
    }
    
    public void setPasswordFieldBorderColor(Color color) {
        this.passwordField.setBorder(BorderFactory.createLineBorder(color, 1));
    }

    private void initComponents() {
        GridBagConstraints constraints = new GridBagConstraints();

        JLabel passwordFieldLabel = new JLabel("Wprowadź hasło:");
        constraints.anchor = GridBagConstraints.LINE_START;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(10, 15, 3, 10);
        this.add(passwordFieldLabel, constraints);

        this.passwordField = new JPasswordField();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(0, 10, 10, 10);
        this.add(passwordField, constraints);

        JButton submiButton = new JButton("Dalej");
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(0, 10, 10, 10);
        this.add(submiButton, constraints);

        submiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.passwordWindowDidSubmit(passwordField.getPassword());
                }
            }
        });
    }
}
