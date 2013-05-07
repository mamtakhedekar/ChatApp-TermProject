/*********************************************************************

    Information about a message that has been received.
    
    Copyright (c) 2012 Stevens Institute of Technology

 **********************************************************************/

package edu.stevens.cs522.chat.messages;

import edu.stevens.cs522.chat.location.DestCoordinates;
import edu.stevens.cs522.chat.location.SourceCoordinates;

public class MessageInfo implements MessageInfoInterface {

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

	/*
	private Coordinates coordinates;
	
	public Coordinates getCoordinates() {
		return coordinates;
	}
	*/
	private SourceCoordinates sourcecoordinates;
	private DestCoordinates destinationcoordinates;
	
	
	public DestCoordinates getDestCoordinates() {
		return (DestCoordinates)destinationcoordinates;
	}
	
	public SourceCoordinates getSourceCoordinates() {
		return (SourceCoordinates)sourcecoordinates;
	}
	
	
	public void setDestCoordinates(DestCoordinates coordinates) {
		this.destinationcoordinates = coordinates;
	}

	public void setSourceCoordinates(SourceCoordinates coordinates) {
		this.sourcecoordinates = coordinates;
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
