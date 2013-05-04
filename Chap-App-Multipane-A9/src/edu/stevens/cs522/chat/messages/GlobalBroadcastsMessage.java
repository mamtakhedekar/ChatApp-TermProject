package edu.stevens.cs522.chat.messages;

import java.util.List;

/*
 * This is the type of message sent from a chatroom client to the server,
 * which then distributes the text message as a BroadcastMessage.
 */
public class GlobalBroadcastsMessage extends MessageInfo {
	
	private static final long serialVersionUID = 6014022116074281728L;

	/*
	 * List of peers in vicinity of this peer (in meters).
	 */
	private int seqNumber;  
	
	private List<BroadcastMessage> messages;
	
	public int getSeqNumber() {
		return seqNumber;
	}
	
	public List<BroadcastMessage> getMessages() {
		return messages;
	}
	
	public GlobalBroadcastsMessage(int seqNumber) {
		super(MessageType.GLOBAL_BROADCASTS);
		this.seqNumber = seqNumber;
	}


}
