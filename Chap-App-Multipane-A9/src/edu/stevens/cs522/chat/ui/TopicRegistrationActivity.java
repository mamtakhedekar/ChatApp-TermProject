package edu.stevens.cs522.chat.ui;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.messages.ChatService;
import edu.stevens.cs522.chat.messages.MessageUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TopicRegistrationActivity extends Activity{

	private Button register;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_topic_registration);
		register = (Button) findViewById(R.id.register_button);
		register.setOnClickListener(registerListener);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// TODO: Add menu for Register
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.register, menu);		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case (R.id.back):
			Intent callParent = getIntent();
			setResult(Activity.RESULT_OK, callParent);
			finish();
			return true;
		}
		return false;
	}
	
	/*
	 * On click listener for the register button
	 */
	private OnClickListener registerListener = new OnClickListener() {
		public void onClick(View v) {
			registerTopic();
		}
	};
	
	
	/*
	 * Open registration fragment
	 */
	private void registerTopic() {
		// send the message @TODO
//		MessageUtils.sendLogInMessageToRoom(getApplicationContext(), ChatService.class,
//				ipAddr, port, roomName, userName);
		
		Intent callParent = getIntent();
		setResult(Activity.RESULT_OK, callParent);
		finish();
		//return true;
	}	
}
