package pl.edu.pw.stud.bialek2.marcin.proz.Views;

import pl.edu.pw.stud.bialek2.marcin.proz.*;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import java.awt.Container;


public class ChatroomListView extends JPanel {
    private final JPanel newChatroomPanel = new JPanel();
    private final JPanel listPanel = new JPanel();

    public ChatroomListView() {
        Dimension dimension = this.getPreferredSize();
        dimension.width = 250;
        this.setPreferredSize(dimension);
        this.setLayout(new BorderLayout());
        this.setBackground(App.BACKGROUND_COLOR);
        this.initComponents();
    }    

    private void initComponents() {
        //this.newChatroomPanel.setBackground(Color.RED);
        //this.add(this.newChatroomPanel, BorderLayout.NORTH);

        this.listPanel.setBackground(App.BACKGROUND_COLOR);
        this.listPanel.setLayout(new BoxLayout(this.listPanel, BoxLayout.Y_AXIS));

        JScrollPane listScrollPane = new JScrollPane(this.listPanel);
        listScrollPane.setBackground(App.BACKGROUND_COLOR);
        listScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        listScrollPane.setLayout(new ScrollPaneLayout() {
            @Override
            public void layoutContainer(Container parent) {
                JScrollPane scrollPane = (JScrollPane)parent;
                scrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                super.layoutContainer(parent);
                scrollPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            }
        });

        this.add(listScrollPane, BorderLayout.CENTER);
    }

    public void appendChatroom(Chatroom chatroom) {
        this.listPanel.add(new ChatroomListRowView(chatroom));
        this.listPanel.revalidate();
    }
}