package edu.stevens.cs522.chat.providers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.net.Uri;
import android.util.Log;

public class NotificationProvider extends ContentProvider{
	
	private final static String TAG = NotificationProvider.class.getCanonicalName();

	private static final String DATABASE_NAME = "notifications.txt";
	
	@Override
	public boolean onCreate() {
		/*
		 * Can't create the notifications file here, don't have access to the context
		 * for openFileOutput (to provide write access to local file space).
		 */
		return true;
	}
	
	private static final int NOTIFICATIONS = 1;
	private static final int NOTIFICATION_ID = 2;

	private static final UriMatcher uriMatcher;

	/*
	 * Note: We should use a cursor that implements a circular buffer and drops all but the last N
	 * notifications received.  As it is, this buffer grows unboundedly, a potential DoS attack.
	 */
	MatrixCursor notifications = null;

	/*
	 * Utility operations for reading notifications from a file and saving notifications
	 * back to that file.
	 */
	
	private MatrixCursor loadNotifications() {

		if (notifications == null) {
			
			String[] columns = { ChatContent.Notifications._ID, 
								 ChatContent.Notifications.OWNER, 
								 ChatContent.Notifications.CHATROOM,
								 ChatContent.Notifications.TEXT};

			notifications = new MatrixCursor(columns);

			try {
				InputStream is = getContext().openFileInput(DATABASE_NAME);
				BufferedReader messageInputFile = new BufferedReader(new InputStreamReader(is));
				String notificationLine = messageInputFile.readLine();
				while (notificationLine != null) {
					/*
					 * Each line contains owner, chatroom and text.
					 */
					String[] notification = notificationLine.split(":");

					/*
					 * TODO: Finish this loop.
					 */
					notifications.addRow(notification);
					notificationLine = messageInputFile.readLine();					
					/*
					 * Each line contains owner, chatroom and text.
					 */
				}
				messageInputFile.close();
			} catch (FileNotFoundException e) {
				Log.i(TAG, "Notifications file has not been created yet.");
			} catch (IOException e) {
				Log.e(TAG, "IO error while reading notification file");
			}
		}

		return notifications;
	}
	
	private void saveNotifications(Cursor notifications) {

		notifications.moveToFirst();
		
		try {
			OutputStream os = getContext().openFileOutput(DATABASE_NAME, Context.MODE_PRIVATE);
			BufferedWriter notificationOutputFile = new BufferedWriter(new OutputStreamWriter(os));
			for (int row=1; row<=notifications.getCount(); row++) {
				String owner = notifications.getString(notifications.getColumnIndex(ChatContent.Notifications.OWNER));
				String chatroom = notifications.getString(notifications.getColumnIndex(ChatContent.Notifications.CHATROOM));
				String text = notifications.getString(notifications.getColumnIndex(ChatContent.Notifications.TEXT));
				notificationOutputFile.write(row + ":" + owner + ":" + chatroom + ":" + text);
				notificationOutputFile.newLine();
				notifications.moveToNext();
			}
			notificationOutputFile.close();
		} catch (IOException e) {
			Log.e(TAG, "IO error while writing notification file");
		}

	}	

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		/*
		 * We don't support notification deletion.
		 */
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (uriMatcher.match(uri) != NOTIFICATIONS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		/*
		 * Make sure fields are set
		 */
		if (values.containsKey(ChatContent.Notifications.OWNER) == false) {
			values.put(ChatContent.Notifications.OWNER, "Unknown");
		}

		if (values.containsKey(ChatContent.Notifications.CHATROOM) == false) {
			values.put(ChatContent.Notifications.CHATROOM, " ");
		}
		
		if (values.containsKey(ChatContent.Notifications.TEXT) == false) {
			values.put(ChatContent.Notifications.TEXT, " ");
		}		
		
		/*
		 * TODO: Load notifications from file, add new notification, and save file.
		 */
		MatrixCursor cursor = loadNotifications();
		
		String[] columns = { ChatContent.Notifications._ID,
				 ChatContent.Notifications.OWNER,
				 ChatContent.Notifications.CHATROOM, 
				 ChatContent.Notifications.TEXT };
		
		String[] notification = new String[columns.length];
		notification[0] = values.getAsString(ChatContent.Notifications._ID);
		notification[1] = values.getAsString(ChatContent.Notifications.OWNER);
		notification[2] = values.getAsString(ChatContent.Notifications.CHATROOM);
		notification[3] = values.getAsString(ChatContent.Notifications.TEXT);		
		
		cursor.addRow(notification);
		
		saveNotifications(cursor);		
		/*
		 * End Todo.
		 */
		
		int rowId = notifications.getCount();
		if (rowId > 0) {
			Uri notificationUri = ContentUris.withAppendedId(
					ChatContent.Notifications.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(notificationUri, null);
			return notificationUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}



	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		/*
		 * We just support returning all notifications.
		 */
		if (uriMatcher.match(uri) != NOTIFICATIONS) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		Cursor notifications = loadNotifications();
		return notifications;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		/*
		 * We don't support notification editing.
		 */
		return 0;
	}

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(ChatContent.Notifications.AUTHORITY, null, NOTIFICATIONS);
		uriMatcher.addURI(ChatContent.Notifications.AUTHORITY, "#", NOTIFICATION_ID);

	}

}
