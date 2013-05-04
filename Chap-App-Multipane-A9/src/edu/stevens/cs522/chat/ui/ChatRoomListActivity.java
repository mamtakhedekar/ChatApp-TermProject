package edu.stevens.cs522.chat.ui;

import java.net.InetAddress;
import java.util.List;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.messages.ChatService;
import edu.stevens.cs522.chat.messages.IChatService;
import edu.stevens.cs522.chat.messages.MessageUtils;
import edu.stevens.cs522.chat.rooms.Chatrooms;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * An activity representing a list of ChatRooms. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ChatRoomDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ChatRoomListFragment} and the item details (if present) is a
 * {@link ChatRoomDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link IChatRoomManager} interface to listen for item
 * selections.
 */
public class ChatRoomListActivity extends Activity implements
		IChatRoomManager, ISendMessage {

	private final static String TAG = ChatRoomListActivity.class.getCanonicalName();
	
	//private ChatRoomListFragment fragment;
	
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean isTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatroom_list);
		
		
		/*ADDED 
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			//arguments.putString(ChatRoomListFragment..CHATROOM_ID_KEY, getIntent().getStringExtra(ChatRoomDetailFragment.CHATROOM_ID_KEY));
			//arguments.putString("", getIntent().getStringExtra(this.rooms));
			fragment = new ChatRoomListFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.add(R.id.chatroom_list, fragment).commit();
		}
		*/

		if (findViewById(R.id.chatroom_detail_container) != null) {
			Log.i(TAG, "Executing in two-pane mode.");
			
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			isTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.

			((ChatRoomListFragment) getFragmentManager()
					.findFragmentById(R.id.chatroom_list))
					.setActivateOnItemClick(true);
			
		} else {
			Log.i(TAG, "Executing in single-pane mode.");
		}

		/*
		 * TODO: Start the background service that will receive messages from
		 * peers, and bind to the service.
		 */
		
		Intent intent = new Intent(this, ChatService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		startService(intent);		

		/*
		 * End Todo
		 */
		
		rooms = Chatrooms.createChatrooms();
		
		//@TODO move this to the action bar
		rooms.checkAndAddRoom("PermitRoom");
		// To Do: If exposing deep links into your app, handle intents here.
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// TODO: Add menu for Register
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case (R.id.register_topic):
			Intent registrationIntent = new Intent(this, TopicRegistrationActivity.class);
			startActivity(registrationIntent);
			return true;
		}
		return false;
	}	

	private Chatrooms rooms;

	/**
	 * Callback method from {@link IChatRoomManager} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (isTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
/*			Bundle arguments = new Bundle();
			arguments.putString(ChatRoomDetailFragment.CHATROOM_ID_KEY, id);
			ChatRoomDetailFragment fragment = new ChatRoomDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.chatroom_detail_container, fragment).commit();*/
			Bundle arguments = new Bundle();
			arguments.putString(UserInputFragment.CHATROOM_ID_KEY, id);
			UserInputFragment fragment = new UserInputFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.replace(R.id.chatroom_detail_container, fragment).commit();			

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
/*			Intent detailIntent = new Intent(this, ChatRoomDetailActivity.class);
			detailIntent.putExtra(ChatRoomDetailFragment.CHATROOM_ID_KEY, id);
			startActivity(detailIntent);*/
			Intent detailIntent = new Intent(this, UserInputActivity.class);
			detailIntent.putExtra(UserInputFragment.CHATROOM_ID_KEY, id);
			startActivity(detailIntent);			
		}
	}
	
	/**
	 * Callback method from {@link IChatRoomManager} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public List<String> getRoomNames() {
		return rooms.getRoomNames();
	}
	
	
	/*
	 * Service binder.
	 */
	private IChatService service;

	/*
	 * TODO: Handle the connection with the service.
	 * 
	 * Handle ALL service connections here.
	 */
	
	private ServiceConnection mConnection = new ServiceConnection() {

	    public void onServiceConnected(ComponentName className,
	                IBinder binderService) {
	            // We've bound to LocalService, cast the IBinder and get LocalService instance
	        	service = ((ChatService.ChatBinder) binderService).getService();
	        }

	        public void onServiceDisconnected(ComponentName arg0) {
	        	service = null;
	        }
	    };	

	/*
	 * End Todo
	 */
	
	/*
	 * The detail fragment calls into the activity to invoke the 
	 * service operation for sending a message.
	 */
	@Override
	public void send(InetAddress addr, int port, String message, String longi, String lati, String uname) {
		Log.i(TAG, "Sending message on tablet.");
		MessageUtils.send(this, service, addr, port, message, longi, lati, uname);		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (isFinishing()) {
			/*
			 * TODO unbind the service.
			 */
			unbindService(mConnection);
			service = null;			

			/*
			 * End Todo
			 */
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    // Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);
	}
	
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	    // Always call the superclass so it can restore the view hierarchy
	    super.onRestoreInstanceState(savedInstanceState);
	}	
}
