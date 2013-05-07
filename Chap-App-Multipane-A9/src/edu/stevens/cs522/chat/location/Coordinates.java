package edu.stevens.cs522.chat.location;

import java.io.Serializable;
import java.net.InetAddress;

public class Coordinates implements Serializable {

	private static final long serialVersionUID = 3903990529302787822L;

	private InetAddress address;
	
	private int servicePort;

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getServicePort() {
		return servicePort;
	}

	public void setServicePort(int servicePort) {
		this.servicePort = servicePort;
	}

	public Coordinates() {
	}

}
