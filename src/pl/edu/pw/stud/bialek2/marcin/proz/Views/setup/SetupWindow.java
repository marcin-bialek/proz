package pl.edu.pw.stud.bialek2.marcin.proz.views.setup;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.views.RoundedButtonView;
import pl.edu.pw.stud.bialek2.marcin.proz.views.RoundedPasswordFieldView;
import pl.edu.pw.stud.bialek2.marcin.proz.views.RoundedTextFieldView;

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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JTextField;


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

        JTextArea info = new JTextArea("Zanim zaczniesz korzystać z chatu musisz ustawić kilka rzeczy.");
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

        JLabel nickLabel = new JLabel("Nick:");
        nickLabel.setForeground(Color.WHITE);
        constraints.weightx = 0.1;
        constraints.gridwidth = 1;
        constraints.gridy = 1;
        constraints.insets = new Insets(15, 20, 0, 20);
        this.add(nickLabel, constraints);

        JLabel portLabel = new JLabel("Hasło:");
        portLabel.setForeground(Color.WHITE);
        constraints.gridy = 3;
        this.add(portLabel, constraints);

        JLabel passwordLabel = new JLabel("Port:");
        passwordLabel.setForeground(Color.WHITE);
        constraints.gridy = 5;
        this.add(passwordLabel, constraints);

        JLabel databaseLabel = new JLabel("Baza danych:");
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
        nickPane.setText("To Twój identyfikator, który będą widzieli Twoi znajomi. Jego zmiana będzie możliwa jedynie poprzez usunięcie danych aplikacji i ponowną instalację.");
        nickPane.setEditable(false);
        nickPane.setForeground(Color.WHITE);
        nickPane.setOpaque(false);
        nickPane.setFont(App.SMALL_FONT);
        constraints.gridy = 2;
        constraints.insets = new Insets(5, 0, 0, 20);
        this.add(nickPane, constraints);

        JTextPane passwordPane = new JTextPane();
        passwordPane.setText("Hasło chroni Twoje ustawienia i wiadomości przed niepowołanym dostępem. Trzeba będzie je wprowadzić przy każdym uruchomieniu aplikacji. Musi składać się co najmniej z 6 znaków. Pamiętaj, że hasła nie da się odzyskać.");
        passwordPane.setEditable(false);
        passwordPane.setForeground(Color.WHITE);
        passwordPane.setOpaque(false);
        passwordPane.setFont(App.SMALL_FONT);
        constraints.gridy = 4;
        this.add(passwordPane, constraints);

        JTextPane portPane = new JTextPane();
        portPane.setText("Jest to punkt połączenia do wymiany informacji. Twoi znajomi będą musieli go podać (razem z adresem), aby się z Tobą połączyć. Możesz zostawić to ustawienie domyślne. Jeśli aplikacji wykryje, że podany port jest już używany, poprosi Cię o jego zmianę.");
        portPane.setEditable(false);
        portPane.setForeground(Color.WHITE);
        portPane.setOpaque(false);
        portPane.setFont(App.SMALL_FONT);
        constraints.gridy = 6;
        this.add(portPane, constraints);

        JTextPane databasePane = new JTextPane();
        databasePane.setText("Nazwa pliku, w którym będą zapisywani Twoi znajomi i wiadomości. Możesz zostawić to ustawienie domyślne.");
        databasePane.setEditable(false);
        databasePane.setForeground(Color.WHITE);
        databasePane.setOpaque(false);
        databasePane.setFont(App.SMALL_FONT);
        constraints.gridy = 8;
        this.add(databasePane, constraints);


        this.submitButtonView = new RoundedButtonView("Dalej");
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
