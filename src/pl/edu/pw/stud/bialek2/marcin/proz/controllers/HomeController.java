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
import java.util.ArrayList;


public class HomeController implements HomeWindowListener {
	private HomeControllerListener listener;
	private HomeWindow view;
	private Chatroom activeChatroom;
	private Peer sender;

    public HomeController(HomeWindow view, Peer sender, ArrayList<Chatroom> chatrooms) {
		this.view = view;
		this.sender = sender;
		view.setListener(this);
		
		for(Chatroom chatroom : chatrooms) {
			view.appendChatroom(chatroom);
		}
	}
	
	public void setListener(HomeControllerListener listener) {
		this.listener = listener;
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
		if(this.listener != null) {
			this.listener.homeControllerDidExit(this);
		}
	}

	@Override
	public void homeWindowDidClickAddChatroomButton() {
		final AddChatroomWindow addChatroomWindow = new AddChatroomWindow();
		final HomeController sender = this;
	
		addChatroomWindow.setListener(new AddChatroomWindowListener() {
			@Override
			public void addChatroomWindowDidCreateChatroom(Chatroom chatroom) {
				if(listener != null) {
					addChatroomWindow.setVisible(false);
					addChatroomWindow.dispose();
					view.appendChatroom(chatroom);
					listener.homeControllerDidCreateChatroom(sender, chatroom);
				}
			}
		});
	}

	@Override
	public void homeWindowDidChangeChatroom(Chatroom chatroom) {
		this.activeChatroom = chatroom;

		if(chatroom.getMessages() != null) {
			this.displayActiveChatroomMessages();
		}
		else if(this.listener != null) {
			this.listener.homeControllerLoadMessages(this, chatroom);
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
		this.activeChatroom.getMessages().add(message);
		
		if(this.listener != null) {
			this.listener.homeControllerDidEnterMessage(this, message);
		}
	}
}
