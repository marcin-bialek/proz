package pl.edu.pw.stud.bialek2.marcin.proz.views.addchat;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Chatroom;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;
import java.awt.GridBagConstraints;
import java.awt.CardLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class AddChatroomWindow extends JFrame {
    private AddChatroomWindowListener listener;

    public AddChatroomWindow() {
        super(App.APP_DISPLAY_NAME);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(new Dimension(350, 250));
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.initComponents();
        this.setVisible(true);
    }

    public void setListener(AddChatroomWindowListener listener) {
        this.listener = listener;
    }

    private void initComponents() {
        final JPanel connectToChatPanel = this.makeConnectToChatPanel();
        final JPanel newChatPanel = this.makeNewChatPanel();

        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Utwórz nowy chat", newChatPanel);
        tabbedPane.addTab("Dołącz do chatu", connectToChatPanel);
        this.add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel makeConnectToChatPanel() {
        final JPanel view = new JPanel();
        view.setLayout(new GridBagLayout());

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        final JTextField uuidField = new JTextField();
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        view.add(uuidField, constraints);

        final JTextField addressField = new JTextField();
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        view.add(addressField, constraints);

        final JTextField portField = new JTextField();
        constraints.gridx = 2;
        constraints.gridwidth = 1;
        view.add(portField, constraints);

        final JButton connectButton = new JButton("Dołącz");
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.CENTER;
        view.add(connectButton, constraints);

        return view;
    }

    private JPanel makeNewChatPanel() {
        final JPanel view = new JPanel();
        view.setLayout(new GridBagLayout());

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridx = 0;
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        final JTextField nameField = new JTextField();
        constraints.gridy = 0;
        view.add(nameField, constraints);

        final UUID uuid = UUID.randomUUID();
        final JTextField uuidField = new JTextField();
        uuidField.setText(uuid.toString());
        uuidField.setEditable(false);
        constraints.gridy = 1;
        view.add(uuidField, constraints);

        final JButton createButton = new JButton("Utwórz");
        constraints.gridy = 2;
        constraints.fill = GridBagConstraints.CENTER;
        view.add(createButton, constraints);

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    final Chatroom chatroom = new Chatroom(uuid, nameField.getText());
                    listener.addChatroomWindowDidCreateChatroom(chatroom);
                }
            }
        });

        return view;
    }
}