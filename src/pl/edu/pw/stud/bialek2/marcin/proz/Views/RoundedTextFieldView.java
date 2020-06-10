package pl.edu.pw.stud.bialek2.marcin.proz.views;

import java.awt.Color;
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JTextField;


public class RoundedTextFieldView extends RoundedView {
    private JTextField textField;

    public RoundedTextFieldView(int cornerRadius) {
        super(cornerRadius);
        this.setBackground(Color.WHITE);
        this.getSafeArea().setLayout(new BorderLayout());
        this.textField = new JTextField();
        this.textField.setOpaque(false);
        //this.textField.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        this.getSafeArea().add(this.textField, BorderLayout.CENTER);
    }

    public RoundedTextFieldView() {
        this(15);
    }

    public JTextField getTextField() {
        return this.textField;
    }
}
