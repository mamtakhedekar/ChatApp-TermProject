package edu.stevens.cs522.chat.rooms;

public class Chatroom {
	
	private String name;
	private String ip;
	private int port;
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}
	
/*	public String getPortString() {
		return String.valueOf(this.port);
		//return Integer.valueOf(port).toString();
	}*/

	public void setPort(int port) {
		this.port = port;
	}
	
	Chatroom(String name, String p_ip, int p_port) {
		this.name = name;
		this.ip = p_ip;
		this.port = p_port;
	}
	
	public static Chatroom createRoom(String name, String p_ip, int p_port) {
		return new Chatroom(name, p_ip, p_port);
	}
	
	private static Chatroom defaultRoom = createRoom("MAIN", "10.0.2.2", 6667);
	
	public static Chatroom getDefault() {
		return defaultRoom;
	}
	
	public String getName() {
		return name;
	}
}
