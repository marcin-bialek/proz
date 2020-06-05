package pl.edu.pw.stud.bialek2.marcin.proz.controllers;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;
import pl.edu.pw.stud.bialek2.marcin.proz.models.TextMessage;
import pl.edu.pw.stud.bialek2.marcin.proz.views.AddPeerWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.AddPeerWindowListener;
import pl.edu.pw.stud.bialek2.marcin.proz.views.home.HomeWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.home.HomeWindowListener;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class HomeController implements HomeWindowListener {
	private HomeControllerDelegate delegate;
	private HomeWindow view;
	private Peer activePeer;

    public HomeController(HomeWindow view, HashMap<Integer, Peer> peers) {
		this.view = view;
		view.setListener(this);
		
		for(Map.Entry<Integer, Peer> entry : peers.entrySet()) {
			view.appendPeer(entry.getValue());
		}
	}
	
	public void setDelegate(HomeControllerDelegate delegate) {
		this.delegate = delegate;
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
				final Peer peer = new Peer(null, address, port, null);

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

		if(peer.getMessages() != null) {
			this.displayActivePeerMessages();
			this.view.scrollMessagesToBottom();
		}
		else if(this.delegate != null) {
			this.delegate.homeControllerLoadMessages(this, peer);
		}
	}

	@Override
	public void homeWindowDidEnterMessage(String text) {
		if(this.activePeer == null) {
			return;
		}

		final Message message = new TextMessage(this.activePeer, true, text);
		this.view.appendMessageToBottom(message);
		this.view.scrollMessagesToBottom();
		this.activePeer.getMessages().add(message);
		
		if(this.delegate != null) {
			this.delegate.homeControllerDidEnterMessage(this, message);
		}
	}
}
