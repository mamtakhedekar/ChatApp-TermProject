package edu.stevens.cs522.chat.messages;

import java.util.ArrayList;
import java.util.List;

/*
 * This is the type of message sent from a chatroom client to the server,
 * which then distributes the text message as a BroadcastMessage.
 */
public class TextMessage extends MessageInfo {
	
	private static final long serialVersionUID = 1306234108724442296L;

	private String chatroomName;
	
	private String chatroomOwner;
	
	private List<String> tags;
	
	private String text;
	
	public String getChatroomName() {
		return chatroomName;
	}

	public String getChatroomOwner() {
		return chatroomOwner;
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

	public TextMessage(String chatroom, String owner, String text) { 
		super(MessageType.TEXT);
		this.chatroomName = chatroom;
		this.chatroomOwner = owner;
		this.text = text;
		this.tags = new ArrayList<String>();
	}

	public TextMessage(String chatSender, String chatroom, String owner, int seqNumber, String text, String tag) { 
		this(chatroom, owner, text);
		this.tags.add(tag);
	}

}
