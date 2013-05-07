package edu.stevens.cs522.chat.ui;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.providers.ChatContent;

/**
 * A fragment representing a single ChatRoom detail screen. This fragment is
 * either contained in a {@link ChatRoomListActivity} in two-pane mode (on
 * tablets) or a {@link ChatRoomDetailActivity} on handsets.
 */
public class ChatRoomDetailFragment extends Fragment 
implements LoaderManager.LoaderCallbacks<Cursor> {

	private final static String TAG = ChatRoomDetailFragment.class
			.getCanonicalName();
	
	private static final int URL_LOADER = 0;
	private static final int LOADER_ID = 0;
	private ListView msgList;
	//private Receiver updater = new Receiver();
	
	// The callbacks through which we will interact with the LoaderManager.
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;	

	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static String CHATROOM_ID_KEY = "chatroom_id";
	public static final String DEFAULT_CHATROOM_ID = "MAIN";
	static String longitude_val = "";
	static String latitude_val = "";
	static String user_name_val = "";
	LoaderManager lm;	

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ChatRoomDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * getArguments().getString(CHATROOM_ID_KEY) should return chatroom id.
		 */
		CHATROOM_ID_KEY = getArguments().getString("CHATROOM_ID_KEY");
		longitude_val = getArguments().getString(getResources().getString(R.string.LONGITUDE));
		latitude_val = getArguments().getString(getResources().getString(R.string.LATITUDE));
		user_name_val = getArguments().getString(getResources().getString(R.string.UNAME));

	}	

	private ISendMessage sender;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		/*
		 * TODO Bind parent activity callbacks here.
		 */
		try {
			sender = (ISendMessage) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement ISendMessage.");
		}
		/*
		 * End To do
		 */
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return loadContents(inflater, container, savedInstanceState);
	}

	
	public View loadContents(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_chatroom_detail,
				container, false);

		destHost = (EditText) rootView.findViewById(R.id.dest_text);

		destPort = (EditText) rootView.findViewById(R.id.port_text);

		message = (EditText) rootView.findViewById(R.id.message_text);

		/*
		 * TODO: Messages content provider should be linked to the listview
		 * named "msgList" in the UI: 1. Build a cursor that projects Messages
		 * content. 2. Use a SimpleCursorAdapter to adapt this cursor for
		 * msgList listview. 3. Use messages_row layout for the list of messages
		 */
		
		String[] to = new String[] { ChatContent.Messages.MESSAGE };
        int[] from = new int[] { R.id.messages_message };		
				
        mCallbacks = this;     
        lm = this.getLoaderManager();//getSupportLoaderManager();
        lm.initLoader(LOADER_ID, null, mCallbacks);
        
        this.messageAdapter = 
        		new SimpleCursorAdapter(getActivity(), R.layout.messages_row, null, to, from, 0 );
        // Bind to our new adapter.
        msgList = (ListView) rootView.findViewById(R.id.msgList);
        msgList.setAdapter(this.messageAdapter);		

		/*
		 * End Todo
		 */

		send = (Button) rootView.findViewById(R.id.send_button);
		send.setOnClickListener(sendListener);		

		return rootView;
	}
	
	/*
	 * Adapter for displaying received messages.
	 */
	SimpleCursorAdapter messageAdapter = null;

	/*
	 * Widgets for dest address, message text, send button.
	 */
	private EditText destHost;
	private EditText destPort;
	private EditText message;
	private Button send;
	

	/*
	 * On click listener for the send button
	 */
	private OnClickListener sendListener = new OnClickListener() {
		public void onClick(View v) {
			postMessage();
		}
	};
	
	/*
	 * Send the message in the msg EditText
	 */
	private void postMessage() {
		/*
		 * On the emulator, which does not support WIFI stack, we'll send to (an
		 * AVD alias for) the host loopback interface, with the server port on
		 * the host redirected to the server port on the server AVD.
		 */

		try {
			String targetAddrString = destHost.getText().toString();
			InetAddress targetAddr = InetAddress.getByName(targetAddrString);
			
			int targetPort = Integer.parseInt(destPort.getText().toString());

			String theNewMessage = message.getText().toString();

			if (sender != null) {
				sender.send(targetAddr, targetPort, theNewMessage, longitude_val, latitude_val, user_name_val, ChatRoomDetailFragment.CHATROOM_ID_KEY);
			} else {
				Log.e(TAG, "No sender callback registered.");
			}
			message.setText("");

		} catch (UnknownHostException e) {

		}
	}

	/*
	 * TODO: Update the messages listview. Use a loadermanager to refresh the
	 * cursor. Use this as the projection:
	 * 
	 * String[] projection = new String[] { ChatContent.Messages._ID,
	 * ChatContent.Messages.SENDER, ChatContent.Messages.MESSAGE };
	 * 
	 * Once the cursor is loaded, tell the adapter to change its cursor.
	 */
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		String[] projection = 
				new String[] { ChatContent.Messages._ID,
							   ChatContent.Messages.OWNER,
							   ChatContent.Messages.CHATROOM,
							   ChatContent.Messages.OWNER_MESSAGE_ID,
							   ChatContent.Messages.TAGS,
							   ChatContent.Messages.SENDER, 
							   ChatContent.Messages.MESSAGE };
		switch(LOADER_ID)
		{
		case URL_LOADER:
			return new CursorLoader(getActivity(), ChatContent.Messages.CONTENT_URI, projection, null, null, null);
		default:
			return null;		//An invalid id was passed in
		}
	}

	protected Cursor makeMessageCursor () {
		/*
		 * TODO: managedQuery is deprecated, use CursorLoader instead!
		 */
		String[] projection = 
				new String[] { ChatContent.Messages._ID,
				   			   ChatContent.Messages.OWNER,
				   			   ChatContent.Messages.CHATROOM,
				   			   ChatContent.Messages.OWNER_MESSAGE_ID,
				   			   ChatContent.Messages.TAGS,				
							   ChatContent.Messages.SENDER, 
							   ChatContent.Messages.MESSAGE };
/*		Cursor c = managedQuery(ChatContent.Messages.CONTENT_URI, 
				projection,
				null, null, null);*/
		ContentResolver cr = getActivity().getContentResolver();
		Cursor c = cr.query(ChatContent.Messages.CONTENT_URI,
		        projection, null, null, null);		
		return c;
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		messageAdapter.changeCursor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		messageAdapter.changeCursor(null);
	}	

	/*
	 * End Todo
	 */

	/*
	 * Options menu includes an option to list all peers from whom we have
	 * received communication.
	 */

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.main, menu);
	}
	
/*	public class Receiver extends BroadcastReceiver {
		 
	@Override 
	public void onReceive(Context context, Intent intent) {
	      // react to the event
		String action = intent.getAction();
		if(action.equalsIgnoreCase(ChatService.NEW_MESSAGE_BROADCAST)){  
		messageAdapter.changeCursor(makeMessageCursor());
		//LoaderManager lm = getLoaderManager();
		//lm.restartLoader(LOADER_ID, null, mCallbacks);	    	   
	       }
	   }
	}
	
	public void createNewUpdater() {
		this.updater = new Receiver();
	}
	
	public Receiver getUpdater() {
		return updater;
	}

	public void setUpdater(Receiver updater) {
		this.updater = updater;
	}*/
}
