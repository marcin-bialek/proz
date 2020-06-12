package pl.edu.pw.stud.bialek2.marcin.proz.views;

import pl.edu.pw.stud.bialek2.marcin.proz.App;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class PasswordWindow extends JFrame {
    private static final long serialVersionUID = -1741907328559266181L;
    
    private PasswordWindowListener listener;
    private RoundedPasswordFieldView passwordFieldView;
    private RoundedButtonView submitButtonView;

    public PasswordWindow() {
        super(App.APP_DISPLAY_NAME);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(300, 160));
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
                    listener.passwordWindowDidClose();
                }
            }
        });
    }

    public void setListener(PasswordWindowListener listener) {
        this.listener = listener;
    }

    public void setPasswordFieldBackgroundColor(Color color) {
        this.passwordFieldView.setBackground(color);
    }

    public void setSubmitButtonEnabled(boolean enabled) {
        if(enabled) {
            this.submitButtonView.enableButton();
        }
        else {
            this.submitButtonView.disableButton();
        }
    }

    public void setSubmitButtonText(String text) {
        this.submitButtonView.getButton().setText(text);
    }

    private void initComponents() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;

        JLabel passwordFieldLabel = new JLabel("Wprowadź hasło:");
        passwordFieldLabel.setForeground(Color.WHITE);
        constraints.gridy = 0;
        constraints.weighty = 0.5;
        constraints.insets = new Insets(20, 20, 0, 20);
        this.add(passwordFieldLabel, constraints);

        this.passwordFieldView = new RoundedPasswordFieldView();
        constraints.gridy = 1;
        constraints.insets = new Insets(10, 20, 20, 20);
        this.add(this.passwordFieldView, constraints);

        this.submitButtonView = new RoundedButtonView("Dalej");
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.gridy = 2;
        constraints.weighty = 0.25;
        constraints.insets = new Insets(0, 20, 20, 20);
        this.add(this.submitButtonView, constraints);

        this.passwordFieldView.getPasswordField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if(submitButtonView.isButtonEnabled() && event.getKeyCode() == KeyEvent.VK_ENTER) {
                    event.consume();
                    listener.passwordWindowDidSubmit(passwordFieldView.getPasswordField().getPassword());
                }
            }
        });

        this.submitButtonView.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.passwordWindowDidSubmit(passwordFieldView.getPasswordField().getPassword());
                }
            }
        });
    }
}
