package edu.stevens.cs522.chat.messages;

import java.util.List;

import edu.stevens.cs522.chat.location.SourceCoordinates;

/*
 * This is the type of message sent from a chatroom client to the server,
 * which then distributes the text message as a BroadcastMessage.
 */
public class LocalPeersMessage extends MessageInfo {
	
	private static final long serialVersionUID = 8807287826918756466L;

	/*
	 * List of peers in vicinity of this peer (in meters).
	 */
	private double radius;  
	
	private List<SourceCoordinates> peers;
	
	public double getRadius() {
		return radius;
	}
	
	public List<SourceCoordinates> getPeers() {
		return peers;
	}
	
	public LocalPeersMessage(double radius) {
		super(MessageType.LOCAL_PEERS);
		this.radius = radius;
	}


}
