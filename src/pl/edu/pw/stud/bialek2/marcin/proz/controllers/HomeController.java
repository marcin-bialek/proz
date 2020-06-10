package pl.edu.pw.stud.bialek2.marcin.proz.controllers;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
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
import pl.edu.pw.stud.bialek2.marcin.proz.views.HomeWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.HomeWindowListener;
import pl.edu.pw.stud.bialek2.marcin.proz.views.PeerRowView;

import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;


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
		// System.out.println("[newMessage] message: " + message);
		// System.out.println("[newMessage] peer: " + message.getPeer());

		final PeerRowView row = this.rows.get(message.getPeer().getId());
		// System.out.println("[newMessage] row: " + row);
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
			if(status == PeerStatus.OFFLINE) {
				this.view.setMessageInputEnabled(false);
			}
			else if(status == PeerStatus.ONLINE) {
				this.view.setMessageInputEnabled(true);
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
		if(this.activePeer == null) {
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
}
