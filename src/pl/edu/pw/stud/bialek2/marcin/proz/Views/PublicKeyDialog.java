package pl.edu.pw.stud.bialek2.marcin.proz.views;

import java.awt.Frame;
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JTextArea;


public class PublicKeyDialog extends JDialog {
    private static final long serialVersionUID = -7920131074930451582L;

    public PublicKeyDialog(Frame owner, String title, String key) {
        super(owner, title);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setSize(460, 300);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        final JTextArea text = new JTextArea(key);
        text.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        text.setEditable(false);
        text.setOpaque(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        this.add(text, BorderLayout.CENTER);
        this.setVisible(true);
    }
}
