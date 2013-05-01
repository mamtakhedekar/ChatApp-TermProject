package edu.stevens.cs522.chat.messages;

public class NotifyMessage extends MessageInfo {
	
	/*
	 * Notification of a new chatroom that is available.
	 * Get the network coordinates from SourceCoordinates.
	 */

	private static final long serialVersionUID = 4993895804230554980L;

	private String chatroomOwner;
	
	private String chatroomName;
	
	public String getChatroomOwner() {
		return chatroomOwner;
	}

	public void setChatroomOwner(String chatroomOwner) {
		this.chatroomOwner = chatroomOwner;
	}

	public String getChatroomName() {
		return chatroomName;
	}

	public void setChatroomName(String chatroomName) {
		this.chatroomName = chatroomName;
	}

	/*
	 * For a notification in the UI.
	 */
	private String text;
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public NotifyMessage() {
		super(MessageType.CHATROOM_NOTIFY);
	}
}
