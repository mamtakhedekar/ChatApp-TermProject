package edu.stevens.cs522.chat.ui;

import edu.stevens.cs522.chat.R;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class UserInputActivity extends Activity {
	
	/*
	 * Widgets for user name, longitude, latitude text and send button.
	 */
	private EditText userNameText;
	private EditText latitudeText;
	private EditText longitudeText;
	
	//public static final String CHATROOM_ID_KEY = "chatroom_id";
	//static String CR_ID_KEY;	
	static private String UNAME = ""; 
	static private String LONGITUDE = ""; 
	static private String LATITUDE = "";	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_input);
	    //Intent intent = getIntent();
	    //CR_ID_KEY = intent.getStringExtra(CHATROOM_ID_KEY);		
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_user_input, menu);
		return true;
	}
	

	public void callChatClient(View view) {

		/*
		 * Append client info to the front of the message.
		 */
		userNameText = (EditText) findViewById(R.id.ui_user_text);
		latitudeText = (EditText) findViewById(R.id.ui_latitude);
		longitudeText = (EditText) findViewById(R.id.ui_longitude);
		
		String user_name = userNameText.getText().toString();
		String latitude = latitudeText.getText().toString();
		String longitude = longitudeText.getText().toString();

		Resources res = getResources();
		UNAME = res.getString(R.string.UNAME);
		LONGITUDE = res.getString(R.string.LONGITUDE);
		LATITUDE = res.getString(R.string.LATITUDE);
				
		// Transfer user data back to chat app.
		Intent listIntent = new Intent(this, ChatRoomListActivity.class);
		//detailIntent.putExtra(ChatRoomDetailFragment.CHATROOM_ID_KEY, CR_ID_KEY);
		listIntent.putExtra(UNAME, user_name);
		listIntent.putExtra(LATITUDE, latitude);
		listIntent.putExtra(LONGITUDE, longitude);
		startActivity(listIntent);
	}	
}
