
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.event.NetworkConnectionListener;

/**
 * This thread is responsible for keeping the application connected
 * to the network.
 *
 * Every now and then, the thread will check if there are better
 * networks available, and reconnect to that network instead.
 *
 * @author Christian Ihle
 */
public class ConnectionWorker implements Runnable
{
	/** The logger. */
	private static final Logger LOG = Logger.getLogger( ConnectionWorker.class.getName() );

	/** Period of time to sleep if network is up. 60 sec. */
	private static final int SLEEP_UP = 1000 * 60;

	/** Period of time to sleep if network is down. 15 sec. */
	private static final int SLEEP_DOWN = 1000 * 15;

	/** Indicates whether the thread should run or not. */
	private boolean run;

	/** Whether the network is up or not. */
	private boolean networkUp;

	/** The current network interface. */
	private NetworkInterface networkInterface;

	/** The working thread. */
	private Thread worker;

	/** A list of connection listeners. */
	private final List<NetworkConnectionListener> listeners;

	/** For locating the operating system's choice of network interface. */
	private final OperatingSystemNetworkInfo osNetworkInfo;

	/**
	 * Constructor.
	 */
	public ConnectionWorker()
	{
		listeners = new ArrayList<NetworkConnectionListener>();
		osNetworkInfo = new OperatingSystemNetworkInfo();
	}

	/**
	 * The thread. See {@link #updateNetwork()} for details.
	 */
	@Override
	public void run()
	{
		LOG.log( Level.FINE, "Network is starting" );

		while ( run )
		{
			boolean networkUp = updateNetwork();

			try
			{
				if ( networkUp )
					Thread.sleep( SLEEP_UP );
				else
					Thread.sleep( SLEEP_DOWN );
			}

			// Sleep interrupted - probably from stop() or checkNetwork()
			catch ( final InterruptedException e )
			{
				LOG.log( Level.FINE, e.toString() );
			}
		}

		LOG.log( Level.FINE, "Network is stopping" );

		if ( networkUp )
			notifyNetworkDown( false );

		networkInterface = null;
	}

	/**
	 * Asks the thread to check the network now to detect loss of network connectivity.
	 */
	public void checkNetwork()
	{
		if ( worker != null )
			worker.interrupt();
	}

	/**
	 * Checks the state of the network, and tries to keep the best possible
	 * network connection up. Listeners are notified of any changes.
	 *
	 * @return If the network is up or not after this update is done.
	 */
	private synchronized boolean updateNetwork()
	{
		NetworkInterface netif = selectNetworkInterface();

		// No network interface to connect with
		if ( !NetworkUtils.isUsable( netif ) )
		{
			LOG.log( Level.FINE, "Network is down" );

			if ( networkUp )
				notifyNetworkDown( false );

			return false;
		}

		// Switching network interface, like going from cable to wireless
		else if ( isNewNetworkInterface( netif ) )
		{
			String origNetwork = networkInterface == null ? "[null]" : networkInterface.getName();
			LOG.log( Level.FINE, "Changing network from " + origNetwork + " to " + netif.getName() );
			networkInterface = netif;

			if ( networkUp )
			{
				notifyNetworkDown( true );
				notifyNetworkUp( true );
			}

			else
				notifyNetworkUp( false );
		}

		// If the connection was lost, like unplugging cable, and plugging back in
		else if ( !networkUp )
		{
			LOG.log( Level.FINE, "Network " + netif.getName() + " is up again" );
			networkInterface = netif;
			notifyNetworkUp( false );
		}

		// Else, the old connection is still up

		return true;
	}

	/**
	 * Compares <code>netif</code> with the current network interface.
	 *
	 * @param netif The new network interface to compare against the original.
	 * @return True if netif is new.
	 */
	private boolean isNewNetworkInterface( final NetworkInterface netif )
	{
		return !NetworkUtils.sameNetworkInterface( netif, networkInterface );
	}

	/**
	 * Notifies all the listeners that the network is up.
	 *
	 * @param silent Don't give any messages to the user about the change.
	 */
	private synchronized void notifyNetworkUp( final boolean silent )
	{
		networkUp = true;

		for ( NetworkConnectionListener listener : listeners )
		{
			listener.networkCameUp( silent );
		}
	}

	/**
	 * Notifies all the listeners that the network is down.
	 *
	 * @param silent Don't give any messages to the user about the change.
	 */
	private synchronized void notifyNetworkDown( final boolean silent )
	{
		networkUp = false;

		for ( NetworkConnectionListener listener : listeners )
		{
			listener.networkWentDown( silent );
		}
	}

	/**
	 * Registers the listener as a connection listener.
	 *
	 * @param listener The listener to register.
	 */
	public void registerNetworkConnectionListener( final NetworkConnectionListener listener )
	{
		listeners.add( listener );
	}

	/**
	 * Starts a new thread if no thread is already running.
	 */
	public synchronized void start()
	{
		if ( !run && !isAlive() )
		{
			run = true;
			worker = new Thread( this, "ConnectionWorker" );
			worker.start();
		}
	}

	/**
	 * Stops the thread.
	 */
	public void stop()
	{
		run = false;

		if ( worker != null )
			worker.interrupt();
	}

	/**
	 * Locates the best network interface to use.
	 *
	 * <p>The operating system's choice of network interface is prioritized,
	 * but if the interface is not seen as usable, then the first usable
	 * interface in the list of available choices is used instead.</p>
	 *
	 * <p>If no usable network interfaces are found, then <code>null</code>
	 * is returned.</p>
	 *
	 * @return The network interface found, or <code>null</code>.
	 * @see NetworkUtils#isUsable(NetworkInterface)
	 */
	private NetworkInterface selectNetworkInterface()
	{
		NetworkInterface firstUsableNetIf = NetworkUtils.findFirstUsableNetworkInterface();

		if ( firstUsableNetIf == null )
		{
			LOG.log( Level.FINER, "No usable network interface detected." );
			return null;
		}

		NetworkInterface osNetIf = osNetworkInfo.getOperatingSystemNetworkInterface();

		if ( NetworkUtils.isUsable( osNetIf ) )
		{
			LOG.log( Level.FINER, "Using operating system's choice of network interface." );
			return osNetIf;
		}

		LOG.log( Level.FINER, "Overriding operating system's choice of network interface." );
		return firstUsableNetIf;
	}

	/**
	 * Finds the current network interface.
	 *
	 * @return The current network interface.
	 */
	public NetworkInterface getCurrentNetworkInterface()
	{
		NetworkInterface updatedNetworkInterface =
			NetworkUtils.getUpdatedNetworkInterface( networkInterface );

		if ( updatedNetworkInterface != null )
			return updatedNetworkInterface;

		return networkInterface;
	}

	/**
	 * Checks if the network is up.
	 *
	 * @return If the network is up.
	 */
	public boolean isNetworkUp()
	{
		return networkUp;
	}

	/**
	 * Checks if the thread is alive.
	 *
	 * @return If the thread is alive.
	 */
	public boolean isAlive()
	{
		if ( worker == null )
			return false;
		else
			return worker.isAlive();
	}
}
