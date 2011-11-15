
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

package net.usikkert.kouchat.ui.swing;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.usikkert.kouchat.misc.CommandException;
import net.usikkert.kouchat.misc.CommandParser;
import net.usikkert.kouchat.misc.Controller;
import net.usikkert.kouchat.misc.MessageController;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.SoundBeeper;
import net.usikkert.kouchat.misc.Topic;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.misc.UserList;
import net.usikkert.kouchat.net.FileReceiver;
import net.usikkert.kouchat.net.FileSender;
import net.usikkert.kouchat.net.FileTransfer;
import net.usikkert.kouchat.ui.PrivateChatWindow;
import net.usikkert.kouchat.ui.UserInterface;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

/**
 * This class is a mediator for the gui, and gets all the events from the gui layer
 * that needs access to other components, or classes in lower layers. It is also
 * the interface for classes in lower layers to update the gui.
 *
 * @author Christian Ihle
 */
public class SwingMediator implements Mediator, UserInterface
{
	private final SidePanel sideP;
	private final SettingsDialog settingsDialog;
	private final KouChatFrame gui;
	private final MainPanel mainP;
	private final SysTray sysTray;
	private final MenuBar menuBar;
	private final ButtonPanel buttonP;

	private final Controller controller;
	private final Settings settings;
	private final User me;
	private final CommandParser cmdParser;
	private final SoundBeeper beeper;
	private final MessageController msgController;

	/** The image loader. */
	private final ImageLoader imageLoader;

	/**
	 * Constructor. Initializes the lower layers.
	 *
	 * @param compHandler An object with references to all the gui components this mediator works with.
	 * @param imageLoader The image loader.
	 */
	public SwingMediator( final ComponentHandler compHandler, final ImageLoader imageLoader )
	{
		Validate.notNull( compHandler, "Component handler can not be null" );
		Validate.notNull( imageLoader, "Image loader can not be null" );
		compHandler.validate();

		this.imageLoader = imageLoader;
		sideP = compHandler.getSidePanel();
		settingsDialog = compHandler.getSettingsDialog();
		gui = compHandler.getGui();
		mainP = compHandler.getMainPanel();
		sysTray = compHandler.getSysTray();
		menuBar = compHandler.getMenuBar();
		buttonP = compHandler.getButtonPanel();

		msgController = new MessageController( mainP, this );
		controller = new Controller( this );
		settings = Settings.getSettings();
		me = settings.getMe();
		cmdParser = new CommandParser( controller, this );
		beeper = new SoundBeeper();

		sideP.setUserList( controller.getUserList() );
		mainP.setAutoCompleter( controller.getAutoCompleter() );
	}

	/**
	 * Hides the main window in the system tray if a system tray is supported.
	 * Or just minimizes the window to the taskbar.
	 */
	@Override
	public void minimize()
	{
		if ( sysTray.isSystemTraySupport() )
			gui.setVisible( false );
		else
			UITools.minimize( gui );
	}

	/**
	 * Clears all the text from the main chat area.
	 */
	@Override
	public void clearChat()
	{
		mainP.clearChat();
		mainP.getMsgTF().requestFocusInWindow();
	}

	/**
	 * If the user is not away, asks for an away reason,
	 * and sets the user as away.
	 *
	 * If user is away, asks if the user wants to come back.
	 */
	@Override
	public void setAway()
	{
		if ( me.isAway() )
		{
			final int choice = UITools.showOptionDialog( "Back from '" + me.getAwayMsg() + "'?", "Away" );

			if ( choice == JOptionPane.YES_OPTION )
			{
				try
				{
					controller.changeAwayStatus( me.getCode(), false, "" );
					changeAway( false );
					msgController.showSystemMessage( "You came back" );
				}

				catch ( final CommandException e )
				{
					UITools.showWarningMessage( e.getMessage(), "Change away" );
				}
			}
		}

		else
		{
			final String reason = UITools.showInputDialog( "Reason for away?", "Away", null );

			if ( reason != null && reason.trim().length() > 0 )
			{
				if ( controller.isWrote() )
				{
					controller.changeWriting( me.getCode(), false );
					mainP.getMsgTF().setText( "" );
				}

				try
				{
					controller.changeAwayStatus( me.getCode(), true, reason );
					changeAway( true );
					msgController.showSystemMessage( "You went away: " + me.getAwayMsg() );
				}

				catch ( final CommandException e )
				{
					UITools.showWarningMessage( e.getMessage(), "Change away" );
				}
			}
		}

		mainP.getMsgTF().requestFocusInWindow();
	}

	/**
	 * Asks for the new topic, and changes it.
	 */
	@Override
	public void setTopic()
	{
		final Topic topic = controller.getTopic();
		final String newTopic = UITools.showInputDialog( "Change topic?", "Topic", topic.getTopic() );

		if ( newTopic != null )
		{
			try
			{
				cmdParser.fixTopic( newTopic );
			}

			catch ( final CommandException e )
			{
				UITools.showWarningMessage( e.getMessage(), "Change topic" );
			}
		}

		mainP.getMsgTF().requestFocusInWindow();
	}

	/**
	 * Logs on to the network.
	 */
	@Override
	public void start()
	{
		controller.logOn();
		updateTitleAndTray();
	}

	/**
	 * Asks if the user wants to quit.
	 */
	@Override
	public void quit()
	{
		final int choice = UITools.showOptionDialog( "Are you sure you want to quit?", "Quit" );

		if ( choice == JOptionPane.YES_OPTION )
		{
			System.exit( 0 );
		}
	}

	/**
	 * Updates the titlebar and the system tray tooltip with
	 * current information about the application and the user.
	 */
	@Override
	public void updateTitleAndTray()
	{
		if ( me != null )
		{
			String title = me.getNick();
			String tooltip = me.getNick();

			if ( !controller.isConnected() )
			{
				if ( controller.isLoggedOn() )
				{
					title += " - Connection lost";
					tooltip += " - Connection lost";
				}

				else
				{
					title += " - Not connected";
					tooltip += " - Not connected";
				}
			}

			else
			{
				if ( me.isAway() )
				{
					title += " (Away)";
					tooltip += " (Away)";
				}

				if ( controller.getTopic().getTopic().length() > 0 )
					title += " - Topic: " + controller.getTopic();
			}

			gui.setTitle( UITools.createTitle( title ) );
			gui.updateWindowIcon();
			sysTray.setToolTip( UITools.createTitle( title ) );
		}
	}

	/**
	 * Shows or hides the main window.
	 * The window will always be brought to front when shown.
	 */
	@Override
	public void showOrHideWindow()
	{
		if ( gui.isVisible() )
			minimize();

		else
		{
			if ( UITools.isMinimized( gui ) )
				UITools.restore( gui );

			gui.setVisible( true );
			gui.toFront();
		}
	}

	/**
	 * If the main window is hidden it is set visible,
	 * but only as minimized in the taskbar.
	 */
	@Override
	public void minimizeWindowIfHidden()
	{
		if ( !gui.isVisible() )
		{
			UITools.minimize( gui );
			gui.setVisible( true );
		}
	}

	/**
	 * Opens the settings dialog window.
	 */
	@Override
	public void showSettings()
	{
		settingsDialog.showSettings();
	}

	/**
	 * Opens a file chooser, where the user can select a file to send to
	 * another user.
	 *
	 * @param user The user to send the file to.
	 * @param selectedFile A file that already exists to open the file chooser with
	 *                     that file already selected, or <code>null</code> if the
	 *                     file chooser should start fresh.
	 */
	@Override
	public void sendFile( final User user, final File selectedFile )
	{
		if ( user == null )
			return;

		else if ( user.isMe() )
		{
			final String message = "You cannot send files to yourself.";
			UITools.showWarningMessage( message, "Warning" );
		}

		else if ( me.isAway() )
		{
			final String message = "You cannot send files while you are away.";
			UITools.showWarningMessage( message, "Warning" );
		}

		else if ( user.isAway() )
		{
			final String message = "You cannot send files to " + user.getNick() + ", which is away.";
			UITools.showWarningMessage( message, "Warning" );
		}

		else if ( !user.isOnline() )
		{
			final String message = "You cannot send files to " + user.getNick() + ", which is not online anymore.";
			UITools.showWarningMessage( message, "Warning" );
		}

		else
		{
			final JFileChooser chooser = UITools.createFileChooser( "Open" );

			if ( selectedFile != null && selectedFile.exists() )
				chooser.setSelectedFile( selectedFile );

			final int returnVal = chooser.showOpenDialog( null );

			if ( returnVal == JFileChooser.APPROVE_OPTION )
			{
				final File file = chooser.getSelectedFile().getAbsoluteFile();

				if ( file.exists() && file.isFile() )
				{
					try
					{
						cmdParser.sendFile( user, file );
					}

					catch ( final CommandException e )
					{
						UITools.showWarningMessage( e.getMessage(), "Send file" );
					}
				}
			}
		}
	}

	/**
	 * Gets the text written in the input field and either sends it to
	 * the command parser or sends it as a message.
	 */
	@Override
	public void write()
	{
		final String line = mainP.getMsgTF().getText();

		if ( line.trim().length() > 0 )
		{
			if ( line.startsWith( "/" ) )
			{
				cmdParser.parse( line );
			}

			else
			{
				try
				{
					controller.sendChatMessage( line );
					msgController.showOwnMessage( line );
				}

				catch ( final CommandException e )
				{
					msgController.showSystemMessage( e.getMessage() );
				}
			}
		}

		mainP.getMsgTF().setText( "" );
	}

	/**
	 * Gets the text from the input field of the private chat, and
	 * sends it as a message to the user.
	 *
	 * @param privchat The private chat.
	 */
	@Override
	public void writePrivate( final PrivateChatWindow privchat )
	{
		final String line = privchat.getChatText();
		final User user = privchat.getUser();

		if ( line.trim().length() > 0 )
		{
			try
			{
				controller.sendPrivateMessage( line, user );
				msgController.showPrivateOwnMessage( user, line );
			}

			catch ( final CommandException e )
			{
				msgController.showPrivateSystemMessage( user, e.getMessage() );
			}
		}

		privchat.clearChatText();
	}

	/**
	 * Shows a list of the supported commands and their syntax.
	 */
	@Override
	public void showCommands()
	{
		cmdParser.showCommands();
	}

	/**
	 * Checks if the user is currently writing, and updates the status.
	 */
	@Override
	public void updateWriting()
	{
		if ( mainP.getMsgTF().getText().length() > 0 )
		{
			if ( !controller.isWrote() )
			{
				controller.changeWriting( me.getCode(), true );
			}
		}

		else
		{
			if ( controller.isWrote() )
			{
				controller.changeWriting( me.getCode(), false );
			}
		}
	}

	/**
	 * Changes the nick name of the user, if the nick is valid.
	 *
	 * @param nick The new nick name to change to.
	 * @return If the nick name was changed successfully.
	 */
	@Override
	public boolean changeNick( final String nick )
	{
		final String trimNick = nick.trim();

		if ( !trimNick.equals( me.getNick() ) )
		{
			if ( controller.isNickInUse( trimNick ) )
			{
				UITools.showWarningMessage( "The nick is in use by someone else.", "Change nick" );
			}

			else if ( !Tools.isValidNick( trimNick ) )
			{
				final String message = "'" + trimNick + "' is not a valid nick name.\n\n"
						+ "A nick name can have between 1 and 10 characters.\nLegal characters are 'a-z',"
						+ " '0-9', '-' and '_'.";
				UITools.showWarningMessage( message, "Change nick" );
			}

			else
			{
				try
				{
					controller.changeMyNick( trimNick );
					msgController.showSystemMessage( "You changed nick to " + me.getNick() );
					updateTitleAndTray();
					return true;
				}

				catch ( final CommandException e )
				{
					UITools.showWarningMessage( e.getMessage(), "Change nick" );
				}
			}
		}

		else
		{
			return true;
		}

		return false;
	}

	/**
	 * Runs when the user presses the cancel/close button in the
	 * transfer dialog. If the button's text is close, the dialog should
	 * close. If the text is cancel, the file transfer should stop,
	 * and the button should change text to close.
	 *
	 * @param transferDialog The transfer dialog.
	 */
	@Override
	public void transferCancelled( final TransferDialog transferDialog )
	{
		if ( transferDialog.getCancelButtonText().equals( "Close" ) )
			transferDialog.dispose();

		else
		{
			transferDialog.setCancelButtonText( "Close" );
			final FileTransfer fileTransfer = transferDialog.getFileTransfer();
			cmdParser.cancelFileTransfer( fileTransfer );
		}
	}

	/**
	 * Notifies the user of a new message in different ways,
	 * depending on the state of the main chat window.
	 *
	 * <ul>
	 *   <li><i>Main chat in focus</i> - do nothing</li>
	 *   <li><i>Main chat out of focus</i> - beep, update main chat icon</li>
	 *   <li><i>Main chat hidden</i> - beep, update systray, show balloon</li>
	 * </ul>
	 */
	@Override
	public void notifyMessageArrived( final User user )
	{
		// Main chat hidden - beep, update systray, show balloon
		if ( !gui.isVisible() )
		{
			if ( me.isAway() )
				sysTray.setAwayActivityState();

			else
			{
				sysTray.setNormalActivityState();
				beeper.beep();
				sysTray.showBalloonMessage( UITools.createTitle( me.getNick() ),
						"New message from " + user.getNick() );
			}
		}

		// Main chat out of focus - beep, update main chat icon
		else if ( !gui.isFocused() )
		{
			updateTitleAndTray();

			if ( !me.isAway() )
				beeper.beep();
		}
	}

	/**
	 * Notifies the user of new private message in different ways,
	 * depending on the state of the main chat window and the private
	 * chat window.
	 *
	 * <br /><br />
	 *
	 * A private message can never be sent while the sender
	 * or receiver is away, so this method assumes that is the case.
	 *
	 * <ul>
	 *   <li><b>Main chat in focus</b></li>
	 *   <ul>
	 *     <li><i>Private chat in focus</i> - not possible</li>
	 *     <li><i>Private chat out of focus</i> - update privchat icon</li>
	 *     <li><i>Private chat hidden</i> - do nothing</li>
	 *   </ul>
	 *
	 *   <li><b>Main chat out of focus</b></li>
	 *	 <ul>
	 *     <li><i>Private chat in focus</i> - do nothing</li>
	 *	   <li><i>Private chat out of focus</i> - beep, update privchat icon</li>
	 *	   <li><i>Private chat hidden</i> - beep, update main chat icon</li>
	 *   </ul>
	 *
	 *   <li><b>Main chat hidden</b></li>
	 *   <ul>
	 *	   <li><i>Private chat in focus</i> - do nothing</li>
	 *	   <li><i>Private chat out of focus</i> - beep, update privchat icon</li>
	 *	   <li><i>Private chat hidden</i> - beep, update systray, show balloon</li>
	 *   </ul>
	 * </ul>
	 *
	 * @param user The user that sent the private message.
	 */
	@Override
	public void notifyPrivateMessageArrived( final User user )
	{
		final PrivateChatWindow privchat = user.getPrivchat();

		// Main chat hidden
		if ( !gui.isVisible() )
		{
			// Private chat hidden - beep, update systray, show balloon
			if ( !privchat.isVisible() )
			{
				sysTray.setNormalActivityState();
				beeper.beep();
				sysTray.showBalloonMessage( UITools.createTitle( me.getNick() ),
						"New private message from " + user.getNick() );
			}

			// Private chat out of focus - beep, update privchat icon
			else if ( !privchat.isFocused() )
			{
				privchat.updateUserInformation();
				beeper.beep();
			}
		}

		// Main chat out of focus
		else if ( !gui.isFocused() )
		{
			// Private chat hidden - beep, update main chat icon
			if ( !privchat.isVisible() )
			{
				me.setNewMsg( true );
				updateTitleAndTray();
				beeper.beep();
			}

			// Private chat out of focus - beep, update privchat icon
			else if ( !privchat.isFocused() )
			{
				privchat.updateUserInformation();
				beeper.beep();
			}
		}

		// Main chat in focus
		else if ( gui.isFocused() )
		{
			// Private chat out of focus - update privchat icon
			if ( privchat.isVisible() && !privchat.isFocused() )
				privchat.updateUserInformation();
		}
	}

	/**
	 * Gives a notification beep, and opens a dialog box asking if the user
	 * wants to accept a file transfer from another user.
	 *
	 * @param user The user that wants to send a file.
	 * @param fileName The name of the file.
	 * @param size The size of the file, in readable format.
	 * @return If the file was accepted or not.
	 */
	@Override
	public boolean askFileSave( final String user, final String fileName, final String size )
	{
		beeper.beep();
		final String message = user + " wants to send you the file " + fileName + " (" + size + ")\nAccept?";
		final int choice = UITools.showOptionDialog( message, "File send" );

		return choice == JOptionPane.YES_OPTION;
	}

	/**
	 * Opens a file chooser so the user can choose where to save a file
	 * another user is trying to send. Warns if the file name chosen
	 * already exists.
	 *
	 * @param fileReceiver Information about the file to save.
	 */
	@Override
	public void showFileSave( final FileReceiver fileReceiver )
	{
		final JFileChooser chooser = UITools.createFileChooser( "Save" );
		chooser.setSelectedFile( fileReceiver.getFile() );
		boolean done = false;

		while ( !done )
		{
			done = true;
			final int returnVal = chooser.showSaveDialog( null );

			if ( returnVal == JFileChooser.APPROVE_OPTION )
			{
				final File file = chooser.getSelectedFile().getAbsoluteFile();

				if ( file.exists() )
				{
					final String message = file.getName() + " already exists.\nOverwrite?";
					final int overwrite = UITools.showOptionDialog( message, "File exists" );

					if ( overwrite != JOptionPane.YES_OPTION )
					{
						done = false;
					}
				}

				if ( done )
				{
					fileReceiver.setFile( file );
					fileReceiver.accept();
				}
			}

			else
			{
				fileReceiver.reject();
			}
		}
	}

	/**
	 * Updates the titlebar and tray tooltip with current information.
	 */
	@Override
	public void showTopic()
	{
		updateTitleAndTray();
	}

	/**
	 * Creates a new {@link TransferDialog} for that {@link FileReceiver}.
	 *
	 * @param fileRes The file receiver to create a transfer dialog for.
	 */
	@Override
	public void showTransfer( final FileReceiver fileRes )
	{
		new TransferDialog( this, fileRes, imageLoader );
	}

	/**
	 * Creates a new {@link TransferDialog} for that {@link FileSender}.
	 *
	 * @param fileSend The file sender to create a transfer dialog for.
	 */
	@Override
	public void showTransfer( final FileSender fileSend )
	{
		new TransferDialog( this, fileSend, imageLoader );
	}

	/**
	 * Updates the gui components depending on the away state.
	 *
	 * @param away If away or not.
	 */
	@Override
	public void changeAway( final boolean away )
	{
		if ( away )
		{
			sysTray.setAwayState();
			mainP.getMsgTF().setEnabled( false );
			menuBar.setAwayState( true );
			buttonP.setAwayState( true );
		}

		else
		{
			sysTray.setNormalState();
			mainP.getMsgTF().setEnabled( true );
			menuBar.setAwayState( false );
			buttonP.setAwayState( false );
		}

		updateAwayInPrivChats( away );
		updateTitleAndTray();
	}

	/**
	 * Notifies the open private chat windows that the away state has changed.
	 *
	 * @param away If the user is away.
	 */
	private void updateAwayInPrivChats( final boolean away )
	{
		final UserList list = controller.getUserList();

		for ( int i = 0; i < list.size(); i++ )
		{
			final User user = list.get( i );

			if ( user.getPrivchat() != null )
			{
				if ( !user.isAway() )
				{
					user.getPrivchat().setAway( away );
				}

				if ( away )
				{
					msgController.showPrivateSystemMessage( user, "You went away: " + me.getAwayMsg() );
				}

				else
				{
					msgController.showPrivateSystemMessage( user, "You came back" );
				}
			}
		}
	}

	/**
	 * If the user does not have a private chat window already,
	 * one is created.
	 *
	 * @param user The user to create a new private chat for.
	 */
	@Override
	public void createPrivChat( final User user )
	{
		if ( user.getPrivchat() == null )
			user.setPrivchat( new PrivateChatFrame( this, user, imageLoader ) );
	}

	/**
	 * Shows the user's private chat window.
	 *
	 * @param user The user to show the private chat for.
	 */
	@Override
	public void showPrivChat( final User user )
	{
		createPrivChat( user );
		user.getPrivchat().setVisible( true );
	}

	/**
	 * Resets the new private message field of the user.
	 *
	 * @param user The user to reset the field for.
	 */
	@Override
	public void activatedPrivChat( final User user )
	{
		if ( user.isNewPrivMsg() )
		{
			user.setNewPrivMsg( false ); // In case the user has logged off
			controller.changeNewMessage( user.getCode(), false );
		}
	}

	/**
	 * Returns the message controller for swing.
	 *
	 * @return The message controller.
	 */
	@Override
	public MessageController getMessageController()
	{
		return msgController;
	}

	/**
	 * Returns if the main chat is in focus.
	 *
	 * @return If the main chat is in focus.
	 */
	@Override
	public boolean isFocused()
	{
		return gui.isFocused();
	}

	/**
	 * Returns if the main chat is visible.
	 *
	 * @return If the main chat is visible.
	 */
	@Override
	public boolean isVisible()
	{
		return gui.isVisible();
	}
}
