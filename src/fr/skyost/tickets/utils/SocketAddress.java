package fr.skyost.tickets.utils;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Represents the address of a remote Skyotickets.
 * 
 * @author Skyost
 */

public class SocketAddress implements Serializable {
	
	/**
	 * Used to serialize the object.
	 */
	
	private static final long serialVersionUID = 1L;

	/**
	 * Address of the Skyotickets.
	 */
	
	public final InetAddress inetAddress;
	
	/**
	 * Port of the Skyotickets.
	 */
	
	public final int port;
	
	/**
	 * Create a new address.
	 * 
	 * @param inetAdress Address of Skyotickets.
	 * @param port Port of Skyotickets.
	 */
	
	public SocketAddress(final InetAddress inetAddress, final int port) {
		this.inetAddress = inetAddress;
		this.port = port;
	}
	
}
