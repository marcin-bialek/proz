package pl.edu.pw.stud.bialek2.marcin.proz.views.setup;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class SetupWindow extends JFrame {
    private static final long serialVersionUID = 206072431562958302L;
    private SetupWindowListener listener;

    public SetupWindow() {
        super("Chat");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(300, 400));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.initComponents();
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
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
        JTextField nickField = new JTextField(32);
        this.add(nickField);

        JButton submitButton = new JButton("Zapisz");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.setupWindowDidSubmit(nickField.getText());
                }
            }
        });

        this.add(submitButton);
    }
}
