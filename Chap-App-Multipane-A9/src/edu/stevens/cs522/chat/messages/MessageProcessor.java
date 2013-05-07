package edu.stevens.cs522.chat.messages;

import java.net.InetAddress;
import java.util.List;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
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

		Cursor c = cr.query(ChatContent.Chatrooms.CONTENT_URI, null, where,
				selectionArgs, null);
		if (!c.moveToFirst()) {
			cr.insert(ChatContent.Chatrooms.CONTENT_URI, values);
		
		c = cr.query(ChatContent.Chatrooms.CONTENT_URI, null, where,
					selectionArgs, null);
			
		} 
		
		List<String> peers = ChatContent.Chatrooms.getSubscribers(c);
		
		for (String string : peers) {
			if (string == checkInMsg.getName())
			{
				return;
			}
		}
		
		ContentValues values1 = new ContentValues();
		values1.put(ChatContent.Chatrooms._ID, ChatContent.Chatrooms.getChatroomId(c));
		
		peers.clear();
		//@TODO Add to the peers list
		peers.add(checkInMsg.getName());
		ChatContent.Chatrooms.putSubscribers(values1, peers);
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
		
		addReceivedMessage(srv, bcastMsg);
		addSender(srv, bcastMsg);	
	}
	
	public void addNewText(Service srv, MessageInfo msg) {
		TextMessage textMsg = (TextMessage)msg;
		pushOutMessageToPeers(srv, textMsg);
	}
	
	private void pushOutMessageToPeers(Service srv, TextMessage msg)
	{
		ContentValues values = new ContentValues();
		values.put(ChatContent.Chatrooms.NAME, msg.getChatroomName());
		values.put(ChatContent.Chatrooms.OWNER, "SELF");
		
		ContentResolver cr = srv.getContentResolver();
		String[] projection = {};//new String[] { ChatContent.Peers.NAME };
		String where = ChatContent.Chatrooms.NAME + "= ?";
		String[] selectionArgs = new String[] { msg.getChatroomName() };

		Cursor c = cr.query(ChatContent.Chatrooms.CONTENT_URI, projection, where,
				selectionArgs, null);
		
		if (c.moveToFirst()) {
			List<String> peers = ChatContent.Chatrooms.getSubscribers(c);
			
			for (String peerName : peers) {

				values.put(ChatContent.Peers.NAME, peerName);
				
				String[] peer_projection = new String[] { ChatContent.Peers.NAME };
				String peer_where = ChatContent.Peers.NAME + "= ?";
				String[] peer_selectionArgs = new String[] { peerName };

				Cursor peer_c = cr.query(ChatContent.Peers.CONTENT_URI, peer_projection, peer_where,
						peer_selectionArgs, null);
				
				if (peer_c .moveToFirst())
				{
					String host = ChatContent.Peers.getHost(peer_c);
					int port = ChatContent.Peers.getPort(peer_c);
					
					BroadcastMessage bcastMsg = 
							new BroadcastMessage(
									msg.getName(), 
									msg.getChatroomName(), 
									msg.getChatroomOwner(), 
									1, msg.getText());
					
					try {
						bcastMsg.getDestCoordinates().setAddress(InetAddress.getByName(host));
						bcastMsg.getDestCoordinates().setServicePort(port);
						((ChatService)srv).send(bcastMsg);
					} catch (Exception ex) {
						Log.e("S", ex.getStackTrace().toString() + ex.getMessage());
					}
				}
			}
		} 
	}
	
	private void addReceivedMessage(Service srv, BroadcastMessage msg) {
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
	
	private void addSender(Service srv, BroadcastMessage msg) {

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
