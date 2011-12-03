
/***************************************************************************
 *   Copyright 2006-2009 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/

package net.usikkert.kouchat.net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class containing utility methods for network operations.
 *
 * @author Christian Ihle
 */
public final class NetworkUtils
{
	/** The logger. */
	private static final Logger LOG = Logger.getLogger( NetworkUtils.class.getName() );

	/**
	 * Private constructor. Only static methods here.
	 */
	private NetworkUtils()
	{

	}

	/**
	 * Checks if the network interface is up, and usable.
	 *
	 * <p>A network interface is usable when it:</p>
	 *
	 * <ul>
	 *   <li>Is up.</li>
	 *   <li>Supports multicast.</li>
	 *   <li>Is not a loopback device, like localhost.</li>
	 *   <li>Is not a point to point device, like a modem.</li>
	 *   <li>Is not virtual, like <code>eth0:1</code>.</li>
	 *   <li>Is not a virtual machine network interface (vmnet).</li>
	 *   <li>Has an IPv4 address.</li>
	 * </ul>
	 *
	 * @param netif The network interface to check.
	 * @return True if the network interface is usable.
	 */
	public static boolean isUsable( final NetworkInterface netif )
	{
		if ( netif == null )
			return false;

		try
		{
			return netif.isUp() && !netif.isLoopback() && !netif.isPointToPoint()
					&& !netif.isVirtual() && netif.supportsMulticast()
					&& !netif.getName().toLowerCase().contains( "vmnet" )
					&& !netif.getDisplayName().toLowerCase().contains( "vmnet" )
					&& hasIPv4Address( netif );
		}

		catch ( final SocketException e )
		{
			LOG.log( Level.WARNING, e.toString() );
			return false;
		}
	}

	/**
	 * Checks if the network interface has an IPv4-address.
	 *
	 * @param netif The network interface to check.
	 * @return If an IPv4-address was found or not.
	 */
	public static boolean hasIPv4Address( final NetworkInterface netif )
	{
		if ( netif == null )
			return false;

		Enumeration<InetAddress> inetAddresses = netif.getInetAddresses();

		while ( inetAddresses.hasMoreElements() )
		{
			InetAddress inetAddress = inetAddresses.nextElement();
			if ( inetAddress instanceof Inet4Address )
				return true;
		}

		return false;
	}

	/**
	 * Constructs a string with the information found on a {@link NetworkInterface}.
	 *
	 * @param netif The network interface to check.
	 * @return A string with information.
	 */
	public static String getNetworkInterfaceInfo( final NetworkInterface netif )
	{
		if ( netif == null )
			return "Invalid network interface.";

		try
		{
			return "Interface name: " + netif.getDisplayName() + "\n"
					+ "Device: " + netif.getName() + "\n"
					+ "Is loopback: " + netif.isLoopback() + "\n"
					+ "Is up: " + netif.isUp() + "\n"
					+ "Is p2p: " + netif.isPointToPoint() + "\n"
					+ "Is virtual: " + netif.isVirtual() + "\n"
					+ "Supports multicast: " + netif.supportsMulticast() + "\n"
					+ "MAC address: " + getMacAddress( netif ) + "\n"
					+ "IP addresses: " + getIPv4Addresses( netif );
		}

		catch ( final SocketException e )
		{
			LOG.log( Level.WARNING, e.toString() );
			return "Failed to get network interface information.";
		}
	}

	/**
	 * Returns a list of the IPv4-addresses on the network interface in string format.
	 *
	 * @param netif The network interface to get the IPv4-addresses from.
	 * @return All the IPv4-addresses on the network interface.
	 */
	public static String getIPv4Addresses( final NetworkInterface netif )
	{
		if ( netif == null )
			return "";

		String ipAddress = "";
		Enumeration<InetAddress> inetAddresses = netif.getInetAddresses();

		while ( inetAddresses.hasMoreElements() )
		{
			InetAddress inetAddress = inetAddresses.nextElement();
			if ( inetAddress instanceof Inet4Address )
				ipAddress += inetAddress.getHostAddress() + " ";
		}

		return ipAddress;
	}

	/**
	 * Returns the MAC-address of the network interface, in hex format.
	 *
	 * @param netif The network interface to get the MAC-address of.
	 * @return The MAC-address in hex, as a string.
	 */
	public static String getMacAddress( final NetworkInterface netif )
	{
		if ( netif == null )
			return "";

		String macAddress = "";
		byte[] address = null;

		try
		{
			address = netif.getHardwareAddress();
		}

		catch ( final SocketException e )
		{
			LOG.log( Level.WARNING, e.toString() );
		}

		if ( address != null )
		{
			// Convert byte array to hex format
			for ( int i = 0; i < address.length; i++ )
			{
				macAddress += String.format( "%02x", address[i] );
				if ( i != address.length - 1 )
					macAddress += "-";
			}
		}

		return macAddress.toUpperCase();
	}

	/**
	 * Fetches all the network interfaces again, and returns the one
	 * which is the same as the original network interface.
	 *
	 * <p>This is useful to make sure the network interface information
	 * is up to date, like the current ip address.</p>
	 *
	 * @param origNetIf The original network interface to compare with.
	 * @return An updated version of the same network interface,
	 * 		   or <code>null</code> if not found.
	 */
	public static NetworkInterface getUpdatedNetworkInterface( final NetworkInterface origNetIf )
	{
		if ( origNetIf == null )
			return null;

		Enumeration<NetworkInterface> networkInterfaces = getNetworkInterfaces();

		if ( networkInterfaces == null )
			return null;

		while ( networkInterfaces.hasMoreElements() )
		{
			NetworkInterface netif = networkInterfaces.nextElement();
			if ( sameNetworkInterface( origNetIf, netif ) )
				return netif;
		}

		return null;
	}

	/**
	 * Compares 2 network interfaces. The only way the 2 network interfaces
	 * can be considered the same is if they have the exact same name.
	 *
	 * <p>If any of the network interfaces are <code>null</code> then they
	 * are not considered the same.</p>
	 *
	 * @param netIf1 The first network interface.
	 * @param netIf2 The second network interface.
	 * @return If they are the same or not.
	 */
	public static boolean sameNetworkInterface( final NetworkInterface netIf1, final NetworkInterface netIf2 )
	{
		if ( netIf1 == null || netIf2 == null )
			return false;

		return netIf1.getName().equals( netIf2.getName() );
	}

	/**
	 * Iterates through a list of available network interfaces, and returns
	 * the first that is usable. Returns <code>null</code> if no usable
	 * interface is found.
	 *
	 * @return The first usable network interface, or <code>null</code>.
	 * @see #isUsable(NetworkInterface)
	 */
	public static NetworkInterface findFirstUsableNetworkInterface()
	{
		Enumeration<NetworkInterface> networkInterfaces = getNetworkInterfaces();

		if ( networkInterfaces == null )
			return null;

		while ( networkInterfaces.hasMoreElements() )
		{
			NetworkInterface netif = networkInterfaces.nextElement();
			if ( isUsable( netif ) )
				return netif;
		}

		return null;
	}

	/**
	 * Gets all the available network interfaces. Returns <code>null</code>
	 * if the operation fails, or no interfaces are available.
	 *
	 * @return All network interfaces, or <code>null</code>.
	 */
	public static Enumeration<NetworkInterface> getNetworkInterfaces()
	{
		Enumeration<NetworkInterface> networkInterfaces = null;

		try
		{
			networkInterfaces = NetworkInterface.getNetworkInterfaces();
		}

		catch ( final SocketException e )
		{
			LOG.log( Level.WARNING, e.toString() );
		}

		return networkInterfaces;
	}

	/**
	 * Gets the name of the localhost.
	 *
	 * @return The host name, or <code>null</code> if the host name cannot be determined.
	 */
	public static String getLocalHostName()
	{
		try
		{
			return InetAddress.getLocalHost().getHostName();
		}

		catch ( final UnknownHostException e )
		{
			LOG.log( Level.WARNING, e.toString() );
		}

		return null;
	}

	/**
	 * Gets the host name of the ip address.
	 *
	 * @param ipAddress The ip address to find the host name of.
	 * @return The host name, or <code>null</code> if the host name cannot be determined.
	 */
	public static String getHostName( final String ipAddress )
	{
		try
		{
			InetAddress inetAddress = InetAddress.getByName( ipAddress );
			String hostName = inetAddress.getHostName();

			if ( !hostName.equals( ipAddress ) )
				return hostName;
		}

		catch ( final UnknownHostException e )
		{
			LOG.log( Level.WARNING, e.toString() );
		}

		return null;
	}
}
