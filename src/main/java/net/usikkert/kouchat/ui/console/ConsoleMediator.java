
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

package net.usikkert.kouchat.ui.console;

import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.Tools;

/**
 * This class is the binding between the controller and the console ui.
 *
 * @author Christian Ihle
 */
public class ConsoleMediator implements UserInterface
{
	/** For showing messages in the ui. */
	private final MessageController msgController;

	/** The controller, for access to lower layer functionality. */
	private final Controller controller;

	/**
	 * Constructor.
	 * Initializes the lower layers, and starts the input loop thread.
	 */
	public ConsoleMediator()
	{
		final ConsoleChatWindow chat = new ConsoleChatWindow();
		msgController = new MessageController( chat, this );
		controller = new Controller( this );
		final ConsoleInput ci = new ConsoleInput( controller, this );
		ci.start();
	}

	/**
	 * Will log on to the network.
	 */
	public void start()
	{
		controller.logOn();
	}

	/**
	 * Shows information about how to save the file, then returns true.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public boolean askFileSave( final String user, final String fileName, final String size )
	{
		msgController.showSystemMessage( "/receive or /reject the file" );
		return true;
	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void changeAway( final boolean away )
	{

	}

	/**
	 * Shows a message that says this is not supported.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void clearChat()
	{
		msgController.showSystemMessage( "Clear chat is not supported in console mode" );
	}

	/**
	 * Waits until the user has accepted or rejected the file transfer, and then returns.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void showFileSave( final FileReceiver fileReceiver )
	{
		while ( !fileReceiver.isAccepted() && !fileReceiver.isRejected() && !fileReceiver.isCanceled() )
		{
			Tools.sleep( 500 );
		}
	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void showTopic()
	{

	}

	/**
	 * Creates a new {@link TransferHandler}.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void showTransfer( final FileReceiver fileRes )
	{
		new TransferHandler( fileRes, msgController );
	}

	/**
	 * Creates a new {@link TransferHandler}.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void showTransfer( final FileSender fileSend )
	{
		new TransferHandler( fileSend, msgController );
	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void notifyMessageArrived( final User user )
	{

	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void notifyPrivateMessageArrived( final User user )
	{

	}

	/**
	 * If the user does not have a private chat yet,
	 * a new {@link PrivateChatConsole} is created.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void createPrivChat( final User user )
	{
		if ( user.getPrivchat() == null )
			user.setPrivchat( new PrivateChatConsole( user ) );
	}

	/**
	 * Returns the message controller for console mode.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public MessageController getMessageController()
	{
		return msgController;
	}

	/**
	 * Will always return true.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFocused()
	{
		return true;
	}

	/**
	 * Will always return true.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public boolean isVisible()
	{
		return true;
	}

	/**
	 * Quits the application.
	 */
	@Override
	public void quit()
	{
		System.exit( 0 );
	}
}
