package edu.stevens.cs522.chat.messages;

import java.util.ArrayList;
import java.util.List;

public class GlobalCheckOutMessage extends MessageInfo {

	private static final long serialVersionUID = 4697741663698588150L;

	private List<String> tags = new ArrayList<String>();
	
	public List<String> getTags() {
		return tags;
	}

	public GlobalCheckOutMessage() {
		super(MessageType.GLOBAL_CHECKIN);
	}

	public GlobalCheckOutMessage(MessageType type) {
		super(type);
	}

}
