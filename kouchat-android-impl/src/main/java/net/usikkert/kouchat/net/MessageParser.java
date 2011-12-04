
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
 * This class listens for multicast messages from the network,
 * and parses them into a format the {@link MessageResponder} can use.
 *
 * <p>The supported message types:</p>
 *
 * <ul>
 *   <li>MSG</li>
 *   <li>LOGON</li>
 *   <li>EXPOSING</li>
 *   <li>LOGOFF</li>
 *   <li>AWAY</li>
 *   <li>BACK</li>
 *   <li>EXPOSE</li>
 *   <li>NICKCRASH</li>
 *   <li>WRITING</li>
 *   <li>STOPPEDWRITING</li>
 *   <li>GETTOPIC</li>
 *   <li>TOPIC</li>
 *   <li>NICK</li>
 *   <li>IDLE</li>
 *   <li>SENDFILEACCEPT</li>
 *   <li>SENDFILEABORT</li>
 *   <li>SENDFILE</li>
 *   <li>CLIENT</li>
 * </ul>
 *
 * @author Christian Ihle
 */
public class MessageParser implements ReceiverListener
{
	/** The logger. */
	private static final Logger LOG = Logger.getLogger( MessageParser.class.getName() );

	/** To handle the different kind of messages parsed here. */
	private final MessageResponder responder;

	/** The application settings. */
	private final Settings settings;

	/** If logged on to the chat or not. */
	private boolean loggedOn;

	/**
	 * Constructor.
	 *
	 * @param responder To handle the different kind of messages parsed here.
	 */
	public MessageParser( final MessageResponder responder )
	{
		this.responder = responder;
		settings = Settings.getSettings();
	}

	/**
	 * The parser. Checks what kind of message it is,
	 * and then gives the correct data to the responder for
	 * more processing.
	 *
	 * @param message The raw message to parse.
	 * @param ipAddress The IP address of the user who sent the message.
	 */
	@Override
	public void messageArrived( final String message, final String ipAddress )
	{
		try
		{
			int exclamation = message.indexOf( "!" );
			int hash = message.indexOf( "#" );
			int colon = message.indexOf( ":" );

			int msgCode = Integer.parseInt( message.substring( 0, exclamation ) );
			String type = message.substring( exclamation + 1, hash );
			String msgNick = message.substring( hash + 1, colon );
			String msg = message.substring( colon + 1, message.length() );

			User tempme = settings.getMe();

			if ( msgCode != tempme.getCode() && loggedOn )
			{
				if ( type.equals( "MSG" ) )
				{
					int leftBracket = msg.indexOf( "[" );
					int rightBracket = msg.indexOf( "]" );
					int rgb = Integer.parseInt( msg.substring( leftBracket + 1, rightBracket ) );

					responder.messageArrived( msgCode, msg.substring( rightBracket + 1, msg.length() ), rgb );
				}

				else if ( type.equals( "LOGON" ) )
				{
					User newUser = new User( msgNick, msgCode );
					newUser.setIpAddress( ipAddress );
					newUser.setLastIdle( System.currentTimeMillis() );
					newUser.setLogonTime( System.currentTimeMillis() );

					responder.userLogOn( newUser );
				}

				else if ( type.equals( "EXPOSING" ) )
				{
					User user = new User( msgNick, msgCode );
					user.setIpAddress( ipAddress );
					user.setAwayMsg( msg );

					if ( msg.length() > 0 )
						user.setAway( true );

					user.setLastIdle( System.currentTimeMillis() );
					user.setLogonTime( System.currentTimeMillis() );

					responder.userExposing( user );
				}

				else if ( type.equals( "LOGOFF" ) )
				{
					responder.userLogOff( msgCode );
				}

				else if ( type.equals( "AWAY" ) )
				{
					responder.awayChanged( msgCode, true, msg );
				}

				else if ( type.equals( "BACK" ) )
				{
					responder.awayChanged( msgCode, false, "" );
				}

				else if ( type.equals( "EXPOSE" ) )
				{
					responder.exposeRequested();
				}

				else if ( type.equals( "NICKCRASH" ) )
				{
					if ( tempme.getNick().equals( msg ) )
					{
						responder.nickCrash();
					}
				}

				else if ( type.equals( "WRITING" ) )
				{
					responder.writingChanged( msgCode, true );
				}

				else if ( type.equals( "STOPPEDWRITING" ) )
				{
					responder.writingChanged( msgCode, false );
				}

				else if ( type.equals( "GETTOPIC" ) )
				{
					responder.topicRequested();
				}

				else if ( type.equals( "TOPIC" ) )
				{
					int leftBracket = msg.indexOf( "[" );
					int rightBracket = msg.indexOf( "]" );
					int leftPara = msg.indexOf( "(" );
					int rightPara = msg.indexOf( ")" );

					if ( rightBracket != -1 && leftBracket != -1 )
					{
						String theNick = msg.substring( leftPara + 1, rightPara );
						long theTime = Long.parseLong( msg.substring( leftBracket + 1, rightBracket ) );
						String theTopic = null;

						if ( msg.length() > rightBracket + 1 )
						{
							theTopic = msg.substring( rightBracket + 1, msg.length() );
						}

						responder.topicChanged( msgCode, theTopic, theNick, theTime );
					}
				}

				else if ( type.equals( "NICK" ) )
				{
					responder.nickChanged( msgCode, msgNick );
				}

				else if ( type.equals( "IDLE" ) )
				{
					responder.userIdle( msgCode, ipAddress );
				}

				else if ( type.equals( "SENDFILEACCEPT" ) )
				{
					int leftPara = msg.indexOf( "(" );
					int rightPara = msg.indexOf( ")" );
					int fileCode = Integer.parseInt( msg.substring( leftPara + 1, rightPara ) );

					if ( fileCode == tempme.getCode() )
					{
						int leftCurly = msg.indexOf( "{" );
						int rightCurly = msg.indexOf( "}" );
						int leftBracket = msg.indexOf( "[" );
						int rightBracket = msg.indexOf( "]" );
						int port = Integer.parseInt( msg.substring( leftBracket + 1, rightBracket ) );
						int fileHash = Integer.parseInt( msg.substring( leftCurly + 1, rightCurly ) );
						String fileName = msg.substring( rightCurly + 1, msg.length() );

						responder.fileSendAccepted( msgCode, fileName, fileHash, port );
					}
				}

				else if ( type.equals( "SENDFILEABORT" ) )
				{
					int leftPara = msg.indexOf( "(" );
					int rightPara = msg.indexOf( ")" );
					int fileCode = Integer.parseInt( msg.substring( leftPara + 1, rightPara ) );

					if ( fileCode == tempme.getCode() )
					{
						int leftCurly = msg.indexOf( "{" );
						int rightCurly = msg.indexOf( "}" );
						String fileName = msg.substring( rightCurly + 1, msg.length() );
						int fileHash = Integer.parseInt( msg.substring( leftCurly + 1, rightCurly ) );

						responder.fileSendAborted( msgCode, fileName, fileHash );
					}
				}

				else if ( type.equals( "SENDFILE" ) )
				{
					int leftPara = msg.indexOf( "(" );
					int rightPara = msg.indexOf( ")" );
					int fileCode = Integer.parseInt( msg.substring( leftPara + 1, rightPara ) );

					if ( fileCode == tempme.getCode() )
					{
						int leftCurly = msg.indexOf( "{" );
						int rightCurly = msg.indexOf( "}" );
						int leftBracket = msg.indexOf( "[" );
						int rightBracket = msg.indexOf( "]" );
						long byteSize = Long.parseLong( msg.substring( leftBracket + 1, rightBracket ) );
						String fileName = msg.substring( rightCurly + 1, msg.length() );
						int fileHash = Integer.parseInt( msg.substring( leftCurly + 1, rightCurly ) );

						responder.fileSend( msgCode, byteSize, fileName, msgNick, fileHash );
					}
				}

				else if ( type.equals( "CLIENT" ) )
				{
					int leftPara = msg.indexOf( "(" );
					int rightPara = msg.indexOf( ")" );
					int leftBracket = msg.indexOf( "[" );
					int rightBracket = msg.indexOf( "]" );
					int leftCurly = msg.indexOf( "{" );
					int rightCurly = msg.indexOf( "}" );
					int lessThan = msg.indexOf( "<" );
					int greaterThan = msg.indexOf( ">" );

					String client = msg.substring( leftPara + 1, rightPara );
					long timeSinceLogon = Long.parseLong( msg.substring( leftBracket + 1, rightBracket ) );
					String operatingSystem = msg.substring( leftCurly + 1, rightCurly );

					int privateChatPort = 0;

					try
					{
						privateChatPort = Integer.parseInt( msg.substring( lessThan + 1, greaterThan ) );
					}

					catch ( final NumberFormatException e )
					{
						LOG.log( Level.WARNING, e.toString() );
					}

					responder.clientInfo( msgCode, client, timeSinceLogon, operatingSystem, privateChatPort );
				}
			}

			else if ( msgCode == tempme.getCode() && type.equals( "LOGON" ) )
			{
				responder.meLogOn( ipAddress );
				loggedOn = true;
			}

			else if ( msgCode == tempme.getCode() && type.equals( "IDLE" ) && loggedOn )
			{
				responder.meIdle( ipAddress );
			}
		}

		catch ( final StringIndexOutOfBoundsException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
		}

		catch ( final NumberFormatException e )
		{
			LOG.log( Level.SEVERE, e.toString(), e );
		}
	}
}