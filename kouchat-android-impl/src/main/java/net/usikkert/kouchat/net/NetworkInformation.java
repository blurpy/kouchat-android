
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

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import net.usikkert.kouchat.util.Validate;

/**
 * This is a JMX MBean for the network service.
 *
 * @author Christian Ihle
 */
public class NetworkInformation implements NetworkInformationMBean
{
	/** Information and control of the network. */
	private final ConnectionWorker connectionWorker;

	/**
	 * Constructor.
	 *
	 * @param connectionWorker To get information about the network, and control the network.
	 */
	public NetworkInformation( final ConnectionWorker connectionWorker )
	{
		Validate.notNull( connectionWorker, "Connection worker can not be null" );
		this.connectionWorker = connectionWorker;
	}

	/** {@inheritDoc} */
	@Override
	public String showCurrentNetwork()
	{
		NetworkInterface networkInterface = connectionWorker.getCurrentNetworkInterface();

		if ( networkInterface == null )
			return "No current network interface.";
		else
			return NetworkUtils.getNetworkInterfaceInfo( networkInterface );
	}

	/** {@inheritDoc} */
	@Override
	public String showOperatingSystemNetwork()
	{
		OperatingSystemNetworkInfo osNicInfo = new OperatingSystemNetworkInfo();
		NetworkInterface osInterface = osNicInfo.getOperatingSystemNetworkInterface();

		if ( osInterface == null )
			return "No network interface detected.";
		else
			return NetworkUtils.getNetworkInterfaceInfo( osInterface );
	}

	/** {@inheritDoc} */
	@Override
	public String[] showUsableNetworks()
	{
		List<String> list = new ArrayList<String>();

		Enumeration<NetworkInterface> networkInterfaces = NetworkUtils.getNetworkInterfaces();

		if ( networkInterfaces == null )
			return new String[] { "No network interfaces detected." };

		while ( networkInterfaces.hasMoreElements() )
		{
			NetworkInterface netif = networkInterfaces.nextElement();

			if ( NetworkUtils.isUsable( netif ) )
				list.add( NetworkUtils.getNetworkInterfaceInfo( netif ) );
		}

		if ( list.size() == 0 )
			return new String[] { "No usable network interfaces detected." };

		return list.toArray( new String[0] );
	}

	/** {@inheritDoc} */
	@Override
	public String[] showAllNetworks()
	{
		List<String> list = new ArrayList<String>();

		Enumeration<NetworkInterface> networkInterfaces = NetworkUtils.getNetworkInterfaces();

		if ( networkInterfaces == null )
			return new String[] { "No network interfaces detected." };

		while ( networkInterfaces.hasMoreElements() )
		{
			NetworkInterface netif = networkInterfaces.nextElement();
			list.add( NetworkUtils.getNetworkInterfaceInfo( netif ) );
		}

		return list.toArray( new String[0] );
	}

	/** {@inheritDoc} */
	@Override
	public void disconnect()
	{
		connectionWorker.stop();
	}

	/** {@inheritDoc} */
	@Override
	public void connect()
	{
		connectionWorker.start();
	}
}
