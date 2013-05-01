package edu.stevens.cs522.chat.messages;


public class LocalCheckOutMessage extends MessageInfo {

	private static final long serialVersionUID = -5252726309572367457L;

	private String chatroomName;
	
	public String getChatroomName() {
		return chatroomName;
	}

	public void setChatroomName(String chatroomName) {
		this.chatroomName = chatroomName;
	}

	public LocalCheckOutMessage() {
		super(MessageType.LOCAL_CHECKOUT);
	}

}
