package edu.stevens.cs522.chat.messages;

import java.net.InetAddress;

import android.content.Context;

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
	
	public static void sendCheckInMessage(Context context, IChatService service, 
			LocalCheckInMessage checkInMessage )
	{
		service.send(checkInMessage);
	}
	
	public static void sendCheckOutMessage(Context context, IChatService service, 
			LocalCheckOutMessage checkOutMessage )
	{
		service.send(checkOutMessage);
	}
}
