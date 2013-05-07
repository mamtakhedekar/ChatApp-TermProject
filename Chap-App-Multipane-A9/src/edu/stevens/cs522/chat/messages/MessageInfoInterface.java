package edu.stevens.cs522.chat.messages;

import java.io.Serializable;

import edu.stevens.cs522.chat.location.DestCoordinates;
import edu.stevens.cs522.chat.location.SourceCoordinates;
import edu.stevens.cs522.chat.messages.MessageInfo.MessageType;

public interface MessageInfoInterface extends Serializable {
	
	public MessageType getMessageType();
	public DestCoordinates getDestCoordinates() ;
	
	public SourceCoordinates getSourceCoordinates();
	
	
	public void setDestCoordinates(DestCoordinates coordinates);

	public void setSourceCoordinates(SourceCoordinates coordinates);
	public void setName(String pname );

	public String getName();

}
