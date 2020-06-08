package pl.edu.pw.stud.bialek2.marcin.proz.views;

import java.awt.Color;
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPasswordField;


public class RoundedPasswordFieldView extends RoundedView {
    private JPasswordField passwordField;

    public RoundedPasswordFieldView(int cornerRadius) {
        super(cornerRadius);
        this.setBackground(Color.WHITE);
        this.getSafeArea().setLayout(new BorderLayout());
        this.passwordField = new JPasswordField();
        this.passwordField.setOpaque(false);
        this.passwordField.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 7));
        this.getSafeArea().add(this.passwordField, BorderLayout.CENTER);
    }

    public RoundedPasswordFieldView() {
        this(15);
    }

    public JPasswordField getPasswordField() {
        return this.passwordField;
    }
}
