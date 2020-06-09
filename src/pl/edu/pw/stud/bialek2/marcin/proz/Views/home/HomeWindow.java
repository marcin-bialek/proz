package pl.edu.pw.stud.bialek2.marcin.proz.views.home;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;
import pl.edu.pw.stud.bialek2.marcin.proz.views.RoundedButtonView;
import pl.edu.pw.stud.bialek2.marcin.proz.views.RoundedScrollView;
import pl.edu.pw.stud.bialek2.marcin.proz.views.RoundedView;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import java.awt.Font;
import java.awt.Point;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;


public class HomeWindow extends JFrame {
    private static final long serialVersionUID = -7341554873868191886L;
    private static final String CHAT_CARD = "CHAT";
    private static final String SETTINGS_CARD = "SETTINGS";
    private static BufferedImage addImage;
    private static BufferedImage gearImage;
    private HomeWindowListener listener;
    private JPanel centerPanel;
    private CardLayout centerPanelLayout;
    private JPanel peersWrapperPanel;
    private JPanel messagesWrapperPanel;
    private RoundedScrollView messagesScrollView;
    private JTextArea nickValueArea;
    private JTextArea localAddressValueArea;
    private JTextArea externalAddressValueArea;
    private JTextArea portValueArea;
    private JTextArea publicKeyValueArea;

    public HomeWindow(Dimension windowSize) {
        super(App.APP_DISPLAY_NAME);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setSize(windowSize);
        this.setMinimumSize(new Dimension(670, 460));
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(App.BACKGROUND_COLOR);
        this.initComponents();
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                if(listener != null) {
                    listener.homeWindowDidClose();
                }
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                if(listener != null) {
                    listener.homeWindowDidResize(event.getComponent().getSize());
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
        final JPanel peerListPanel = this.makePeerListPanel();
        final JPanel peerInfoPanel = this.makePeerInfoPanel();
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
        leftPanel.add(peerListPanel, BorderLayout.CENTER);

        final JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.add(peerInfoPanel, BorderLayout.NORTH);
        chatPanel.add(messageListPanel, BorderLayout.CENTER);
        chatPanel.add(messageInputPanel, BorderLayout.SOUTH);

        this.centerPanel = new JPanel();
        this.centerPanelLayout = new CardLayout();
        this.centerPanel.setLayout(this.centerPanelLayout);
        this.centerPanel.add(CHAT_CARD, chatPanel);
        this.centerPanel.add(SETTINGS_CARD, settingsPanel);
        this.centerPanelLayout.show(this.centerPanel, SETTINGS_CARD);

        this.add(leftPanel, BorderLayout.WEST);
        this.add(this.centerPanel, BorderLayout.CENTER);
    }

    private JPanel makeControlPanel() {
        final JButton addPeerButton = new JButton(new ImageIcon(addImage.getScaledInstance(20, 20, Image.SCALE_FAST)));
        addPeerButton.setOpaque(false);
        addPeerButton.setContentAreaFilled(false);
        addPeerButton.setBorderPainted(false);
        addPeerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.homeWindowDidClickAddPeerButton();
                }
            }    
        });

        final JButton settingsButton = new JButton(new ImageIcon(gearImage.getScaledInstance(20, 20, Image.SCALE_FAST)));
        settingsButton.setOpaque(false);
        settingsButton.setContentAreaFilled(false);
        settingsButton.setBorderPainted(false);
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
        view.add(addPeerButton, BorderLayout.WEST);
        view.add(settingsButton, BorderLayout.EAST);
        return view;
    }

    private JPanel makePeerListPanel() {
        peersWrapperPanel = new JPanel();
        peersWrapperPanel.setBackground(App.BACKGROUND_COLOR);;
        peersWrapperPanel.setLayout(new BoxLayout(peersWrapperPanel, BoxLayout.Y_AXIS));

        final RoundedScrollView scrollView = new RoundedScrollView(peersWrapperPanel);
        scrollView.setBackground(App.BACKGROUND_COLOR);

        final JPanel view = new JPanel();
        view.setOpaque(false);
        view.setLayout(new BorderLayout());
        view.add(scrollView, BorderLayout.CENTER);
        return view;
    }

    private JPanel makePeerInfoPanel() {
        final JLabel nickLabel = new JLabel("Elo");

        final JPanel view = new JPanel();
        view.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
        view.setLayout(new BorderLayout());
        view.add(nickLabel, BorderLayout.CENTER);
        return view;
    }

    private JPanel makeMessageListPanel() {
        this.messagesWrapperPanel = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                Dimension dimension = super.getPreferredSize();
                dimension.width = this.getParent().getSize().width - 20;
                return dimension;
            }
        };

        this.messagesWrapperPanel.setLayout(new BoxLayout(this.messagesWrapperPanel, BoxLayout.Y_AXIS));
        final JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(messagesWrapperPanel, BorderLayout.PAGE_END);
        this.messagesScrollView = new RoundedScrollView(wrapper);
        final JPanel view = new JPanel();
        view.setLayout(new BorderLayout());
        view.add(this.messagesScrollView, BorderLayout.CENTER);
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
        final JPanel view = new JPanel(new GridBagLayout());
        final JPanel wrapper = new JPanel(new GridBagLayout()); 
        wrapper.setMinimumSize(new Dimension(400, 400));
        wrapper.setPreferredSize(new Dimension(400, 400));
        wrapper.setMaximumSize(new Dimension(400, 400));
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        view.add(wrapper);

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 0;

        final JLabel nickLabel = new JLabel("Mój nick:");
        constraints.weightx = 0.1;
        constraints.gridy = 0;
        constraints.insets = new Insets(0, 0, 0, 20);
        wrapper.add(nickLabel, constraints);

        final JLabel localAddressLabel = new JLabel("Mój adres lokalny:");
        constraints.gridy = 1;
        constraints.insets = new Insets(20, 0, 0, 20);
        wrapper.add(localAddressLabel, constraints);

        final JLabel externalAddressLabel = new JLabel("Mój adres zewnętrzny:");
        constraints.gridy = 3;
        wrapper.add(externalAddressLabel, constraints);

        final JLabel portLabel = new JLabel("Mój port:");
        constraints.gridy = 5;
        wrapper.add(portLabel, constraints);

        final JLabel publicKeyLabel = new JLabel("Mój klucz publiczny:");
        constraints.gridy = 6;
        wrapper.add(publicKeyLabel, constraints);

        this.nickValueArea = new JTextArea("...");
        this.nickValueArea.setOpaque(false);
        this.nickValueArea.setEditable(false);
        this.nickValueArea.setLineWrap(true);
        this.nickValueArea.setWrapStyleWord(true);
        constraints.weightx = 0.9;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.insets = new Insets(0, 0, 0, 0);
        wrapper.add(this.nickValueArea, constraints);

        this.localAddressValueArea = new JTextArea("...");
        this.localAddressValueArea.setOpaque(false);
        this.localAddressValueArea.setEditable(false);
        this.localAddressValueArea.setLineWrap(true);
        this.localAddressValueArea.setWrapStyleWord(true);
        constraints.gridy = 1;
        constraints.insets = new Insets(20, 0, 0, 0);
        wrapper.add(this.localAddressValueArea, constraints);

        this.externalAddressValueArea = new JTextArea("...");
        this.externalAddressValueArea.setOpaque(false);
        this.externalAddressValueArea.setEditable(false);
        this.externalAddressValueArea.setLineWrap(true);
        this.externalAddressValueArea.setWrapStyleWord(true);
        constraints.gridy = 3;
        wrapper.add(this.externalAddressValueArea, constraints);

        this.portValueArea = new JTextArea("...");
        this.portValueArea.setOpaque(false);
        this.portValueArea.setEditable(false);
        this.portValueArea.setLineWrap(true);
        this.portValueArea.setWrapStyleWord(true);
        constraints.gridy = 5;
        wrapper.add(this.portValueArea, constraints);

        this.publicKeyValueArea = new JTextArea("...");
        this.publicKeyValueArea.setOpaque(false);
        this.publicKeyValueArea.setEditable(false);
        this.publicKeyValueArea.setLineWrap(true);
        this.publicKeyValueArea.setWrapStyleWord(true);
        constraints.gridy = 6;
        wrapper.add(this.publicKeyValueArea, constraints);

        final Font SMALL_FONT = new Font("Verdana", Font.PLAIN, 9);

        final JTextArea localAddressArea = new JTextArea("Podaj ten adres osobom, które znajdują się w tej samej sieci lokalnej.");
        localAddressArea.setFont(SMALL_FONT);
        localAddressArea.setOpaque(false);
        localAddressArea.setEditable(false);
        localAddressArea.setLineWrap(true);
        localAddressArea.setWrapStyleWord(true);
        constraints.gridy = 2;
        constraints.insets = new Insets(3, 0, 0, 0);
        wrapper.add(localAddressArea, constraints);

        final JTextArea externalAddressArea = new JTextArea("Podaj ten adres osobom, które chcę połączyć się z Tobą przez internet. Aby się to udało, może być konieczne odpowiednie skonfigurowanie routera.");
        externalAddressArea.setFont(SMALL_FONT);
        externalAddressArea.setOpaque(false);
        externalAddressArea.setEditable(false);
        externalAddressArea.setLineWrap(true);
        externalAddressArea.setWrapStyleWord(true);
        constraints.gridy = 4;
        wrapper.add(externalAddressArea, constraints);

        final JTextArea publicKeyArea = new JTextArea("Każda osoba przyjmująca od Ciebie połączenie zobaczy ten ciąg. Dzięki niemu będzie mogła zweryfikować Twoją tożsamość.");
        publicKeyArea.setFont(SMALL_FONT);
        publicKeyArea.setOpaque(false);
        publicKeyArea.setEditable(false);
        publicKeyArea.setLineWrap(true);
        publicKeyArea.setWrapStyleWord(true);
        constraints.gridy = 7;
        wrapper.add(publicKeyArea, constraints);

        final RoundedButtonView deleteDataButtonView = new RoundedButtonView("Usuń dane");
        deleteDataButtonView.setBackground(App.RED_COLOR);
        constraints.gridx = 0;
        constraints.gridy = 8;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.insets = new Insets(40, 0, 0, 0);
        wrapper.add(deleteDataButtonView, constraints);

        deleteDataButtonView.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(listener != null) {
                    listener.homeWindowDidClickDeleteDataButton();
                }
            }
        });

        return view;
    }

    public PeerRowView appendPeer(Peer peer) {
        final PeerRowView row = new PeerRowView(peer);

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
                    listener.homeWindowDidChangePeer(peer);
                }
            }
        });

        this.insertPeerRow(row);
        return row;
    }

    public void insertPeerRow(PeerRowView row) {
        this.peersWrapperPanel.add(row, 0);
        this.peersWrapperPanel.revalidate();
    }

    public void removePeerRow(PeerRowView row) {
        this.peersWrapperPanel.remove(row);
        this.peersWrapperPanel.revalidate();
    }

    public void scrollMessagesToBottom() {
        JScrollBar bar = this.messagesScrollView.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
        bar.revalidate();
    }

    public void appendMessageToTop(Message message) {
        final MessageRowView row = new MessageRowView(message, messagesWrapperPanel);
        this.messagesWrapperPanel.add(row, 0);
        this.messagesWrapperPanel.revalidate();
    }

    public void appendMessageToBottom(Message message) {
        final MessageRowView row = new MessageRowView(message, messagesWrapperPanel);
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

    public void setSettingsPanelInfo(String nick, String localAddress, String externalAddress, String port, String publicKey) {
        this.nickValueArea.setText(nick);
        this.localAddressValueArea.setText(localAddress);
        this.externalAddressValueArea.setText(externalAddress);
        this.portValueArea.setText(port);
        this.publicKeyValueArea.setText(publicKey.substring(0, 56) + "...");
    }

    public void showSettingsPanel() {
        this.centerPanelLayout.show(this.centerPanel, SETTINGS_CARD);
    }

    static {
        try {
            addImage = ImageIO.read(HomeWindow.class.getResource("/resources/add.png"));
            gearImage = ImageIO.read(HomeWindow.class.getResource("/resources/gear.png"));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}

