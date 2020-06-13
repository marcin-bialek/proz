package pl.edu.pw.stud.bialek2.marcin.proz.views;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.Language;

import java.awt.Color;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;


public class SetupWindow extends JFrame {
    private static final long serialVersionUID = 206072431562958302L;

    private SetupWindowListener listener;
    private RoundedTextFieldView nickFieldView;
    private RoundedPasswordFieldView passwordFieldView;
    private RoundedTextFieldView portFieldView;
    private RoundedTextFieldView databaseFieldView;
    private RoundedButtonView submitButtonView;

    public SetupWindow() {
        super(App.APP_DISPLAY_NAME);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(600, 550));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridBagLayout());
        this.initComponents();
        this.getContentPane().setBackground(App.BACKGROUND_COLOR);
        this.setVisible(true);
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(listener != null) {
                    listener.setupWindowDidClose();
                }
            }
        });
    }

    public void setListener(SetupWindowListener listener) {
        this.listener = listener;
    }

    private void initComponents() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(30, 20, 15, 0);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;

        JTextArea info = new JTextArea(Language.DEFAULT.getString("setup_info"));
        info.setForeground(Color.WHITE);
        info.setOpaque(false);
        info.setEditable(false);
        info.setLineWrap(true);
        info.setFont(App.NORMAL_BOLD_FONT);
        info.setWrapStyleWord(true);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        this.add(info, constraints);

        JLabel nickLabel = new JLabel(Language.DEFAULT.getString("nick") + ":");
        nickLabel.setForeground(Color.WHITE);
        constraints.weightx = 0.1;
        constraints.gridwidth = 1;
        constraints.gridy = 1;
        constraints.insets = new Insets(15, 20, 0, 20);
        this.add(nickLabel, constraints);

        JLabel portLabel = new JLabel(Language.DEFAULT.getString("password") + ":");
        portLabel.setForeground(Color.WHITE);
        constraints.gridy = 3;
        this.add(portLabel, constraints);

        JLabel passwordLabel = new JLabel(Language.DEFAULT.getString("port") + ":");
        passwordLabel.setForeground(Color.WHITE);
        constraints.gridy = 5;
        this.add(passwordLabel, constraints);

        JLabel databaseLabel = new JLabel(Language.DEFAULT.getString("database") + ":");
        databaseLabel.setForeground(Color.WHITE);
        constraints.gridy = 7;
        this.add(databaseLabel, constraints);


        this.nickFieldView = new RoundedTextFieldView();
        constraints.weightx = 0.9;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.insets = new Insets(15, 0, 0, 20);
        this.add(this.nickFieldView, constraints);

        this.passwordFieldView = new RoundedPasswordFieldView();
        constraints.gridy = 3;
        this.add(this.passwordFieldView, constraints);

        this.portFieldView = new RoundedTextFieldView();
        this.portFieldView.getTextField().setText("" + App.DEFAULT_PORT);
        constraints.gridy = 5;
        this.add(this.portFieldView, constraints);

        this.databaseFieldView = new RoundedTextFieldView();
        this.databaseFieldView.getTextField().setText(App.DEFAULT_DATABASE_FILE_NAME);
        constraints.gridy = 7;
        this.add(this.databaseFieldView, constraints);

        JTextPane nickPane = new JTextPane();
        nickPane.setText(Language.DEFAULT.getString("nick_description"));
        nickPane.setEditable(false);
        nickPane.setForeground(Color.WHITE);
        nickPane.setOpaque(false);
        nickPane.setFont(App.SMALL_FONT);
        constraints.gridy = 2;
        constraints.insets = new Insets(5, 0, 0, 20);
        this.add(nickPane, constraints);

        JTextPane passwordPane = new JTextPane();
        passwordPane.setText(Language.DEFAULT.getString("password_description"));
        passwordPane.setEditable(false);
        passwordPane.setForeground(Color.WHITE);
        passwordPane.setOpaque(false);
        passwordPane.setFont(App.SMALL_FONT);
        constraints.gridy = 4;
        this.add(passwordPane, constraints);

        JTextPane portPane = new JTextPane();
        portPane.setText(Language.DEFAULT.getString("port_description"));
        portPane.setEditable(false);
        portPane.setForeground(Color.WHITE);
        portPane.setOpaque(false);
        portPane.setFont(App.SMALL_FONT);
        constraints.gridy = 6;
        this.add(portPane, constraints);

        JTextPane databasePane = new JTextPane();
        databasePane.setText(Language.DEFAULT.getString("database_description"));
        databasePane.setEditable(false);
        databasePane.setForeground(Color.WHITE);
        databasePane.setOpaque(false);
        databasePane.setFont(App.SMALL_FONT);
        constraints.gridy = 8;
        this.add(databasePane, constraints);


        this.submitButtonView = new RoundedButtonView(Language.DEFAULT.getString("next"));
        this.submitButtonView.setMinimumSize(new Dimension(270, 40));
        this.setSubmitButtonEnabled(false);
        constraints.gridx = 0;
        constraints.gridy = 9;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(30, 20, 30, 20);
        constraints.fill = GridBagConstraints.NONE;
        this.add(submitButtonView, constraints);

        this.nickFieldView.getTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if(listener != null) {
                    listener.setupWindowDidNickChange(nickFieldView.getTextField().getText());
                }
            }
        });

        this.passwordFieldView.getPasswordField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if(listener != null) {
                    listener.setupWindowDidPasswordChange(passwordFieldView.getPasswordField().getPassword());
                }
            }
        });

        this.portFieldView.getTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if(listener != null) {
                    listener.setupWindowDidPortChange(portFieldView.getTextField().getText());
                }
            }
        });

        this.databaseFieldView.getTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if(listener != null) {
                    listener.setupWindowDidDatabaseFileChange(databaseFieldView.getTextField().getText());
                }
            }
        });

        this.submitButtonView.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    final String nick = nickFieldView.getTextField().getText();
                    final char[] password = passwordFieldView.getPasswordField().getPassword();
                    final String port = portFieldView.getTextField().getText();
                    final String dbFilename = databaseFieldView.getTextField().getText();
                    listener.setupWindowDidSubmit(nick, password, port, dbFilename);
                }
            }
        });
    }

    public void setNickFieldViewBackgroundColor(Color color) {
        this.nickFieldView.setBackground(color);
    }

    public void setPasswordFieldViewBackgroundColor(Color color) {
        this.passwordFieldView.setBackground(color);
    }

    public void setPortFieldViewBackgroundColor(Color color) {
        this.portFieldView.setBackground(color);
    }

    public void setDatabaseFieldViewBackgroundColor(Color color) {
        this.databaseFieldView.setBackground(color);
    }

    public void setSubmitButtonText(String text) {
        this.submitButtonView.getButton().setText(text);
    }

    public void setSubmitButtonEnabled(boolean enabled) {
        if(enabled) {
            this.submitButtonView.enableButton();
        }
        else {
            this.submitButtonView.disableButton();
        }
    }
}
