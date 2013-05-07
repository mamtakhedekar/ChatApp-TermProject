package edu.stevens.cs522.chat.ui;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.messages.ChatService;
import edu.stevens.cs522.chat.messages.IChatService;
import edu.stevens.cs522.chat.messages.MessageUtils;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TopicRegistrationActivity extends Activity{

	private Button register;
	private static String user_name = "";
	private EditText ipAddr;
	private EditText port;
	private EditText topic;	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user_name = getIntent().getStringExtra(getResources().getString(R.string.UNAME));
		setContentView(R.layout.activity_topic_registration);
		register = (Button) findViewById(R.id.register_button);
		register.setOnClickListener(registerListener);
		
		Intent intent = new Intent(this, ChatService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		startService(intent);	
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
	 * Service binder.
	 */
	private IChatService service;
	
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
	/*
	 * Open registration fragment
	 */
	private void registerTopic() {
		ipAddr = (EditText) findViewById(R.id.server_ip_text);
		port = (EditText) findViewById(R.id.server_port_text);
		topic = (EditText) findViewById(R.id.chatroom_name_text);
		String ip_Addr_String = ipAddr.getText().toString();
		String port_String = port.getText().toString();
		String topic_String  = topic.getText().toString();
		MessageUtils.sendLogInMessageToRoom(getApplicationContext(), service,
				ip_Addr_String, Integer.parseInt(port_String), topic_String, user_name);
		
		Intent callParent = getIntent();
		setResult(Activity.RESULT_OK, callParent);
		finish();
		//return true;
	}	
}
