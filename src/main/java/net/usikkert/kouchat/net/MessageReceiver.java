
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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.event.ReceiverListener;
import net.usikkert.kouchat.misc.ErrorHandler;

/**
 * This is the thread that listens for multicast messages from
 * the network, and notifies any listeners when messages arrive.
 *
 * @author Christian Ihle
 */
public class MessageReceiver implements Runnable
{
	/** The logger. */
	private static final Logger LOG = Logger.getLogger( MessageReceiver.class.getName() );

	/** The multicast socket used for receiving messages. */
	private MulticastSocket mcSocket;

	/** The inetaddress object with the multicast ip address to receive messages from. */
	private InetAddress address;

	/** The listener getting all the messages received here. */
	private ReceiverListener listener;

	/** If connected to the network or not. */
	private boolean connected;

	/** The background thread watching for messages from the network. */
	private Thread worker;

	/** The error handler for registering important messages. */
	private final ErrorHandler errorHandler;

	/** The port to receive messages on. */
	private final int port;

	/**
	 * Default constructor.
	 *
	 * <p>Initializes the network with the default ip address and port.</p>
	 *
	 * @see Constants#NETWORK_IP
	 * @see Constants#NETWORK_CHAT_PORT
	 */
	public MessageReceiver()
	{
		this( Constants.NETWORK_IP, Constants.NETWORK_CHAT_PORT );
	}

	/**
	 * Alternative constructor.
	 *
	 * <p>Initializes the network with the given ip address and port.</p>
	 *
	 * @param ipAddress Multicast ip address to connect to.
	 * @param port Port to connect to.
	 */
	public MessageReceiver( final String ipAddress, final int port )
	{
		this.port = port;
		errorHandler = ErrorHandler.getErrorHandler();

		try
		{
			address = InetAddress.getByName( ipAddress );
		}

		catch ( final IOException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
			errorHandler.showCriticalError( "Failed to initialize the network:\n" + e + "\n"
					+ Constants.APP_NAME + " will now shutdown." );
			System.exit( 1 );
		}
	}

	/**
	 * Waits for incoming packets, and notifies the listener when they arrive.
	 */
	public void run()
	{
		while ( connected )
		{
			try
			{
				DatagramPacket packet = new DatagramPacket(
						new byte[Constants.NETWORK_PACKET_SIZE], Constants.NETWORK_PACKET_SIZE );

				if ( connected )
				{
					mcSocket.receive( packet );
					String ip = packet.getAddress().getHostAddress();
					String message = new String( packet.getData(), Constants.MESSAGE_CHARSET ).trim();
					LOG.log( Level.FINE, "Message arrived from " + ip + ": " + message );

					if ( listener != null )
						listener.messageArrived( message, ip );
				}
			}

			// Happens when socket is closed, or network is down
			catch ( final IOException e )
			{
				if ( connected )
					LOG.log( Level.WARNING, e.toString() );
				else
					LOG.log( Level.FINE, e.toString() );
			}
		}
	}

	/**
	 * Starts the thread that listens for messages.
	 */
	private void startThread()
	{
		LOG.log( Level.FINE, "Starting." );
		worker = new Thread( this, "MessageReceiverWorker" );
		worker.start();
	}

	/**
	 * Connects to the network with the given network interface, or gives
	 * the control to the operating system to choose if <code>null</code>
	 * is given.
	 *
	 * <p>Will also start a thread to continuously receive messages.</p>
	 *
	 * @param networkInterface The network interface to use, or <code>null</code>.
	 * @return If connected to the network or not.
	 */
	public synchronized boolean startReceiver( final NetworkInterface networkInterface )
	{
		LOG.log( Level.FINE, "Connecting..." );

		try
		{
			if ( connected )
			{
				LOG.log( Level.FINE, "Already connected." );
			}

			else
			{
				if ( mcSocket == null )
					mcSocket = new MulticastSocket( port );

				if ( networkInterface != null )
					mcSocket.setNetworkInterface( networkInterface );

				mcSocket.joinGroup( address );
				LOG.log( Level.FINE, "Connected to " + mcSocket.getNetworkInterface().getDisplayName() + "." );
				connected = true;
			}
		}

		catch ( final IOException e )
		{
			LOG.log( Level.SEVERE, "Could not start receiver: " + e.toString() );

			if ( mcSocket != null )
			{
				if ( !mcSocket.isClosed() )
					mcSocket.close();

				mcSocket = null;
			}
		}

		if ( connected && ( worker == null || !worker.isAlive() ) )
		{
			startThread();
		}

		return connected;
	}

	/**
	 * Disconnects from the network and closes the multicast socket.
	 */
	public synchronized void stopReceiver()
	{
		LOG.log( Level.FINE, "Disconnecting..." );

		if ( !connected )
		{
			LOG.log( Level.FINE, "Not connected." );
		}

		else
		{
			connected = false;

			try
			{
				if ( !mcSocket.isClosed() )
				{
					mcSocket.leaveGroup( address );
				}
			}

			catch ( final IOException e )
			{
				LOG.log( Level.WARNING, e.toString() );
			}

			if ( !mcSocket.isClosed() )
			{
				mcSocket.close();
				mcSocket = null;
			}

			LOG.log( Level.FINE, "Disconnected." );
		}
	}

	/**
	 * Registers as the listener to receive all the messages from
	 * the network.
	 *
	 * @param listener The listener to register.
	 */
	public void registerReceiverListener( final ReceiverListener listener )
	{
		this.listener = listener;
	}
}
