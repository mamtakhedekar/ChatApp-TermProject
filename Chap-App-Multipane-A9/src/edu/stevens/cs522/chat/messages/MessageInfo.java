/*********************************************************************

    Information about a message that has been received.
    
    Copyright (c) 2012 Stevens Institute of Technology

 **********************************************************************/

package edu.stevens.cs522.chat.messages;

import java.io.Serializable;

import edu.stevens.cs522.chat.location.Coordinates;
import edu.stevens.cs522.chat.location.DestCoordinates;
import edu.stevens.cs522.chat.location.SourceCoordinates;

public abstract class MessageInfo implements Serializable {

	private static final long serialVersionUID = -6050463241441669252L;
	
	public static enum MessageType {
		TEXT("text"),
		BROADCAST("broadcast"),
		CHATROOM_NOTIFY("notify"),
		LOCAL_CHECKIN("localCheckIn"),
		LOCAL_CHECKOUT("localCheckOut"),
		GLOBAL_CHECKIN("globalCheckIn"),
		GLOBAL_CHECKOUT("globalCheckOut"),
		LOCAL_PEERS("localPeers"),
		GLOBAL_BROADCASTS("globalBroadcasts");
		
		private String value;
		
		public String value() {
			return value;
		}
		
		public static MessageType fromString(String v) {
			for (MessageType mtype : values()) {
				if (mtype.value.equals(v)) {
					return mtype;
				}
			}
			return null;
		}
		
		private MessageType(String v) {
			value = v;
		}
	}
	
	private MessageType messageType;
	
	public MessageType getMessageType() {
		return messageType;
	}

	private Coordinates coordinates;
	
	public Coordinates getCoordinates() {
		return coordinates;
	}
	
	public DestCoordinates getDestCoordinates() {
		return (DestCoordinates)coordinates;
	}
	
	public SourceCoordinates getSourceCoordinates() {
		return (SourceCoordinates)coordinates;
	}

	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}
	
	public MessageInfo(MessageType messageType) {
		this.messageType = messageType;
	}

	private String name;
	
	public void setName(String pname )
	{
		name = pname;
	}

	public String getName()
	{
		return name;
	}
}
