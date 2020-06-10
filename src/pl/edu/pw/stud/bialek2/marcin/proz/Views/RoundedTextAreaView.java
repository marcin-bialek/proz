package pl.edu.pw.stud.bialek2.marcin.proz.views;

import java.awt.Color;
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;


public class RoundedTextAreaView extends RoundedView {
    private JTextArea textArea;

    public RoundedTextAreaView(int cornerRadius) {
        super(cornerRadius);
        this.setBackground(Color.WHITE);
        this.getSafeArea().setLayout(new BorderLayout());
        this.textArea = new JTextArea();
        this.textArea.setOpaque(false);
        this.textArea.setLineWrap(true);
        this.textArea.setWrapStyleWord(true);
        this.textArea.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 7));
        this.getSafeArea().add(this.textArea, BorderLayout.CENTER);
    }

    public RoundedTextAreaView() {
        this(15);
    }

    public JTextArea getTextArea() {
        return this.textArea;
    }
}
