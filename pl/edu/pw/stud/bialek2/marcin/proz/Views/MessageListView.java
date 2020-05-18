package pl.edu.pw.stud.bialek2.marcin.proz.Views;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;

import pl.edu.pw.stud.bialek2.marcin.proz.Message;


public class MessageListView extends JPanel {
    private final JPanel listPanel = new JPanel();

    public MessageListView() {
        this.setLayout(new BorderLayout());
        this.initComponents();
    }

    private void initComponents() {
        this.listPanel.setLayout(new BoxLayout(this.listPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void appendMessage(Message message) {
        this.listPanel.add(new MessageListRowView(message));
        this.listPanel.revalidate();
    }
}
