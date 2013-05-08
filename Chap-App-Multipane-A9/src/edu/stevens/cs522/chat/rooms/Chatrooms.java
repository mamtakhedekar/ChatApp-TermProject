package edu.stevens.cs522.chat.rooms;

import java.util.ArrayList;
import java.util.List;

public class Chatrooms {

	private List<Chatroom> rooms;
	
	private Chatrooms() {
		rooms = new ArrayList<Chatroom>();
		rooms.add(Chatroom.getDefault());
	}
	
	public boolean checkAndAddRoom(String newRoomName, String p_ip, int p_port)
	{
		if ((findRoomInList(newRoomName))!= -1)
		{
			return false;
		}
		rooms.add(new Chatroom(newRoomName, p_ip, p_port));
		return true;
	}
	
	private int findRoomInList(String searchRoomName)
	{
		for (int i = 0 ; i < rooms.size(); i++) {
			if (rooms.get(i).getName().compareTo(searchRoomName) == 0)
			{
				return i;
			}
		}
		return -1;
	}
	
	public Chatroom getRoomForName(String name)
	{
		int index = findRoomInList(name);
		
		if (index != -1 )
		{
			return rooms.get(index);
		}
		
		return null;
	}
	
	public static Chatrooms createChatrooms() {
		return new Chatrooms();
	}
	
	public List<String> getRoomNames() {
		List<String> names = new ArrayList<String>();
		for (Chatroom room : rooms) {
			names.add(room.getName());
		}
		return names;
	}
	
	public Chatroom getRoom(int index) {
		return rooms.get(index);
	}
	
}
