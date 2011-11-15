
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

import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.event.ReceiverListener;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;

/**
 * This class listens for udp messages from the network,
 * and parses them into a format the {@link PrivateMessageResponder} can use.
 *
 * <p>The supported message types:</p>
 *
 * <ul>
 *   <li>PRIVMSG</li>
 * </ul>
 *
 * @author Christian Ihle
 */
public class PrivateMessageParser implements ReceiverListener
{
	/** The logger. */
	private static final Logger LOG = Logger.getLogger( PrivateMessageParser.class.getName() );

	private final Settings settings;
	private final PrivateMessageResponder privmsgResponder;

	/**
	 * Constructor.
	 *
	 * @param privmsgResponder The private message responder.
	 */
	public PrivateMessageParser( final PrivateMessageResponder privmsgResponder )
	{
		this.privmsgResponder = privmsgResponder;
		settings = Settings.getSettings();
	}

	/**
	 * Parses raw udp messages from the network, and gives
	 * the result to the message responder.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void messageArrived( final String message, final String ipAddress )
	{
		try
		{
			int exclamation = message.indexOf( "!" );
			int hash = message.indexOf( "#" );
			int colon = message.indexOf( ":" );

			int fromCode = Integer.parseInt( message.substring( 0, exclamation ) );

			String type = message.substring( exclamation + 1, hash );
			String msg = message.substring( colon + 1, message.length() );

			int leftPara = msg.indexOf( "(" );
			int rightPara = msg.indexOf( ")" );
			int toCode = Integer.parseInt( msg.substring( leftPara + 1, rightPara ) );

			User tempme = settings.getMe();

			if ( fromCode != tempme.getCode() && toCode == tempme.getCode() )
			{
				if ( type.equals( "PRIVMSG" ) )
				{
					int leftBracket = msg.indexOf( "[" );
					int rightBracket = msg.indexOf( "]" );
					int rgb = Integer.parseInt( msg.substring( leftBracket + 1, rightBracket ) );
					String privmsg = msg.substring( rightBracket + 1, msg.length() );

					privmsgResponder.messageArrived( fromCode, privmsg, rgb );
				}
			}
		}

		// Just ignore, someone sent a badly formatted message
		catch ( final StringIndexOutOfBoundsException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
		}

		// Just ignore, someone sent a badly formatted message
		catch ( final NumberFormatException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
		}
	}
}
