package edu.stevens.cs522.chat.messages;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import edu.stevens.cs522.chat.location.DestCoordinates;

public class MessageUtils {

	/*
	 * This operation is called by the parent activity of a fragment (via the
	 * sender callback) in response to a UI prompt to send a message.
	 */
	public static void send(Context context, IChatService service,
			InetAddress addr, int port, String message, String longi, String lati, String uname) {

		String sender = uname;							//context.getString(R.string.user_name);

		double longitude = Double.parseDouble(longi);	//context.getString(R.string.longitude));

		double latitude = Double.parseDouble(lati);			//context.getString(R.string.latitude));

		//BroadcastMessage msg = new BroadcastMessage(sender, addr, port, longitude,
		//		latitude, message);

		//service.send(msg);
	}
	
	public static void sendTextMsg(Context context, IChatService service,
			String ipAddr, int port, String roomName, String userName, String textMessage) 
	{
		TextMessage txtMsg = new TextMessage(roomName, "", textMessage);
		txtMsg.setName(userName);
		DestCoordinates coordinates = new DestCoordinates();
		try {
			coordinates.setAddress(InetAddress.getByName(ipAddr));
			coordinates.setServicePort(port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		txtMsg.setDestCoordinates(coordinates);
		sendMessage(context, service, txtMsg);
	}

	public static void sendLogInMessageToRoom( Context context, IChatService service,
			String ipAddr, int port, String roomName, String userName)
	{
		LocalCheckInMessage checkInMsg = new LocalCheckInMessage();
		checkInMsg.setChatroomName(roomName);
		checkInMsg.setName(userName);
		DestCoordinates coordinates = new DestCoordinates();
		try {
			coordinates.setAddress(InetAddress.getByName(ipAddr));
			coordinates.setServicePort(port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		checkInMsg.setDestCoordinates(coordinates);
		sendMessage(context, service, checkInMsg);
	}
		
	public static void sendLogOutMessageToRoom( Context context, IChatService service,
			String ipAddr, int port, String roomName, String userName)
	{
		LocalCheckOutMessage checkOutMsg = new LocalCheckOutMessage();
		checkOutMsg.setChatroomName(roomName);
		checkOutMsg.setName(userName);
		DestCoordinates coordinates = new DestCoordinates();
		try {
			coordinates.setAddress(InetAddress.getByName(ipAddr));
			coordinates.setServicePort(port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		checkOutMsg.setDestCoordinates(coordinates);
		sendMessage(context, service, checkOutMsg);
	}
	
	
	private static void sendMessage(Context context, IChatService service, 
			MessageInfo msg )
	{
		service.send(msg);
	}
	
}
