
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import org.junit.Test;

/**
 * Test of {@link NetworkUtils}.
 *
 * @author Christian Ihle
 */
public class NetworkUtilsTest
{
	/**
	 * Tests if 2 network interfaces are the same.
	 *
	 * <p>NetworkInterface is a final class, and can't be mocked easily.
	 * So this test will only work when there are at least 2 network interfaces available.</p>
	 */
	@Test
	public void testSameNetworkInterface()
	{
		assertFalse( NetworkUtils.sameNetworkInterface( null, null ) );

		Enumeration<NetworkInterface> networkInterfaces = NetworkUtils.getNetworkInterfaces();

		if ( networkInterfaces != null )
		{
			try
			{
				NetworkInterface interface1 = networkInterfaces.nextElement();
				NetworkInterface interface2 = networkInterfaces.nextElement();

				assertTrue( NetworkUtils.sameNetworkInterface( interface1, interface1 ) );
				assertTrue( NetworkUtils.sameNetworkInterface( interface2, interface2 ) );

				assertFalse( NetworkUtils.sameNetworkInterface( interface1, interface2 ) );
				assertFalse( NetworkUtils.sameNetworkInterface( interface1, null ) );
				assertFalse( NetworkUtils.sameNetworkInterface( null, interface2 ) );
			}

			catch ( final NoSuchElementException e )
			{
				System.err.println( "Not enough network interfaces - aborting test" );
			}
		}

		else
			System.err.println( "No network interfaces - aborting test" );
	}

	/**
	 * Tests that the hostname returned is the correct name of the localhost.
	 */
	@Test
	public void testGetLocalHostName()
	{
		try
		{
			InetAddress localHostAddress = InetAddress.getLocalHost(); // Could throw exception

			String localHostName = NetworkUtils.getLocalHostName();
			assertNotNull( "Name of localhost should not be null", localHostName );
			InetAddress addressByName = InetAddress.getByName( localHostName );
			assertEquals( "The addresses should be equal", localHostAddress, addressByName );
		}

		catch ( final UnknownHostException e )
		{
			System.err.println( "Could not get localhost - aborting test: " + e.toString() );
		}
	}
}
