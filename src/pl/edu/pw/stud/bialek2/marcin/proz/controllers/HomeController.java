package pl.edu.pw.stud.bialek2.marcin.proz.controllers;

import pl.edu.pw.stud.bialek2.marcin.proz.models.Chatroom;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Message;
import pl.edu.pw.stud.bialek2.marcin.proz.models.Peer;
import pl.edu.pw.stud.bialek2.marcin.proz.models.TextMessage;
import pl.edu.pw.stud.bialek2.marcin.proz.views.addchat.AddChatroomWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.addchat.AddChatroomWindowListener;
import pl.edu.pw.stud.bialek2.marcin.proz.views.home.HomeWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.home.HomeWindowListener;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class HomeController implements HomeWindowListener {
	private HomeControllerDelegate delegate;
	private HomeWindow view;
	private Chatroom activeChatroom;
	private Peer sender;

    public HomeController(HomeWindow view, Peer sender, HashMap<Integer, Chatroom> chatrooms) {
		this.view = view;
		this.sender = sender;
		view.setListener(this);
		
		for(Map.Entry<Integer, Chatroom> entry : chatrooms.entrySet()) {
			view.appendChatroom(entry.getValue());
		}
	}
	
	public void setDelegate(HomeControllerDelegate delegate) {
		this.delegate = delegate;
	}

	private void displayActiveChatroomMessages() {
		this.view.clearMessages();

		for(Message message : this.activeChatroom.getMessages()) {
			if(message.getPeer().getId() == this.sender.getId()) {
				message.setIsSentByUser(true);
			}

			this.view.appendMessageToBottom(message);
		}
	}

	public void loadedMessagesFor(Chatroom chatroom) {
		if(chatroom == this.activeChatroom) {
			this.displayActiveChatroomMessages();
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
	public void homeWindowDidClickAddChatroomButton() {
		final AddChatroomWindow addChatroomWindow = new AddChatroomWindow();
		final HomeController sender = this;
	
		addChatroomWindow.setListener(new AddChatroomWindowListener() {
			@Override
			public void addChatroomWindowDidCreateChatroom(Chatroom chatroom) {
				if(delegate != null) {
					addChatroomWindow.setVisible(false);
					addChatroomWindow.dispose();
					view.appendChatroom(chatroom);
					delegate.homeControllerDidCreateChatroom(sender, chatroom);
				}
			}

			@Override
			public void addChatroomWindowDidAddChatroom(UUID uuid, String address, int port) {
				if(delegate != null) {
					addChatroomWindow.setVisible(false);
					addChatroomWindow.dispose();
					delegate.homeControllerDidCreateChatroom(sender, chatroom);
				}
			}
		});
	}

	@Override
	public void homeWindowDidClickSettingsButton() {
		this.view.showSettingsPanel();
	}

	@Override
	public void homeWindowDidChangeChatroom(Chatroom chatroom) {
		this.activeChatroom = chatroom;
		this.view.showChatPanel();

		if(chatroom.getMessages() != null) {
			this.displayActiveChatroomMessages();
			this.view.scrollMessagesToBottom();
		}
		else if(this.delegate != null) {
			this.delegate.homeControllerLoadMessages(this, chatroom);
		}
	}

	@Override
	public void homeWindowDidEnterMessage(String text) {
		if(this.activeChatroom == null) {
			return;
		}

		final Message message = new TextMessage(this.activeChatroom, this.sender, text);
		message.setIsSentByUser(true);
		this.view.appendMessageToBottom(message);
		this.view.scrollMessagesToBottom();
		this.activeChatroom.getMessages().add(message);
		
		if(this.delegate != null) {
			this.delegate.homeControllerDidEnterMessage(this, message);
		}
	}
}
