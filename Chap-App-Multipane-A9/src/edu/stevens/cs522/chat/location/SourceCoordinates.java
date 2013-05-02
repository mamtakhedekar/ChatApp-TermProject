package edu.stevens.cs522.chat.location;

public class SourceCoordinates extends Coordinates {
	
	/*
	 * Every message that is exchanged has both physical and digital coordinates
	 * of the sender.
	 */
	
	private static final long serialVersionUID = -109828740180907080L;

	private String peer;
	
	private double longitude;
	
	private double latitude;
	
	public String getPeer() {
		return peer;
	}

	public void setPeer(String peer) {
		this.peer = peer;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public SourceCoordinates() {
	}

}
