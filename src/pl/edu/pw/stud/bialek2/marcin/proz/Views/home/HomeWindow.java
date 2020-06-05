package pl.edu.pw.stud.bialek2.marcin.proz.views.home;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Chatroom;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.views.RoundedScrollView;
import pl.edu.pw.stud.bialek2.marcin.proz.views.RoundedView;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.CardLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;


public class HomeWindow extends JFrame {
    private static final long serialVersionUID = -7341554873868191886L;
    private static final String CHAT_CARD = "CHAT";
    private static final String SETTINGS_CARD = "SETTINGS";
    private HomeWindowListener listener;
    private JPanel centerPanel;
    private CardLayout centerPanelLayout;
    private JPanel chatroomsWrapperPanel;
    private JPanel messagesWrapperPanel;
    private JScrollBar messagesScrollBar;

    public HomeWindow(Dimension windowSize) {
        super(App.APP_DISPLAY_NAME);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(windowSize);
        this.setMinimumSize(new Dimension(550, 350));
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(App.BACKGROUND_COLOR);
        this.initComponents();
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (listener != null) {
                    listener.homeWindowDidClose();
                }
            }
        });
    }

    public HomeWindow() {
        this(new Dimension(App.DEFAULT_WINDOW_WIDTH, App.DEFAULT_WINDOW_HEIGHT));
    }

    public void setListener(HomeWindowListener listener) {
        this.listener = listener;
    }

    private void initComponents() {
        final JPanel controlPanel = this.makeControlPanel();
        final JPanel chatroomListPanel = this.makeChatroomListPanel();
        final JPanel messageListPanel = this.makeMessageListPanel();
        final JPanel messageInputPanel = this.makeMessageInputPanel();
        final JPanel settingsPanel = this.makeSettingsPanel();

        final JPanel leftPanel = new JPanel();
        Dimension laftPanelDimension = leftPanel.getPreferredSize();
        laftPanelDimension.width = 250;
        leftPanel.setPreferredSize(laftPanelDimension);
        leftPanel.setBackground(App.BACKGROUND_COLOR);
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(controlPanel, BorderLayout.NORTH);
        leftPanel.add(chatroomListPanel, BorderLayout.CENTER);

        final JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.add(messageListPanel, BorderLayout.CENTER);
        chatPanel.add(messageInputPanel, BorderLayout.SOUTH);

        this.centerPanel = new JPanel();
        this.centerPanelLayout = new CardLayout();
        this.centerPanel.setLayout(this.centerPanelLayout);
        this.centerPanel.add("CHAT", chatPanel);
        this.centerPanel.add("SETTINGS", settingsPanel);

        this.add(leftPanel, BorderLayout.WEST);
        this.add(this.centerPanel, BorderLayout.CENTER);
    }

    private JPanel makeControlPanel() {
        final JButton addChatroomButton = new JButton("+");
        addChatroomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.homeWindowDidClickAddChatroomButton();
                }
            }    
        });

        final JButton settingsButton = new JButton("*");
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.homeWindowDidClickSettingsButton();
                }
            }    
        });

        final JPanel view = new JPanel();
        view.setOpaque(false);
        view.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        view.setLayout(new BorderLayout());
        view.add(addChatroomButton, BorderLayout.WEST);
        view.add(settingsButton, BorderLayout.EAST);
        return view;
    }

    private JPanel makeChatroomListPanel() {
        chatroomsWrapperPanel = new JPanel();
        chatroomsWrapperPanel.setBackground(App.BACKGROUND_COLOR);;
        chatroomsWrapperPanel.setLayout(new BoxLayout(chatroomsWrapperPanel, BoxLayout.Y_AXIS));

        final RoundedScrollView scrollView = new RoundedScrollView(chatroomsWrapperPanel);
        scrollView.setBackground(App.BACKGROUND_COLOR);

        final JPanel view = new JPanel();
        view.setOpaque(false);
        view.setLayout(new BorderLayout());
        view.add(scrollView, BorderLayout.CENTER);
        return view;
    }

    private JPanel makeMessageListPanel() {
        this.messagesWrapperPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                Dimension dimension = super.getPreferredSize();
                dimension.width = this.getParent().getSize().width;
                return dimension;
            }
        };

        this.messagesWrapperPanel.setLayout(new BoxLayout(this.messagesWrapperPanel, BoxLayout.Y_AXIS));

        final RoundedScrollView scrollView = new RoundedScrollView(this.messagesWrapperPanel);
        this.messagesScrollBar = scrollView.getVerticalScrollBar();
        final JPanel view = new JPanel();
        view.setLayout(new BorderLayout());
        view.add(scrollView, BorderLayout.CENTER);
        return view;
    }

    private JPanel makeMessageInputPanel() {
        final JTextArea textInput = new JTextArea();
        textInput.setLineWrap(true);

        textInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if(event.getKeyCode() == KeyEvent.VK_ENTER) {
                    event.consume();
                    final String text = textInput.getText();
                    textInput.setText("");
                    listener.homeWindowDidEnterMessage(text);
                }
            }
        });
    
        final RoundedView textInputWrapper = new RoundedView(15);
        textInputWrapper.setBackground(Color.WHITE);
        textInputWrapper.getSafeArea().setLayout(new BorderLayout());
        textInputWrapper.getSafeArea().add(textInput, BorderLayout.CENTER);

        final JPanel view = new JPanel();
        view.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
        view.setLayout(new BorderLayout());
        view.add(textInputWrapper, BorderLayout.CENTER);
        return view;
    }

    private JPanel makeSettingsPanel() {
        final JPanel view = new JPanel();
        view.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        view.setLayout(new BorderLayout());
        view.add(new JLabel("Settings"), BorderLayout.NORTH);
        return view;
    }

    public void appendChatroom(Chatroom chatroom) {
        final ChatroomRowView row = new ChatroomRowView(chatroom);

        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(App.PRIMARY_COLOR);
                row.revalidate();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(App.BACKGROUND_COLOR);
                row.revalidate();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if(listener != null) {
                    listener.homeWindowDidChangeChatroom(chatroom);
                }
            }
        });

        this.chatroomsWrapperPanel.add(row, 0);
        this.chatroomsWrapperPanel.revalidate();
    }

    public void scrollMessagesToBottom() {
        this.messagesScrollBar.setValue(this.messagesScrollBar.getMaximum());
    }

    public void appendMessageToTop(Message message) {
        final MessageRowView row = new MessageRowView(message);
        this.messagesWrapperPanel.add(row, 0);
        this.messagesWrapperPanel.revalidate();
    }

    public void appendMessageToBottom(Message message) {
        final MessageRowView row = new MessageRowView(message);
        this.messagesWrapperPanel.add(row);
        this.messagesWrapperPanel.revalidate();
    }

    public void clearMessages() {
        this.messagesWrapperPanel.removeAll();
        this.messagesWrapperPanel.revalidate();
        this.messagesWrapperPanel.repaint();
    }

    public void showChatPanel() {
        this.centerPanelLayout.show(this.centerPanel, CHAT_CARD);
    }

    public void showSettingsPanel() {
        this.centerPanelLayout.show(this.centerPanel, SETTINGS_CARD);
    }
}

