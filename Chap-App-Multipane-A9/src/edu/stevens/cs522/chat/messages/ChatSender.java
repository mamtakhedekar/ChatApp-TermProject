package edu.stevens.cs522.chat.messages;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ChatSender implements Handler.Callback{

	private Handler sendHandler;
	private final String SEND_MESSAGE_KEY = "message";
	private HandlerThread sender;
	private DatagramSocket appSocket;
	
	private String TAG;
	
	public ChatSender(DatagramSocket p_appSocket, String tag) {
		/*
		 * Start the background thread for handling message sends.
		 */
		appSocket = p_appSocket;
		TAG = tag;
		
		sender = new HandlerThread("edu.stevens.cs522.chat.ChatSender");
		sender.start();
		Looper looper = sender.getLooper();
		sendHandler = new Handler(looper, this);
	}
	/*
	 * Marshall and unmarshall messages as JSON using Gson.
	 */	
	private Gson gson = new Gson();
	
	@Override
	public boolean handleMessage(Message sendMsg) {
		Bundle contents = sendMsg.getData();

		try {
			/*
			 * Unmarshall the message from the handler message.
			 */
			MessageInfoInterface message = (MessageInfoInterface)contents.getSerializable(SEND_MESSAGE_KEY);
			/*
			List<MessageInfoInterface> list = new ArrayList<MessageInfoInterface>() ;
			MessageInfo msgInfo = new MessageInfo(message.getMessageType());
			
			list.add(msgInfo);
			list.add(message);
			
			String json_string = new Gson().toJson(list);
			*/
			/*
			 * Extract destination address information.
			 */
			InetAddress destAddr = message.getDestCoordinates().getAddress();
			int destPort = message.getDestCoordinates().getServicePort();
			//message.setAddress(null);
			/*
			 * Marshall the message to JSON data.
			 */
			byte[] sendData = null;
			switch(message.getMessageType())
			{
			case LOCAL_CHECKIN:
				sendData = gson.toJson(message, LocalCheckInMessage.class).getBytes();
				break;
			case LOCAL_CHECKOUT:
				sendData = gson.toJson(message, LocalCheckOutMessage.class).getBytes();
				break;
			case TEXT:
				sendData = gson.toJson(message, TextMessage.class).getBytes();
				break;
			case BROADCAST:
				sendData = gson.toJson(message, BroadcastMessage.class).getBytes();
			default:
				// Dont process
				break;
			}
			
			//byte[] sendData = json_string.getBytes();
			/*
			 * Send the datagram packet.
			 */
			if ( sendData != null )
			{
				DatagramPacket p = new DatagramPacket(sendData, sendData.length,
						destAddr, destPort);
				appSocket.send(p);
			}

		} catch (UnknownHostException e) {
			Log.e(TAG,
					"Unknown host: " + contents.getString(SEND_MESSAGE_KEY));
		} catch (IOException e) {
			Log.e(TAG, "Cannot send message: " + e);
		}
		return true;
	}

	/*
	 * This runs on the UI thread to send a message.
	 */
	public void send(MessageInfoInterface message) {
		Log.i(TAG, "Sending a message.");

		Bundle contents = new Bundle();
		contents.putSerializable(SEND_MESSAGE_KEY, message);

		Message messageToSend = new Message();
		messageToSend.setData(contents);

		sendHandler.sendMessage(messageToSend);
	}
}
