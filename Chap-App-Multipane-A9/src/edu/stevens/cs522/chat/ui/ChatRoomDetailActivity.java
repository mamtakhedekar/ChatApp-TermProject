package edu.stevens.cs522.chat.ui;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.messages.ChatService;
import edu.stevens.cs522.chat.messages.IChatService;
import edu.stevens.cs522.chat.messages.MessageUtils;


/**
 * An activity representing a single ChatRoom detail screen. This activity is
 * only used on handset devices. On tablet-size devices, item details are
 * presented side-by-side with a list of items in a {@link ChatRoomListActivity}
 * .
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link ChatRoomDetailFragment}.
 */
public class ChatRoomDetailActivity extends Activity implements ISendMessage {
	
	private static String TAG = ChatRoomDetailActivity.class.getCanonicalName();
	
	static final private int REGISTER_REQUEST = 1;	
	
	private ChatRoomDetailFragment fragment;
	Receiver updater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatroom_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Resources res = getResources();
			Bundle arguments = new Bundle();
			arguments.putString(ChatRoomDetailFragment.CHATROOM_ID_KEY, getIntent()
					.getStringExtra(ChatRoomDetailFragment.CHATROOM_ID_KEY));
			arguments.putString(res.getString(R.string.UNAME), getIntent()
					.getStringExtra(res.getString(R.string.UNAME)));
			arguments.putString(res.getString(R.string.LATITUDE), getIntent()
					.getStringExtra(res.getString(R.string.LATITUDE)));
			arguments.putString(res.getString(R.string.LONGITUDE), getIntent()
					.getStringExtra(res.getString(R.string.LONGITUDE)));
			arguments.putString(res.getString(R.string.chatroom_ip), getIntent()
					.getStringExtra(res.getString(R.string.chatroom_ip)));
			arguments.putString(res.getString(R.string.chatroom_port), getIntent()
					.getStringExtra(res.getString(R.string.chatroom_port)));			
			fragment = new ChatRoomDetailFragment();
			fragment.setArguments(arguments);
			getFragmentManager().beginTransaction()
					.add(R.id.chatroom_detail_container, fragment).commit();
		}
	
		/*
		 * TODO: Bind to the background service that will receive messages from
		 * peers.  This keeps the service running even if parent activity is destroyed.
		 */
		Intent intent = new Intent(this, ChatService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		startService(intent);
	};

	
		/*
		 * End Todo
		 */

	
	
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
	 * End To do
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// TODO: Add menu for Register
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.detail_fragment, menu);		
		return true;
	}    
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
/*		case android.R.id.home:
            // This is called when the Home (Up) button is pressed
            // in the Action Bar.
            Intent parentActivityIntent = new Intent(this, ChatRoomListActivity.class);
            parentActivityIntent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(parentActivityIntent);
            finish();
            return true;*/
		case (R.id.register_topic):
			Intent registrationIntent = new Intent(this, TopicRegistrationActivity.class);
			startActivityForResult(registrationIntent, REGISTER_REQUEST);
			//startActivity(registrationIntent);
			return true;            
		}
		return super.onOptionsItemSelected(item);
	}
	
	/*
	 * TODO: Since the content provider for messages received is now updated on
	 * a background thread, it sends a broadcast to the UI to tell it to update
	 * the cursor. The UI should register a broadcast receiver that will change
	 * the cursor for the messages adapter.  Call into the child fragment, which
	 * has access to the UI, for the update.  Check that the fragment is there 
	 * with the logic:
	 * 
	 * if (findViewById(R.id.chatroom_detail_container) != null) ...
	 */
	public class Receiver extends BroadcastReceiver {
		
	public Receiver()
	{
		Log.i("Receiver", "Created BroadcastReceiver" );
	}
	
	@Override 
	public void onReceive(Context context, Intent intent) {
	      // react to the event
		String action = intent.getAction();
		if(action.equalsIgnoreCase(ChatService.NEW_MESSAGE_BROADCAST)){  
		//messageAdapter.changeCursor(makeMessageCursor());
		//LoaderManager lm = getLoaderManager();
		//lm.restartLoader(LOADER_ID, null, mCallbacks);	    	   
	       }
	   }
	}	
	
	@Override
	protected void onPause() {
		if (findViewById(R.id.chatroom_detail_container) != null)
		{
			//ChatRoomDetailFragment fragment1 = (ChatRoomDetailFragment) getFragmentManager().findFragmentById(R.id.chatroom_detail_container);
			unregisterReceiver(this.updater);
			super.onPause();
		}
	}
	 
	@Override
	protected void onResume() {
		if (findViewById(R.id.chatroom_detail_container) != null)
		{
			//fragment.createNewUpdater();
			this.updater = new Receiver();
			registerReceiver(
				//fragment.getUpdater(),
				this.updater,
				new IntentFilter(ChatService.NEW_MESSAGE_BROADCAST));
			super.onResume();
		}
	}	

	/*
	 * End Todo
	 */

	/*
	 * The detail fragment calls into the activity to invoke the 
	 * service operation for sending a message.
	 */
	@Override
	public void send(InetAddress addr, int port, String message, String longi, String lati, String uname, String chatroom_name) {
		Log.i(TAG, "Sending message on phone.");
		MessageUtils.send(this, service, addr, port, message, longi, lati, uname);
		try {
			MessageUtils.sendTextMsg(this, service, addr, port, chatroom_name, uname, message, longi, lati);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

}
