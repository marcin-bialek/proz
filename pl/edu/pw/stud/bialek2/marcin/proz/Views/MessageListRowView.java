package pl.edu.pw.stud.bialek2.marcin.proz.Views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Scrollable;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.Message;

public class MessageListRowView extends JPanel implements Scrollable {
    private Message message;

    public MessageListRowView(Message message) {
        this.message = message;

        this.setLayout(new BorderLayout());
        this.initComponents();
    }

    private void initComponents() {
        RoundedView bubble = new RoundedView(15);
        bubble.setBackground(App.ACCENT_COLOR);

        bubble.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JTextArea text = new JTextArea(this.message.getText());
        text.setForeground(Color.DARK_GRAY);
        text.setOpaque(false);
        text.setEditable(false);
        text.setLineWrap(true);

        bubble.getSafeArea().setLayout(new BorderLayout());
        bubble.getSafeArea().add(text, BorderLayout.CENTER);
        bubble.getSafeArea().setOpaque(false);

        // GridBagConstraints constraints = new GridBagConstraints();
        // constraints.anchor = GridBagConstraints.LINE_START;
        // constraints.fill = GridBagConstraints.HORIZONTAL;
        // constraints.gridx = 0;
        // constraints.gridy = 0;
        // constraints.weightx = 2;
        // constraints.weighty = 1;
        // this.add(bubble, constraints);

        // constraints.gridx = 1;
        // constraints.weightx = 1;
        // this.add(new JPanel(), constraints);

        this.add(bubble, BorderLayout.CENTER);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return this.getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        // TODO Auto-generated method stub
        return false;
    }
}