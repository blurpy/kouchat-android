
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.misc.UserList;
import net.usikkert.kouchat.util.Tools;
import net.usikkert.kouchat.util.Validate;

/**
 * This is the complete right side panel of the application.
 * It consists of the user list, and the button panel.
 *
 * @author Christian Ihle
 */
public class SidePanel extends JPanel implements ActionListener, MouseListener, FileDropSource
{
	/** The standard version UID. */
	private static final long serialVersionUID = 1L;

	/** The right click popup menu in the user list. */
	private final JPopupMenu userMenu;

	/** The information menu item. */
	private final JMenuItem infoMI;

	/** The send file menu item. */
	private final JMenuItem sendfileMI;

	/** The private chat menu item. */
	private final JMenuItem privchatMI;

	/** The user list. */
	private final JList userL;

	/** The application user. */
	private final User me;

	/** Handles drag and drop of files on users. */
	private final FileTransferHandler fileTransferHandler;

	/** Custom model for the user list. */
	private UserListModel userListModel;

	/** The mediator. */
	private Mediator mediator;

	/**
	 * Constructor. Creates the panel.
	 *
	 * @param buttonP The button panel.
	 * @param imageLoader The image loader.
	 */
	public SidePanel( final ButtonPanel buttonP, final ImageLoader imageLoader )
	{
		Validate.notNull( buttonP, "Button panel can not be null" );
		Validate.notNull( imageLoader, "Image loader can not be null" );

		setLayout( new BorderLayout( 2, 2 ) );

		fileTransferHandler = new FileTransferHandler( this );
		userL = new JList();
		userL.setCellRenderer( new UserListCellRenderer( imageLoader ) );
		userL.addMouseListener( this );
		userL.setTransferHandler( fileTransferHandler );
		userL.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		JScrollPane userSP = new JScrollPane( userL );

		add( userSP, BorderLayout.CENTER );
		add( buttonP, BorderLayout.SOUTH );

		userMenu = new JPopupMenu();
		infoMI = new JMenuItem( "Information" );
		infoMI.setMnemonic( 'I' );
		infoMI.addActionListener( this );
		sendfileMI = new JMenuItem( "Send file" );
		sendfileMI.setMnemonic( 'S' );
		sendfileMI.addActionListener( this );
		privchatMI = new JMenuItem( "Private chat" );
		privchatMI.setMnemonic( 'P' );
		privchatMI.addActionListener( this );
		privchatMI.setFont( privchatMI.getFont().deriveFont( Font.BOLD ) ); // default menu item
		userMenu.add( infoMI );
		userMenu.add( sendfileMI );
		userMenu.add( privchatMI );

		setPreferredSize( new Dimension( 114, 0 ) );
		me = Settings.getSettings().getMe();
	}

	/**
	 * Sets the mediator.
	 *
	 * @param mediator The mediator to set.
	 */
	public void setMediator( final Mediator mediator )
	{
		Validate.notNull( mediator, "Mediator can not be null" );

		this.mediator = mediator;
		fileTransferHandler.setMediator( mediator );
	}

	/**
	 * Sets the user list implementation in the user list model.
	 *
	 * @param userList The user list to set.
	 */
	public void setUserList( final UserList userList )
	{
		Validate.notNull( userList, "User list can not be null" );

		userListModel = new UserListModel( userList );
		userL.setModel( userListModel );
	}

	/**
	 * Gets the currently selected user.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public User getUser()
	{
		return (User) userL.getSelectedValue();
	}

	/**
	 * Gets the user list.
	 *
	 * @return The user list.
	 */
	public JList getUserList()
	{
		return userL;
	}

	/**
	 * Handles action events on the right click popup menu on the users.
	 *
	 * <p>Currently:</p>
	 * <ul>
	 *   <li>Information</li>
	 *   <li>Send file</li>
	 *   <li>Private chat</li>
	 * </ul>
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed( final ActionEvent e )
	{
		if ( e.getSource() == infoMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					User user = userListModel.getElementAt( userL.getSelectedIndex() );
					String info = "Information about " + user.getNick();

					if ( user.isAway() )
						info += " (Away)";

					info += ".\n\nIP address: " + user.getIpAddress();

					if ( user.getHostName() != null )
						info +=  "\nHost name: " + user.getHostName();

					info += "\nClient: " + user.getClient()
							+ "\nOperating System: " + user.getOperatingSystem()
							+ "\n\nOnline: " + Tools.howLongFromNow( user.getLogonTime() );

					if ( user.isAway() )
						info += "\nAway message: " + user.getAwayMsg();

					UITools.showInfoMessage( info, "Info" );
				}
			} );
		}

		else if ( e.getSource() == sendfileMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					mediator.sendFile( getUser(), null );
				}
			} );
		}

		else if ( e.getSource() == privchatMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					User user = userListModel.getElementAt( userL.getSelectedIndex() );
					mediator.showPrivChat( user );
				}
			} );
		}
	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void mouseClicked( final MouseEvent e )
	{

	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void mouseEntered( final MouseEvent e )
	{

	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void mouseExited( final MouseEvent e )
	{

	}

	/**
	 * Handles mouse pressed events on the user list.
	 *
	 * <p>If a mouse click happens on a user the user is selected,
	 * else the currently selected user is unselected.</p>
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void mousePressed( final MouseEvent e )
	{
		if ( e.getSource() == userL )
		{
			Point p = e.getPoint();
			int index = userL.locationToIndex( p );

			if ( index != -1 )
			{
				Rectangle r = userL.getCellBounds( index, index );

				if ( r.x <= p.x && p.x <= r.x + r.width && r.y <= p.y && p.y <= r.y + r.height )
				{
					userL.setSelectedIndex( index );
				}

				else
				{
					userL.clearSelection();
				}
			}
		}
	}

	/**
	 * Handles mouse released events on the user list.
	 *
	 * <p>Decides which menu items to show on right click on a user,
	 * and opens a private chat with the selected user on double click.</p>
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void mouseReleased( final MouseEvent e )
	{
		if ( e.getSource() == userL )
		{
			// Right click
			if ( userMenu.isPopupTrigger( e ) && userL.getSelectedIndex() != -1 )
			{
				User temp = userListModel.getElementAt( userL.getSelectedIndex() );

				if ( temp.isMe() )
				{
					sendfileMI.setVisible( false );
					privchatMI.setVisible( false );
				}

				else if ( temp.isAway() || me.isAway() )
				{
					sendfileMI.setVisible( true );
					sendfileMI.setEnabled( false );
					privchatMI.setVisible( true );

					if ( temp.getPrivateChatPort() == 0 )
						privchatMI.setEnabled( false );
					else
						privchatMI.setEnabled( true );
				}

				else
				{
					sendfileMI.setVisible( true );
					sendfileMI.setEnabled( true );
					privchatMI.setVisible( true );

					if ( temp.getPrivateChatPort() == 0 )
						privchatMI.setEnabled( false );
					else
						privchatMI.setEnabled( true );
				}

				userMenu.show( userL, e.getX(), e.getY() );
			}

			// Double left click
			else if ( e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && userL.getSelectedIndex() != -1 )
			{
				User user = userListModel.getElementAt( userL.getSelectedIndex() );

				if ( user != me && user.getPrivateChatPort() != 0 )
					mediator.showPrivChat( user );
			}
		}
	}
}
