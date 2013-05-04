package edu.stevens.cs522.chat.ui;

import edu.stevens.cs522.chat.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class TopicRegistrationActivity extends Activity{

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic_registration);
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
			Intent chatIntent = new Intent(this, ChatRoomListActivity.class);
			startActivity(chatIntent);
			finish();
			return true;
		}
		return false;
	}	
}
