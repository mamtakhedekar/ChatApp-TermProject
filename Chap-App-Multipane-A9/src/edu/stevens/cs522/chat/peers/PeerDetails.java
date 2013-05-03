package edu.stevens.cs522.chat.peers;

import edu.stevens.cs522.chat.location.SourceCoordinates;
import edu.stevens.cs522.chat.messages.MessageInfo.MessageType;

public class PeerDetails {
	
	private SourceCoordinates sourceCorCoordinates;
	private String name;

	public static enum PeerState {
		ACTIVE("active"),
		INACTIVE("inactive");
		
		private String value;
		
		public String value() {
			return value;
		}
		
		public static PeerState fromString(String v) {
			for (PeerState mtype : values()) {
				if (mtype.value.equals(v)) {
					return mtype;
				}
			}
			return null;
		}
		
		private PeerState(String v) {
			value = v;
		}
	}
	
	private PeerState peerState;
		
	public PeerState getPeerType() {
		return peerState;
	}

	public void setPeerState(PeerState state)
	{
		peerState = state;
	}
	
	public PeerDetails(SourceCoordinates src, String peerName)
	{
		sourceCorCoordinates = src;
		name = peerName;
	}
	
	SourceCoordinates getPeerLocation()
	{
		return sourceCorCoordinates;
	}
	
	String getPeerName()
	{
		return name;
	}
}
