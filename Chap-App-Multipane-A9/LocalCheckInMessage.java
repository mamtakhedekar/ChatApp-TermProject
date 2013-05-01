package edu.stevens.cs522.chat.messages;

public class LocalCheckInMessage extends GlobalCheckInMessage {
	
	private static final long serialVersionUID = 1266658479460266364L;
	
	private String chatroomName;
	
	public String getChatroomName() {
		return chatroomName;
	}

	public void setChatroomName(String chatroomName) {
		this.chatroomName = chatroomName;
	}

	public LocalCheckInMessage() {
		super(MessageType.LOCAL_CHECKIN);
	}

}
