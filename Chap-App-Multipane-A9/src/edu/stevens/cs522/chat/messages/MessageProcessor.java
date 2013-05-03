package edu.stevens.cs522.chat.messages;

import java.util.List;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import edu.stevens.cs522.chat.location.SourceCoordinates;
import edu.stevens.cs522.chat.providers.ChatContent;

public class MessageProcessor {

	public void checkIn(Service srv, MessageInfo msg) {
		
		LocalCheckInMessage checkInMsg = (LocalCheckInMessage)msg;

		ContentValues values = new ContentValues();
		values.put(ChatContent.Chatrooms.NAME, checkInMsg.getChatroomName());
		values.put(ChatContent.Chatrooms.OWNER, "SELF");
		
		ContentResolver cr = srv.getContentResolver();
		

		String[] projection = new String[] { ChatContent.Peers.NAME };
		String where = ChatContent.Chatrooms.NAME + "= ?";
		String[] selectionArgs = new String[] { checkInMsg.getChatroomName() };

		Cursor c = cr.query(ChatContent.Chatrooms.CONTENT_URI, projection, where,
				selectionArgs, null);
		if (!c.moveToFirst()) {
			cr.insert(ChatContent.Chatrooms.CONTENT_URI, values);
		
		c = cr.query(ChatContent.Chatrooms.CONTENT_URI, projection, where,
					selectionArgs, null);
			
		} 
		
		List<String> peers = ChatContent.Chatrooms.getSubscribers(c);
		
		for (String string : peers) {
			if (string == checkInMsg.getName())
			{
				return;
			}
		}
		
		peers.add(checkInMsg.getName());
		ChatContent.Chatrooms.putSubscribers(values, peers);
	}

	public void checkOut(Service srv, MessageInfo msg) {
		
		LocalCheckOutMessage checkInMsg = (LocalCheckOutMessage)msg;

		ContentValues values = new ContentValues();
		values.put(ChatContent.Chatrooms.NAME, checkInMsg.getChatroomName());
		values.put(ChatContent.Chatrooms.OWNER, "SELF");
		
		ContentResolver cr = srv.getContentResolver();
		String[] projection = new String[] { ChatContent.Peers.NAME };
		String where = ChatContent.Chatrooms.NAME + "= ?";
		String[] selectionArgs = new String[] { checkInMsg.getChatroomName() };

		Cursor c = cr.query(ChatContent.Chatrooms.CONTENT_URI, projection, where,
				selectionArgs, null);
		if (!c.moveToFirst()) {
			cr.insert(ChatContent.Chatrooms.CONTENT_URI, values);
		
		c = cr.query(ChatContent.Chatrooms.CONTENT_URI, projection, where,
					selectionArgs, null);
			
		} 
		
		List<String> peers = ChatContent.Chatrooms.getSubscribers(c);
		
		for (String string : peers) {
			if (string == checkInMsg.getName())
			{
				peers.remove(string);
				ChatContent.Chatrooms.putSubscribers(values, peers);
			}
		}
	}
	
	public void addBroadcastMsg(Service srv, MessageInfo msg) {
		BroadcastMessage bcastMsg = (BroadcastMessage)msg;
		
		//addReceivedMessage(srv, msg);
		
	}
	
	public void addNewText(Service srv, MessageInfo msg) {
		TextMessage textMsg = (TextMessage)msg;

		addReceivedMessage(srv, textMsg);
		addSender(srv, textMsg);
	}
	
	private void addReceivedMessage(Service srv, TextMessage msg) {
		/*
		 * Add sender and message to the content provider for received messages.
		 */
		ContentValues values = new ContentValues();
		values.put(ChatContent.Messages.SENDER, msg.getName());
		values.put(ChatContent.Messages.MESSAGE, msg.getText());
		values.put(ChatContent.Messages.CHATROOM, msg.getChatroomName());
		values.put(ChatContent.Messages.OWNER, msg.getChatroomOwner());
		
		ContentResolver cr = srv.getContentResolver();
		cr.insert(ChatContent.Messages.CONTENT_URI, values);
		
	}

	public void sendLocalPeers(MessageInfo msg) {
		LocalPeersMessage peersMsg = (LocalPeersMessage)msg;
	}
	
	private void addSender(Service srv, TextMessage msg) {

		/*
		 * Add sender information to content provider for peers
		 * information, if we have not already heard from them. If repeat
		 * message, update location information.
		 */
		ContentValues values = new ContentValues();
		values.put(ChatContent.Peers.NAME, msg.getName());
		values.put(ChatContent.Peers.HOST, msg.getSourceCoordinates().getAddress().getHostName());
		values.put(ChatContent.Peers.PORT, msg.getSourceCoordinates().getServicePort());
		values.put(ChatContent.Peers.LATITUDE, msg.getSourceCoordinates().getLatitude());
		values.put(ChatContent.Peers.LONGITUDE, msg.getSourceCoordinates().getLongitude());

		String[] projection = new String[] { ChatContent.Peers.NAME };
		String where = ChatContent.Peers.NAME + "= ?";
		String[] selectionArgs = new String[] { msg.getName() };

		ContentResolver cr = srv.getContentResolver();
		Cursor c = cr.query(ChatContent.Peers.CONTENT_URI, projection, where,
				selectionArgs, null);
		if (!c.moveToFirst()) {
			cr.insert(ChatContent.Peers.CONTENT_URI, values);
		} else {
			cr.update(ChatContent.Peers.CONTENT_URI, values, where,
					selectionArgs);
		}
	}


	
}
