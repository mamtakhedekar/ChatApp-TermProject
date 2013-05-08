package edu.stevens.cs522.chat.messages;

import java.io.IOException;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import edu.stevens.cs522.chat.messages.MessageInfo.MessageType;
import edu.stevens.cs522.chat.ui.ChatRoomDetailActivity;

/*
 * The description of the logic that is performed on a background thread.
 */
class ReceiveMessageTask extends AsyncTask<Void, MessageInfo, Void> {
	
	/**
	 * This is the background task for receiving chat messages.
	 */
		
	private final static String TAG = ReceiveMessageTask.class.getCanonicalName();
	
	private final ChatService chatService;
	private MessageProcessor processor;
	
	private ChatService getService() {
		return chatService;
	}

	/**
	 * @param chatService
	 */
	ReceiveMessageTask(ChatService chatService) {
		this.chatService = chatService;
		processor = new MessageProcessor();
	}

	@Override
	protected Void doInBackground(Void... params) {

		/*
		 * Main background loop: receiving and saving messages.
		 * "publishProgress" calls back to the UI loop to notify the user
		 * when a message is received.
		 */

		try {
			while (true) {
				while (true) {
					MessageInfoInterface msg = getService().nextMessage();
					ProcessReceivedMessage(msg);
					//publishProgress(msg);
				}
			}
		} catch (IOException e) {
			Log.i(TAG, "Socket closed, shutting down background thread: " + e);
		}
		return ((Void) null);
	}
	
	public void ProcessReceivedMessage(MessageInfoInterface msg) {
		
		switch(msg.getMessageType())
		{
		case LOCAL_CHECKIN:
			processor.checkIn(chatService, (LocalCheckInMessage)msg);
			break;
		case LOCAL_CHECKOUT:
			processor.checkOut(chatService,(LocalCheckOutMessage) msg);
			break;
		case TEXT:
			processor.addNewText(chatService, (TextMessage)msg);
			break;
		case BROADCAST:
			processor.addBroadcastMsg(chatService,(BroadcastMessage) msg);
			publishProgress((BroadcastMessage)msg);
			break;
		case LOCAL_PEERS:
			processor.sendLocalPeers((LocalPeersMessage)msg);
			break;
		default:
			// Dont process
			break;
		}
	}
		
	@Override
	protected void onProgressUpdate(MessageInfo... values) {
		
		if (values[0].getMessageType() == MessageType.TEXT)
		{
			TextMessage textMsg = (TextMessage)values[0];
			/*
			 * Logic for updating the Messages cursor is done on the UI thread.
			 */
			getService().sendBroadcast(msgUpdateBroadcast);
			/*
			 * Progress update for UI thread: The notification is given a
			 * "pending intent," so that if the user selects the notification,
			 * that pending intent is used to launch the main UI (ChatApp).
			 */
			String svcName = Context.NOTIFICATION_SERVICE;
			NotificationManager notificationManager;
			notificationManager = (NotificationManager) this.chatService.getSystemService(svcName);
	
			Context context = this.chatService.getApplicationContext();
			String expandedText = textMsg.getText();
			String expandedTitle = "M:" + textMsg.getName();
			Intent startActivityIntent = new Intent(this.chatService,
					ChatRoomDetailActivity.class);
			PendingIntent launchIntent = PendingIntent.getActivity(context, 0,
					startActivityIntent, 0);
	
			this.chatService.newMessageNotification.setLatestEventInfo(context, expandedTitle,
					expandedText, launchIntent);
			this.chatService.newMessageNotification.when = java.lang.System.currentTimeMillis();
	
			notificationManager.notify(ChatService.NOTIFICATION_ID, this.chatService.newMessageNotification);
	
			Toast.makeText(context, expandedTitle, Toast.LENGTH_SHORT).show();
		}
	}
	
	private Intent msgUpdateBroadcast = new Intent(IChatService.NEW_MESSAGE_BROADCAST);

	@Override
	protected void onPostExecute(Void result) {
		/*
		 * The background thread is stopped by closing the socket, which is done
		 * in the service, so no point in stopping the service here.  The backbround
		 * thread would shut down the service if the background thread decided when to
		 * terminate.
		 */
//		this.chatService.stopSelf();
	}
}