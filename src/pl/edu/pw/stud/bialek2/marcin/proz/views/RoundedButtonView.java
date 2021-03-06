package pl.edu.pw.stud.bialek2.marcin.proz.views;

import pl.edu.pw.stud.bialek2.marcin.proz.App;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JButton;


public class RoundedButtonView extends RoundedView {
    private static final long serialVersionUID = 4531592537899646569L;
    
    private Color backgroundColor;
    private JButton button;

    public RoundedButtonView(String text, int cornerRadius) {
        super(cornerRadius);
        this.setBackground(App.GREEN_COLOR);
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

    public void enableButton() {
        this.setBackground(this.backgroundColor);
        this.button.setEnabled(true);
    }

    public void disableButton() {
        final Color bg = this.backgroundColor;
        this.setBackground(Color.GRAY);
        this.backgroundColor = bg;
        this.button.setEnabled(false);
    }

    @Override
    public void setBackground(Color color) {
        this.backgroundColor = color;
        super.setBackground(color);
    }

    public boolean isButtonEnabled() {
        return this.button.isEnabled();
    }
}
