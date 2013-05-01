/*********************************************************************

    Content provider for peer information for the chat program.
    This collects the URIs, MIME types and column names for the content provider.

    Copyright (c) 2012 Stevens Institute of Technology

**********************************************************************/

package edu.stevens.cs522.chat.providers;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/*
 * @author dduggan
 * 
 * Convenience class for content providers
 */
public class ChatContent {
	
	public static final int CHATROOMS_LOADER = 0;
	public static final int MESSAGES_LOADER = 1;
	public static final int NOTIFICATIONS_LOADER = 2;
	public static final int PEERS_LOADER = 3;
	
	public static final String AUTHORITY_PREFIX = "edu.stevens.cs522.chat.service";

	/*
	 * Content with information about chatrooms:
	 * content://edu.stevens.cs522.chat/chatrooms
	 */
	public static final class Chatrooms implements BaseColumns {
		
		/*
		 * URI for Loader Manager.
		 */
		public static final int URI_LOADER = CHATROOMS_LOADER;
		
		/*
		 * Content URI for message data
		 */
		public static final String AUTHORITY = AUTHORITY_PREFIX + ".chatrooms";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY);
		
		/*
         * The MIME type of {@link #CONTENT_URI} providing a directory of chatrooms.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.edu.stevens.cs.cs522.chat.chatrooms";

        /*
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single chatroom.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.edu.stevens.cs.cs522.chat.chatroom";

	
		public static final String DEFAULT_SORT_ORDER = "_id ASC";
	
		/*
		 * Chatroom name.
		 */
		public static final String NAME = "name";
	
		/*
		 * Chatroom owner name ("SELF" for this device, "GLOBAL" for cloud).
		 */
		public static final String OWNER = "owner";
	
		/*
		 * Message sequence id (last message received on this channel).
		 * (What if messages are not delivered FIFO?)
		 */
		public static final String MESSAGE_SEQ_ID = "message_seq_id";
		
		/*
		 * Colon-separated list of peers who have subscribed to a channel.
		 */
		public static final String SUBSCRIBERS = "subscribers";
		
		/*
		 * Getter operations for the cursor.
		 */
	
		public static final long getChatroomId(Cursor c) {
			return c.getLong(c.getColumnIndexOrThrow(_ID));
		}

		public static final String getChatroomName(Cursor c) {
			return c.getString(c.getColumnIndexOrThrow(NAME));
		}

		public static final String getChatroomOwner(Cursor c) {
			return c.getString(c.getColumnIndexOrThrow(OWNER));
		}

		public static final int getMessageSeqId(Cursor c) {
			return c.getInt(c.getColumnIndexOrThrow(MESSAGE_SEQ_ID));
		}

		public static final List<String> getSubscribers(Cursor c) {
			List<String> subList = new ArrayList<String>();
			String[] subs = c.getString(c.getColumnIndexOrThrow(SUBSCRIBERS)).split(":");
			for (String tag : subs) {
				subList.add(tag);
			}
			return subList;
		}

		/*
		 * Putter operations for insertion and update.
		 */
	
		public static final void putChatroomName(ContentValues v, String name) {
			v.put(NAME, name);
		}

		public static final void putChatroomOwner(ContentValues v, String owner) {
			v.put(OWNER,  owner);
		}

		public static final void putMessageSeqId(ContentValues v, int seqNumber) {
			v.put(MESSAGE_SEQ_ID, seqNumber);
		}

		private static final void putSubscribers(ContentValues v, String[] subs) {
			if (subs.length==0) {
				v.put(SUBSCRIBERS, "");
			} else {
				String subList = subs[0];
				for (int ix=1; ix<subs.length; ix++) {
					subList += ":" + subs[ix];
				}
				v.put(SUBSCRIBERS, subList);
			}
		}
		
		public static final void putSubscribers(ContentValues v, List<String> subList) {
			String[] subs = new String[subList.size()];
			putSubscribers(v, subList.toArray(subs));
		}

	}


	/*
	 * Content with information about messages:
	 * content://edu.stevens.cs522.chat/messages
	 */
	public static final class Messages implements BaseColumns {
		
		/*
		 * URI for Loader Manager.
		 */
		public static final int URI_LOADER = MESSAGES_LOADER;
		
		/*
		 * Content URI for message data
		 */
		public static final String AUTHORITY = AUTHORITY_PREFIX + ".messages";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY);
		
		/*
         * The MIME type of {@link #CONTENT_URI} providing a directory of messages.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.edu.stevens.cs.cs522.chat.messages";

        /*
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single message.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.edu.stevens.cs.cs522.chat.messages";

	
		public static final String DEFAULT_SORT_ORDER = "_id ASC";
		
		/* The chatroom on which a message is sent is identified by the name of the chatroom
		 * and the name of the owner of the chatroom (don't confuse two chatrooms with the same
		 * name but different owners).
		 */
		public static final String OWNER = "owner";

		public static final String CHATROOM = "chatroom";

		/*
		 * Id assigned to message by owner.
		 */
		public static final String OWNER_MESSAGE_ID = "owner_message_id";

		/*
		 * Semantic tag assigned to message by sender.  
		 * This should be a list of tags.
		 */
		public static final String TAGS = "tags";

		/*
		 * Sender name (all other sender information in the PeerInfo provider.
		 */
		public static final String SENDER = "sender";
	
		/*
		 * Content of a message.
		 */
		public static final String MESSAGE = "message";
	
		/*
		 * Getter operations for the cursor.
		 */
	
		public static final long getChatroomId(Cursor c) {
			return c.getLong(c.getColumnIndexOrThrow(_ID));
		}

		public static final String getChatroomOwner(Cursor c) {
			return c.getString(c.getColumnIndexOrThrow(OWNER));
		}

		public static final String getChatroomName(Cursor c) {
			return c.getString(c.getColumnIndexOrThrow(CHATROOM));
		}

		public static final int getMessageId(Cursor c) {
			return c.getInt(c.getColumnIndexOrThrow(OWNER_MESSAGE_ID));
		}

		public static final List<String> getTags(Cursor c) {
			List<String> tagList = new ArrayList<String>();
			String[] tags = c.getString(c.getColumnIndexOrThrow(TAGS)).split(":");
			for (String tag : tags) {
				tagList.add(tag);
			}
			return tagList;
		}

		public static final String getSender(Cursor c) {
			return c.getString(c.getColumnIndexOrThrow(SENDER));
		}

		public static final String getMessageText(Cursor c) {
			return c.getString(c.getColumnIndexOrThrow(MESSAGE));
		}
		
		/*
		 * Putter operations for insertion and update.
		 */
	
		public static final void putChatroomOwner(ContentValues v, String owner) {
			v.put(OWNER, owner);
		}

		public static final void putChatroomName(ContentValues v, String name) {
			v.put(CHATROOM, name);
		}

		public static final void putMessageId(ContentValues v, int seqNumber) {
			v.put(OWNER_MESSAGE_ID, seqNumber);
		}

		private static final void putTags(ContentValues v, String[] tags) {
			if (tags.length==0) {
				v.put(TAGS, "");
			} else {
				String subList = tags[0];
				for (int ix=1; ix<tags.length; ix++) {
					subList += ":" + tags[ix];
				}
				v.put(TAGS, subList);
			}
		}

		public static final void putTags(ContentValues v, List<String> tagList) {
			String[] tags = new String[tagList.size()];
			putTags(v, tagList.toArray(tags));
		}
		
		public static final void putSender(ContentValues v, String sender) {
			v.put(SENDER, sender);
		}

		public static final void putMessageText(ContentValues v, String text) {
			v.put(MESSAGE, text);
		}
		

	}


	/*
	 * Content with information about chatroom notifications:
	 * content://edu.stevens.cs522.chat/notifications
	 */
	public static final class Notifications implements BaseColumns {
		
		/*
		 * URI for Loader Manager.
		 */
		public static final int URI_LOADER = NOTIFICATIONS_LOADER;
		
		/*
		 * Content URI for message data
		 */
		public static final String AUTHORITY = AUTHORITY_PREFIX + ".notifications";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY);
		
		/*
         * The MIME type of {@link #CONTENT_URI} providing a directory of notifications.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.edu.stevens.cs.cs522.chat.notifications";

        /*
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single notification.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.edu.stevens.cs.cs522.chat.notification";

	
		public static final String DEFAULT_SORT_ORDER = "_id ASC";
		
		/* Owner of the chatroom.
		 */
		public static final String OWNER = "owner";

		/*
		 * Name of the chatroom.
		 */
		public static final String CHATROOM = "chatroom";
		
		/*
		 * Text to be displayed when viewing notification.
		 */
		public static final String TEXT = "text";
		
		/*
		 * Getter operations for the cursor.
		 */
	
		public static final long getChatroomId(Cursor c) {
			return c.getLong(c.getColumnIndexOrThrow(_ID));
		}

		public static final String getOwner(Cursor c) {
			return c.getString(c.getColumnIndexOrThrow(OWNER));
		}

		public static final String getChatroom(Cursor c) {
			return c.getString(c.getColumnIndexOrThrow(CHATROOM));
		}
		
		public static final String getText(Cursor c) {
			return c.getString(c.getColumnIndexOrThrow(TEXT));
		}
		
		/*
		 * Putter operations for insertion and update.
		 */
	
		public static final void putOwner(ContentValues v, String owner) {
			v.put(OWNER, owner);
		}

		public static final void putChatroom(ContentValues v, String chatroom) {
			v.put(CHATROOM, chatroom);
		}
		
		public static final void putText(ContentValues v, String text) {
			v.put(TEXT, text);
		}

	}


	/*
	 * Content with information about peers:
	 * content://edu.stevens.cs522.chat/peers
	 */
	public static final class Peers implements BaseColumns {
		
		/*
		 * URI for Loader Manager.
		 */
		public static final int URI_LOADER = PEERS_LOADER;
		
		/*
		 * Content URI for peer data
		 */
		public static final String AUTHORITY = AUTHORITY_PREFIX + ".peers";
		public static final Uri CONTENT_URI = Uri.parse("content://"+ AUTHORITY);
		
		/*
         * The MIME type of {@link #CONTENT_URI} providing a directory of peers.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.edu.stevens.cs.cs522.chat.peers";

        /*
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single peer.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.edu.stevens.cs.cs522.chat.peers";

	
		public static final String DEFAULT_SORT_ORDER = "_id ASC";
	
		/*
		 * Peer user name
		 */
		public static final String NAME = "name";
	
		/*
		 * Host (IP address) for that peer's chat service (obtained from messages sent)
		 */
		public static final String HOST = "host";
	
		/*
		 * UDP port number for that peer's chat service (obtained from messages sent)
		 */
		public static final String PORT = "port";
	
		/*
		 * latitude coordinate
		 */
		public static final String LATITUDE = "latitude";
	
		/*
		 * longitude coordinate
		 */
		public static final String LONGITUDE = "longitude";
		
		/*
		 * Getter operations for the cursor.
		 */
	
		public static final long getChatroomId(Cursor c) {
			return c.getLong(c.getColumnIndexOrThrow(_ID));
		}

		public static final String getName(Cursor c) {
			return c.getString(c.getColumnIndexOrThrow(NAME));
		}

		public static final String getHost(Cursor c) {
			return c.getString(c.getColumnIndexOrThrow(HOST));
		}

		public static final int getPort(Cursor c) {
			return c.getInt(c.getColumnIndexOrThrow(PORT));
		}

		public static final double getLatitude(Cursor c) {
			return c.getDouble(c.getColumnIndexOrThrow(LATITUDE));
		}

		public static final double getLongitude(Cursor c) {
			return c.getDouble(c.getColumnIndexOrThrow(LONGITUDE));
		}

		/*
		 * Putter operations for insertion and update.
		 */
	
		public static final void putName(ContentValues v, String name) {
			v.put(NAME, name);
		}

		public static final void putHost(ContentValues v, String host) {
			v.put(HOST, host);
		}

		public static final void putPort(ContentValues v, int port) {
			v.put(PORT, port);
		}

		public static final void putLatitude(ContentValues v, double lat) {
			v.put(LATITUDE, lat);
		}

		public static final void putLongitude(ContentValues v, double lng) {
			v.put(LONGITUDE, lng);
		}

	
	}
}
