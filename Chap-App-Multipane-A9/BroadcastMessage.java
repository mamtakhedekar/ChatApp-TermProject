package edu.stevens.cs522.chat.messages;

import java.util.ArrayList;
import java.util.List;

/*
 * A text message forwarded to clients of a chatroom.  The server adds
 * a sequence number and the identity of the original sender.
 */
public class BroadcastMessage extends MessageInfo {
	
	private static final long serialVersionUID = 2365298980705041903L;

	private String chatSender;
	
	private String chatroomName;
	
	private String chatroomOwner;
	
	private int seqNumber;
	
	private List<String> tags;
	
	private String text;
	
	public String getChatSender() {
		return chatSender;
	}

	public String getChatroomName() {
		return chatroomName;
	}

	public String getChatroomOwner() {
		return chatroomOwner;
	}

	public int getSeqNumber() {
		return seqNumber;
	}

	public String getText() {
		return text;
	}
	
	public List<String> getTags() {
		return tags;
	}
	
	public String getTag() {
		/*
		 * For the case where we only implement support for one tag.
		 */
		if (tags.isEmpty()) 
			return null;
		else
			return tags.get(0);
	}

	public BroadcastMessage(String chatSender, String chatroom, String owner, int seqNumber, String text) { 
		super(MessageType.BROADCAST);
		this.chatSender = chatSender;
		this.chatroomName = chatroom;
		this.chatroomOwner = owner;
		this.seqNumber = seqNumber;
		this.text = text;
		this.tags = new ArrayList<String>();
	}

	public BroadcastMessage(String chatSender, String chatroom, String owner, int seqNumber, String text, List<String> tags) { 
		this(chatSender, chatroom, owner, seqNumber, text);
		this.tags = tags;
	}

	public BroadcastMessage(String chatSender, String chatroom, String owner, int seqNumber, String text, String tag) { 
		this(chatSender, chatroom, owner, seqNumber, text);
		this.tags.add(tag);
	}

}
