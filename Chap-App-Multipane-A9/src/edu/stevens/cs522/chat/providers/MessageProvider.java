/*********************************************************************

    Content provider for messages received.  We record both the 
    message and the identity of the sender.

    Copyright (c) 2012 Stevens Institute of Technology

 **********************************************************************/

package edu.stevens.cs522.chat.providers;

import java.util.HashMap;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/*
 * @author dduggan
 * 
 * Derived from the NotesList sample application
 *
 */

public class MessageProvider extends ContentProvider {
	
	private final static String TAG = MessageProvider.class.getCanonicalName();

	private static final String DATABASE_NAME = "chat1.db";
	private static final int DATABASE_VERSION = 3;
	private static final String MESSAGE_TABLE_NAME = "messages";
	
	private static HashMap<String, String> messageProjectionMap;
	
	private static final int MESSAGES = 1;
	private static final int MESSAGE_ID = 2;

	private static final UriMatcher uriMatcher;
	
	/*
	 * helper class to open, create, and upgrade the database file
	 */	
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + MESSAGE_TABLE_NAME + " ("
					+ ChatContent.Messages._ID + " INTEGER PRIMARY KEY,"
					+ ChatContent.Messages.OWNER + " TEXT,"
					+ ChatContent.Messages.CHATROOM + " TEXT,"
					+ ChatContent.Messages.OWNER_MESSAGE_ID + " INTEGER,"
					+ ChatContent.Messages.TAGS + " TEXT,"
					+ ChatContent.Messages.SENDER + " TEXT,"
					+ ChatContent.Messages.MESSAGE + " TEXT"
					+ ");");
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE_NAME);
			onCreate(db);
		}
	}
	
	private DatabaseHelper openHelper;
	
	
	@Override
	public boolean onCreate() {
		openHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		switch (uriMatcher.match(uri)) {
		case MESSAGES:
			qb.setTables(MESSAGE_TABLE_NAME);
			qb.setProjectionMap(messageProjectionMap);
			break;
			
		case MESSAGE_ID:
			qb.setTables(MESSAGE_TABLE_NAME);
			qb.setProjectionMap(messageProjectionMap);
			qb.appendWhere(ChatContent.Messages._ID + "=" + uri.getPathSegments().get(1));
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		/*
		 * Use default sort order if not specified
		 */
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = ChatContent.Messages.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}
	
		/*
		 * Query the database
		 */
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		
		/*
		 * Tell the cursor what uri to watch (for data changes)
		 */
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case MESSAGES:
			return ChatContent.Messages.CONTENT_TYPE;
			
		case MESSAGE_ID:
			return ChatContent.Messages.CONTENT_ITEM_TYPE;
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (uriMatcher.match(uri) != MESSAGES) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		
		// Make sure fields are set
		if (values.containsKey(ChatContent.Messages.OWNER) == false) {
			values.put(ChatContent.Messages.OWNER, "Unknown");
		}
		
		if (values.containsKey(ChatContent.Messages.CHATROOM) == false) {
			values.put(ChatContent.Messages.CHATROOM, " ");
		}
		
		if (values.containsKey(ChatContent.Messages.OWNER_MESSAGE_ID) == false) {
			values.put(ChatContent.Messages.OWNER_MESSAGE_ID, 0);
		}

		if (values.containsKey(ChatContent.Messages.TAGS) == false) {
			values.put(ChatContent.Messages.TAGS, " ");
		}
		
		if (values.containsKey(ChatContent.Messages.SENDER) == false) {
			values.put(ChatContent.Messages.SENDER, "Unknown");
		}
		
		if (values.containsKey(ChatContent.Messages.MESSAGE) == false) {
			values.put(ChatContent.Messages.MESSAGE, " ");
		}		
		
		SQLiteDatabase db = openHelper.getWritableDatabase();
		long rowId = db.insert(MESSAGE_TABLE_NAME, ChatContent.Messages.MESSAGE, values);
		if (rowId > 0) {
			Uri messageUri = ContentUris.withAppendedId(ChatContent.Messages.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(messageUri, null);
			return messageUri;
		}
	
		throw new SQLException("Failed to insert row into " + uri);
	}	
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		int count;
		switch (uriMatcher.match(uri)) {
		case MESSAGES:
			count = db.delete(MESSAGE_TABLE_NAME, selection, selectionArgs);
			break;
			
		case MESSAGE_ID: 
			String id = uri.getPathSegments().get(1);
			count = db.delete(MESSAGE_TABLE_NAME, ChatContent.Messages._ID
					+ "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" 
							+ selection + ")" : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}	

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
	
		int count;
		switch (uriMatcher.match(uri)) {
		case MESSAGES:
			count = db.update(MESSAGE_TABLE_NAME, values, selection, selectionArgs);
			break;
			
		case MESSAGE_ID:
			String id = uri.getPathSegments().get(1);
			count = db.update(MESSAGE_TABLE_NAME, values, ChatContent.Messages._ID
					+ "=" + id + (!TextUtils.isEmpty(selection) ? " AND (" 
							+ selection + ")" : ""), selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(ChatContent.Messages.AUTHORITY, null, MESSAGES);
		uriMatcher.addURI(ChatContent.Messages.AUTHORITY, "#", MESSAGE_ID);
		
		messageProjectionMap = new HashMap<String, String>();
		messageProjectionMap.put(ChatContent.Messages._ID, ChatContent.Messages._ID);
		messageProjectionMap.put(ChatContent.Messages.OWNER, ChatContent.Messages.OWNER);
		messageProjectionMap.put(ChatContent.Messages.CHATROOM, ChatContent.Messages.CHATROOM);
		messageProjectionMap.put(ChatContent.Messages.OWNER_MESSAGE_ID, ChatContent.Messages.OWNER_MESSAGE_ID);
		messageProjectionMap.put(ChatContent.Messages.TAGS, ChatContent.Messages.TAGS);
		messageProjectionMap.put(ChatContent.Messages.SENDER, ChatContent.Messages.SENDER);
		messageProjectionMap.put(ChatContent.Messages.MESSAGE, ChatContent.Messages.MESSAGE);		
	}
}
