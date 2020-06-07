package pl.edu.pw.stud.bialek2.marcin.proz.controllers;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;
import pl.edu.pw.stud.bialek2.marcin.proz.models.PeerStatus;
import pl.edu.pw.stud.bialek2.marcin.proz.models.TextMessage;
import pl.edu.pw.stud.bialek2.marcin.proz.views.AddPeerWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.AddPeerWindowListener;
import pl.edu.pw.stud.bialek2.marcin.proz.views.home.HomeWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.home.HomeWindowListener;
import pl.edu.pw.stud.bialek2.marcin.proz.views.home.PeerRowView;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;


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
		System.out.println("[newMessage] message: " + message);
		System.out.println("[newMessage] peer: " + message.getPeer());

		final PeerRowView row = this.rows.get(message.getPeer().getId());
		System.out.println("[newMessage] row: " + row);
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

		if(status == PeerStatus.OFFLINE) {
			row.setPeerOffline();
		}
		else if(status == PeerStatus.ONLINE) {
			row.setPeerOnline();
		}
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

	@Override
	public void homeWindowDidResize(Dimension windowSize) {

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
			@Override
			public void addPeerWindowDidAddPeer(String address, int port) {
				addPeerWindow.setVisible(false);
				addPeerWindow.dispose();
				final Peer peer = new Peer(address, port);

				if(delegate != null) {
					delegate.homeControllerDidAddPeer(sender, peer);
				}
			}
		});
	}

	@Override
	public void homeWindowDidClickSettingsButton() {
		this.view.showSettingsPanel();
	}

	@Override
	public void homeWindowDidChangePeer(Peer peer) {
		this.activePeer = peer;
		this.view.showChatPanel();
		this.displayActivePeerMessages();
		this.view.scrollMessagesToBottom();
		this.rows.get(peer.getId()).updateLastMessage(false);
	}

	@Override
	public void homeWindowDidEnterMessage(String text) {
		if(this.activePeer == null) {
			return;
		}

		final Message message = new TextMessage(this.activePeer, false, text);
		this.view.appendMessageToBottom(message);
		this.view.scrollMessagesToBottom();
		this.activePeer.getMessages().add(message);
		this.rows.get(this.activePeer.getId()).updateLastMessage(false);
		
		if(this.delegate != null) {
			this.delegate.homeControllerDidEnterMessage(this, message);
		}
	}
}
