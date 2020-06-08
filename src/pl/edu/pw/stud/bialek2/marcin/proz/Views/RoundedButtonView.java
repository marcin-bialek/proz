package pl.edu.pw.stud.bialek2.marcin.proz.views;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;


public class RoundedButtonView extends RoundedView {
    private JButton button;

    public RoundedButtonView(String text, int cornerRadius) {
        super(cornerRadius);
        this.getSafeArea().setLayout(new BorderLayout());
        this.button = new JButton(text);
        this.button.setContentAreaFilled(false);
        this.button.setBorderPainted(false);
        this.button.setOpaque(false);
        this.button.setForeground(Color.WHITE);
        this.add(button, BorderLayout.CENTER);
    }

    public RoundedButtonView(String text) {
        this(text, 15);
    }

    public JButton getButton() {
        return this.button;
    }
}
