package pl.edu.pw.stud.bialek2.marcin.proz.views.home;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.views.RoundedScrollView;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import javax.swing.JPanel;


public class MessageListView extends JPanel {
    private static final long serialVersionUID = -1080340830024397623L;
    private JPanel listPanel;
    private int elementsCount = 0;

    public MessageListView() {
        this.setLayout(new BorderLayout());
        this.initComponents();
    }

    private void initComponents() {
        this.listPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                Dimension dimension = super.getPreferredSize();
                dimension.width = this.getParent().getSize().width;
                return dimension;
            }
        };

        this.listPanel.setBackground(Color.WHITE);
        this.listPanel.setLayout(new GridBagLayout());

        RoundedScrollView scrollPane = new RoundedScrollView(listPanel);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void appendMessage(Message message) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = this.elementsCount;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        this.listPanel.add(new MessageListRowView(message), constraints);
        this.listPanel.revalidate();

        this.elementsCount++;
    }
}
