/*********************************************************************

    Chat service: accept chat messages from other peers.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2012 Stevens Institute of Technology

 **********************************************************************/

package edu.stevens.cs522.chat.messages;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;
import android.util.JsonToken;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
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
	public void send(MessageInfoInterface message) {
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
	MessageInfoInterface nextMessage() throws IOException {
		byte[] receiveData = new byte[1024];

		Log.i(TAG, "Waiting for a message.");

		DatagramPacket receivePacket = new DatagramPacket(receiveData,
				receiveData.length);

		appSocket.receive(receivePacket);
		Log.i(TAG, "Received a packet.");

		InetAddress sourceIPAddress = receivePacket.getAddress();
		int sourcePort = receivePacket.getPort();
		Log.d(TAG, "Source IP Address: " + sourceIPAddress + ":" + sourcePort);
		

		JsonReader reader=new JsonReader(
				new StringReader(new String(receivePacket.getData())));
		reader.setLenient(true);
		
		//com.google.gson.stream.JsonToken token = reader.peek();
				
		//Type dataType = new TypeToken<MessageInfo>() {}.getType();
		MessageInfo message = getMessage(reader, new TypeToken<MessageInfo>() {});
		
		Log.d(TAG, "Received from " + message.getName());
		JsonReader reader1=new JsonReader(
				new StringReader(new String(receivePacket.getData())));
		reader1.setLenient(true);
		
		switch(message.getMessageType())
		{
		case LOCAL_CHECKIN:
			return getMessage(reader1, new TypeToken<LocalCheckInMessage>() {});

		case LOCAL_CHECKOUT:
			return getMessage(reader1, new TypeToken<LocalCheckOutMessage>() {});
		case TEXT:
			return getMessage(reader1, new TypeToken<TextMessage>() {});

		case BROADCAST:
			return getMessage(reader1, new TypeToken<BroadcastMessage>() {});
		default:
			// Dont process
			break;
		}

		return message;

	}

	public <T> T getMessage(JsonReader r, TypeToken<T> typeToken){
		  try {
		    return gson.fromJson(r, typeToken.getType());
		  } catch (final JsonSyntaxException e) {
		    Log.e(TAG, "Error in Json format" + e.getMessage());
		  } catch (final JsonParseException e) {
		    Log.e(TAG, "Error in parsing Json" + e.getMessage());
		  }
		  return null;
		}
	
	@Override
	public void onDestroy() {
		appSocket.close();
	}
}
