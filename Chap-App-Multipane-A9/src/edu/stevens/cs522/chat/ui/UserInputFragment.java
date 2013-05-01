package edu.stevens.cs522.chat.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import edu.stevens.cs522.chat.R;

/**
 * A fragment representing a single User Input screen. This fragment is
 * either contained in a {@link ChatRoomListActivity} in two-pane mode (on
 * tablets) or a {@link ChatRoomDetailActivity} on handsets.
 */
public class UserInputFragment extends Fragment {

	private final static String TAG = UserInputFragment.class
			.getCanonicalName();
	
	private EditText userNameText;
	private EditText latitudeText;
	private EditText longitudeText;
	
	public static final String CHATROOM_ID_KEY = "chatroom_id";
	static String CR_ID_KEY;
	static private String UNAME = ""; 
	static private String LONGITUDE = ""; 
	static private String LATITUDE = "";
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public UserInputFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CR_ID_KEY = getArguments().getString(CHATROOM_ID_KEY);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_user_input,
				container, false);

		userNameText = (EditText) rootView.findViewById(R.id.ui_user_text);
		latitudeText = (EditText) rootView.findViewById(R.id.ui_latitude);
		longitudeText = (EditText) rootView.findViewById(R.id.ui_longitude);

		send = (Button) rootView.findViewById(R.id.ui_send_button1);
		send.setOnClickListener(sendListener);

		return rootView;
	}

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
	 * Send the longitude, latitude and user name
	 */
	private void postMessage() {

		String user_name = userNameText.getText().toString();
		String latitude = latitudeText.getText().toString();
		String longitude = longitudeText.getText().toString();

		Resources res = getResources();
		UNAME = res.getString(R.string.UNAME);
		LONGITUDE = res.getString(R.string.LONGITUDE);
		LATITUDE = res.getString(R.string.LATITUDE);
				
		// Transfer user data back to chat app.
/*		Intent transferUserData = new Intent(this, ChatClient.class);
		transferUserData.putExtra(UNAME, user_name);
		transferUserData.putExtra(LATITUDE, latitude);
		transferUserData.putExtra(LONGITUDE, longitude);
		
		startActivity(transferUserData);*/
		Bundle arguments = new Bundle();
		arguments.putString(ChatRoomDetailFragment.CHATROOM_ID_KEY, CR_ID_KEY);
		arguments.putString(UNAME, user_name);
		arguments.putString(LATITUDE, latitude);
		arguments.putString(LONGITUDE, longitude);		
		ChatRoomDetailFragment fragment = new ChatRoomDetailFragment();
		fragment.setArguments(arguments);
		getFragmentManager().beginTransaction()
				.replace(R.id.chatroom_detail_container, fragment).commit();
	}
}
