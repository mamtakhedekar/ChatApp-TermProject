package edu.stevens.cs522.chat.messages;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.gson.Gson;

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
			MessageInfo message = (MessageInfo)contents.getSerializable(SEND_MESSAGE_KEY);
			/*
			 * Extract destination address information.
			 */
			InetAddress destAddr = message.getDestCoordinates().getAddress();
			int destPort = message.getDestCoordinates().getServicePort();
			//message.setAddress(null);
			/*
			 * Marshall the message to JSON data.
			 */
			byte[] sendData = gson.toJson(message).getBytes();
			/*
			 * Send the datagram packet.
			 */
			DatagramPacket p = new DatagramPacket(sendData, sendData.length,
					destAddr, destPort);
			appSocket.send(p);

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
	public void send(MessageInfo message) {
		Log.i(TAG, "Sending a message.");

		Bundle contents = new Bundle();
		contents.putSerializable(SEND_MESSAGE_KEY, message);

		Message messageToSend = new Message();
		messageToSend.setData(contents);

		sendHandler.sendMessage(messageToSend);
	}
}
