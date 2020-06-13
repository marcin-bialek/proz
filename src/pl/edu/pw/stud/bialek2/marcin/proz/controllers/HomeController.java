package pl.edu.pw.stud.bialek2.marcin.proz.controllers;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.Language;
import pl.edu.pw.stud.bialek2.marcin.proz.models.ImageMessage;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.models.P2PSession;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;
import pl.edu.pw.stud.bialek2.marcin.proz.models.PeerStatus;
import pl.edu.pw.stud.bialek2.marcin.proz.models.TextMessage;
import pl.edu.pw.stud.bialek2.marcin.proz.services.P2PService;
import pl.edu.pw.stud.bialek2.marcin.proz.views.AddPeerWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.AddPeerWindowListener;
import pl.edu.pw.stud.bialek2.marcin.proz.views.DeleteDataWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.DeleteDataWindowListener;
import pl.edu.pw.stud.bialek2.marcin.proz.views.DeletePeerWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.DeletePeerWindowListener;
import pl.edu.pw.stud.bialek2.marcin.proz.views.HomeWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.HomeWindowListener;
import pl.edu.pw.stud.bialek2.marcin.proz.views.PeerRowView;
import pl.edu.pw.stud.bialek2.marcin.proz.views.PublicKeyDialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;


public class HomeController implements HomeWindowListener {
	private HomeControllerDelegate delegate;
	private HomeWindow view;
	private Peer activePeer;
	private HashMap<Integer, PeerRowView> rows;

    public HomeController(HomeWindow view, HashMap<String, Peer> peers) {
		this.view = view;
		view.setListener(this);
		this.rows = new HashMap<>();
		
		for(Map.Entry<String, Peer> entry : peers.entrySet()) {
			this.rows.put(entry.getValue().getId(), view.appendPeer(entry.getValue()));
		}
	}
	
	public void setDelegate(HomeControllerDelegate delegate) {
		this.delegate = delegate;
	}

	public void newPeer(Peer peer) {
		this.rows.put(peer.getId(), this.view.appendPeer(peer));
	}

	public void newMessage(Message message) {
		final PeerRowView row = this.rows.get(message.getPeer().getId());
		this.view.removePeerRow(row);
		this.view.insertPeerRow(row);

		if(this.activePeer == null || message.getPeer().getId() != this.activePeer.getId()) {
			row.updateLastMessage(true);
		}
		else {
			this.view.appendMessageToBottom(message);
			this.view.scrollMessagesToBottom();
			row.updateLastMessage(false);
		}
	}

	public void updatePeerStatus(Peer peer, PeerStatus status) {
		final PeerRowView row = this.rows.get(peer.getId());

		if(row != null) {
			if(status == PeerStatus.OFFLINE) {
				row.setPeerOffline();
			}
			else if(status == PeerStatus.ONLINE) {
				row.setPeerOnline();
			}
		}

		if(peer == this.activePeer) {
			if(status == PeerStatus.ONLINE) {
				this.view.setMessageInputEnabled(true);
			}
			else {
				this.view.setMessageInputEnabled(false);
			}
		}
 	}

	public void closeWindow() {
		this.view.setVisible(false);
		this.view.dispose();
	}

	private void displayActivePeerMessages() {
		this.view.clearMessages();

		for(Message message : this.activePeer.getMessages()) {
			this.view.appendMessageToBottom(message);
		}
	}

	public void loadedMessagesFor(Peer peer) {
		if(peer == this.activePeer) {
			this.displayActivePeerMessages();
		}
	}

	public void setUserInfo(String nick, String localAddress, String externalAddress, String port, String publicKey) {
		this.view.setSettingsPanelInfo(nick, localAddress, externalAddress, port, publicKey);
	}

	@Override
	public void homeWindowDidResize(Dimension windowSize) {
		if(this.delegate != null) {
			this.delegate.homeControllerWindowDidResize(this, windowSize);
		}
	}

	@Override
	public void homeWindowDidClose() {
		if(this.delegate != null) {
			this.delegate.homeControllerDidExit(this);
		}
	}

	@Override
	public void homeWindowDidClickAddPeerButton() {
		final AddPeerWindow addPeerWindow = new AddPeerWindow();
		final HomeController sender = this;
	
		addPeerWindow.setListener(new AddPeerWindowListener() {
			private boolean isAddressValid = false;
			private boolean isPortValid = false;

			@Override
			public void addPeerWindowDidAddressChange(String address) {
				this.isAddressValid = address.trim().length() > 0;
				addPeerWindow.setAddressFieldBackground(this.isAddressValid ? Color.WHITE : App.LIGHT_RED_COLOR);
				addPeerWindow.setAddButtonEnabled(this.isAddressValid && this.isPortValid);
			}

			@Override
			public void addPeerWindowDidPortChange(String port) {
				try {
					final int portInt = Integer.parseInt(port.trim());
					this.isPortValid = P2PService.isValidPort(portInt);
				}
				catch(NumberFormatException e) {
					this.isPortValid = false;
				}

				addPeerWindow.setPortFieldBackground(this.isPortValid ? Color.WHITE : App.LIGHT_RED_COLOR);
				addPeerWindow.setAddButtonEnabled(this.isAddressValid && this.isPortValid);
			}

			@Override
			public void addPeerWindowDidAddPeer(String address, String port) {
				addPeerWindow.setVisible(false);
				addPeerWindow.dispose();
				final Peer peer = new Peer(address.trim(), Integer.parseInt(port.trim()));

				if(delegate != null) {
					delegate.homeControllerDidAddPeer(sender, peer);
				}
			}
		});
	}

	@Override
	public void homeWindowDidClickSettingsButton() {
		this.activePeer = null;
		this.view.showSettingsPanel();
	}

	@Override
	public void homeWindowDidClickDeleteDataButton() {
		final DeleteDataWindow deleteDataWindow = new DeleteDataWindow();
		final HomeController sender = this;

		deleteDataWindow.setListener(new DeleteDataWindowListener() {
			@Override
			public void deleteDataWindowDidConfirm() {
				deleteDataWindow.setVisible(false);
				deleteDataWindow.dispose();

				if(delegate != null) {
					delegate.homeControllerDeleteData(sender);
				}
			}
		
			@Override
			public void deleteDataWindowDidCancel() {
				deleteDataWindow.setVisible(false);
				deleteDataWindow.dispose();
			}
		});
	}

	@Override
	public void homeWindowDidChangePeer(Peer peer) {
		if(this.activePeer != peer) {
			this.activePeer = peer;
			this.view.setMessageInputEnabled(peer.getSession().getState() == P2PSession.State.CONNECTED);
			this.view.setNick(peer.getNick());
			this.view.showChatPanel();
			this.displayActivePeerMessages();
			this.rows.get(peer.getId()).updateLastMessage(false);

			SwingUtilities.invokeLater(() -> {
				this.view.scrollMessagesToBottom();
			});
		}
	}

	@Override
	public void homeWindowDidEnterMessage(String text) {
		if(this.activePeer == null || this.activePeer.getSession().getState() != P2PSession.State.CONNECTED) {
			return;
		}

		final Message message = new TextMessage(this.activePeer, false, text);
		this.activePeer.getMessages().add(message);
		this.rows.get(this.activePeer.getId()).updateLastMessage(false);
		this.view.appendMessageToBottom(message);

		SwingUtilities.invokeLater(() -> {
			this.view.scrollMessagesToBottom();
		});	
		
		if(this.delegate != null) {
			this.delegate.homeControllerDidEnterMessage(this, message);
		}
	}

	@Override
	public void homeWindowDidClickPeerKeyButton() {
		if(this.activePeer != null) {
			new PublicKeyDialog(this.view, Language.DEFAULT.getString("public_key") + " " + this.activePeer.getNick(), this.activePeer.getPublicKeyAsString());
		}
	}

	@Override
	public void homeWindowDidClickDeletePeerButton() {
		if(this.activePeer != null) {
			final DeletePeerWindow deletePeerWindow = new DeletePeerWindow(this.activePeer.getNick());
			final HomeController sender = this;

			deletePeerWindow.setListener(new DeletePeerWindowListener(){
				@Override
				public void deletePeerWindowDidConfirm() {
					deletePeerWindow.setVisible(false);
					deletePeerWindow.dispose();
					
					final Peer peer = activePeer;
					final PeerRowView row = rows.remove(peer.getId());
					activePeer = null;
					view.showSettingsPanel();
					view.removePeerRow(row);

					if(delegate != null) {
						delegate.homeControllerDeletePeer(sender, peer);
					}
				}
			
				@Override
				public void deletePeerWindowDidCancel() {
					deletePeerWindow.setVisible(false);
					deletePeerWindow.dispose();
				}
			});
		}
	}

	@Override
	public void homeWindowDidClickImageButton() {
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Image file", "jpg", "jpeg", "png", "bmp"));
		fileChooser.setAcceptAllFileFilterUsed(false);
		final int result = fileChooser.showOpenDialog(this.view);

		if(result == JFileChooser.APPROVE_OPTION) {
			final File file = fileChooser.getSelectedFile();
			
			try {
				final BufferedImage image = scaleImageIfTooBig(ImageIO.read(file));
				final Message message = new ImageMessage(this.activePeer, false, image);
				this.activePeer.getMessages().add(message);
				this.rows.get(this.activePeer.getId()).updateLastMessage(false);
				this.view.appendMessageToBottom(message);
				
				SwingUtilities.invokeLater(() -> {
					this.view.scrollMessagesToBottom();
				});	
				
				if(this.delegate != null) {
					this.delegate.homeControllerDidEnterMessage(this, message);
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static BufferedImage scaleImageIfTooBig(BufferedImage image) {
		if(image.getWidth() <= App.MAX_UPLOAD_IMAGE_SIZE && image.getHeight() <= App.MAX_UPLOAD_IMAGE_SIZE) {
			return image;
		}

		final double width = image.getWidth();
		final double height = image.getHeight();
		final double ratio = width / height;
		double newWidth = 1;
		double newHeight = 1;

		if(width > height) {
			newWidth = App.MAX_UPLOAD_IMAGE_SIZE;
			newHeight = newWidth / ratio;
		}
		else {
			newHeight = App.MAX_UPLOAD_IMAGE_SIZE;
			newWidth = newHeight * ratio;
		}

		final BufferedImage newImage = new BufferedImage((int)newWidth, (int)newHeight, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = newImage.createGraphics();
		graphics.drawImage(image, 0, 0, (int)newWidth, (int)newHeight, null);
		graphics.dispose();
		return newImage;
	}
}
