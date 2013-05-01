/*********************************************************************

    Chat service: accept chat messages from other peers.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2012 Stevens Institute of Technology

 **********************************************************************/

package edu.stevens.cs522.chat.messages;

import java.io.IOException;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import edu.stevens.cs522.chat.R;

public class ChatService extends Service implements IChatService{

	/*
	 * The chat service uses a background thread to receive messages sent by
	 * other devices, so the main UI thread does not block while waiting for a
	 * message. The content providers for messages and peer info are updated. A
	 * notification is placed in the UI, and may be used to bring the chat app
	 * to the foreground to see the messages that have been received.
	 */

	private static final String TAG = ChatService.class.getCanonicalName();
	
	public static final String NEW_MESSAGE_BROADCAST = "edu.stevens.cs522.chat.NewMessageBroadcast";

	Notification newMessageNotification;
	public static final int NOTIFICATION_ID = 1;

	/*
	 * Socket for communication with other instances.
	 */
	private DatagramSocket appSocket;
	
	/*
	 * Marshall and unmarshall messages as JSON using Gson.
	 */	
	private Gson gson = new Gson();

	public boolean isTablet() {
	    return (getApplicationContext().getResources().getConfiguration().screenLayout
	            & Configuration.SCREENLAYOUT_SIZE_MASK)
	            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
	@Override
	public void onCreate() {
		int icon = R.drawable.ic_launcher;
		String tickerText = "New message received";
		long when = System.currentTimeMillis();

		newMessageNotification = new Notification(icon, tickerText, when);

		try {
			appSocket = new DatagramSocket(
					Integer.parseInt(getString(R.string.app_port)));
			
				
			
		} catch (IOException e) {
			Log.e(TAG, "Cannot create socket." + e);
		}
	}

//	private Handler sendHandler;

	private final String SEND_MESSAGE_KEY = "message";

	/*
	 * This runs on the UI thread to send a message.
	 */
	public void send(MessageInfo message) {
		Log.i(TAG, "Sending a message.");

		sender.send(message);
	}

	
	//private ReceiveMessageTask recvTask;

	private ChatSender sender;
	private ReceiveMessageTask receiver;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		/*
		 * Start the background thread for handling message receives.
		 */
		Log.i(TAG,
				"Started Chat service, running task for receiving messages.");
		sender = new ChatSender(appSocket, TAG);
		receiver = new ReceiveMessageTask(this);
		receiver.execute((Void[]) null);
		/*boolean deviceIsTablet = isTablet();
		
		if (!deviceIsTablet)
		{
			if (findViewById(R.id.chatroom_detail_container) != null)
			{
				//ChatRoomDetailFragment fragment1 = (ChatRoomDetailFragment) getFragmentManager().findFragmentById(R.id.chatroom_detail_container);
				unregisterReceiver(this.updater);
				super.onPause();
			}
		}
*/		
		return START_STICKY;
	}

	/*
	 * TODO Provide a binder, since all socket-related operations are on the service.
	 */
	
	private final IBinder binder = new ChatBinder();	

	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	public class ChatBinder extends Binder {
		public IChatService getService() {
			return ChatService.this;
		}
	}	
	
	/*
	 * End Todo
	 */

	/*
	 * Executed on the background thread, to receive a chat message.
	 */
	MessageInfo nextMessage() throws IOException {
		byte[] receiveData = new byte[1024];

		Log.i(TAG, "Waiting for a message.");

		DatagramPacket receivePacket = new DatagramPacket(receiveData,
				receiveData.length);

		appSocket.receive(receivePacket);
		Log.i(TAG, "Received a packet.");

		InetAddress sourceIPAddress = receivePacket.getAddress();
		Log.d(TAG, "Source IP Address: " + sourceIPAddress);
		
		int sourcePort = receivePacket.getPort();
		JsonReader reader=new JsonReader(
				new StringReader(new String(receivePacket.getData())));
		reader.setLenient(true);
		MessageInfo message=gson.fromJson(reader,MessageInfo.class);
		
		//MessageInfo message = gson.fromJson(new String(receivePacket.getData()), MessageInfo.class);
		
		message.setAddress(sourceIPAddress);
		message.setPort(sourcePort);

		Log.d(TAG, "Received from " + message.getSender() + ": " + message.getMessage());
		return message;

	}

	@Override
	public void onDestroy() {
		appSocket.close();
	}

}
