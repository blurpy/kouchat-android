
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

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.util.Validate;

/**
 * This is the system tray.
 *
 * <p>Contains an icon that changes color when there is
 * activity from other users, and a right click menu to exit
 * the application.</p>
 *
 * @author Christian Ihle
 */
public class SysTray implements ActionListener, MouseListener, PropertyChangeListener
{
	/** The logger. */
	private static final Logger LOG = Logger.getLogger( SysTray.class.getName() );

	/** The icon in the system tray. */
	private TrayIcon trayIcon;

	/** The quit menu item. */
	private MenuItem quitMI;

	/** The mediator. */
	private Mediator mediator;

	/** If the system tray is supported or not. */
	private boolean systemTraySupported;

	/** The icons to use in the system tray. */
	private StatusIcons statusIcons;

	/**
	 * Constructor. Activates the system tray icon if it's supported.
	 *
	 * @param imageLoader The image loader.
	 */
	public SysTray( final ImageLoader imageLoader )
	{
		Validate.notNull( imageLoader, "Image loader can not be null" );

		if ( SystemTray.isSupported() )
		{
			statusIcons = new StatusIcons( imageLoader );
			final PopupMenu menu = new PopupMenu();
			quitMI = new MenuItem( "Quit" );
			quitMI.addActionListener( this );
			menu.add( quitMI );

			final SystemTray sysTray = SystemTray.getSystemTray();
			trayIcon = new TrayIcon( statusIcons.getNormalIcon(), "", menu );
			trayIcon.setImageAutoSize( true );
			trayIcon.addMouseListener( this );
			trayIcon.setToolTip( Constants.APP_NAME );

			try
			{
				sysTray.add( trayIcon );
				sysTray.addPropertyChangeListener( "trayIcons", this );
				systemTraySupported = true;
			}

			catch ( final AWTException e )
			{
				// This may happen if the System Tray is hidden on a system
				// that actually supports a System Tray.
				LOG.log( Level.SEVERE, e.toString() );
			}
		}

		else
		{
			LOG.log( Level.SEVERE, "System Tray is not supported. Deactivating System Tray support." );
		}
	}

	/**
	 * Returns if the system tray is supported and activated.
	 *
	 * @return If the system tray is supported
	 */
	public boolean isSystemTraySupport()
	{
		return systemTraySupported;
	}

	/**
	 * Sets the mediator.
	 *
	 * @param mediator The mediator.
	 */
	public void setMediator( final Mediator mediator )
	{
		this.mediator = mediator;
	}

	/**
	 * Sets the tray icon to away.
	 */
	public void setAwayState()
	{
		if ( trayIcon != null )
		{
			setTrayIcon( statusIcons.getAwayIcon() );
		}
	}

	/**
	 * Sets the tray icon to away with activity.
	 */
	public void setAwayActivityState()
	{
		if ( trayIcon != null )
		{
			setTrayIcon( statusIcons.getAwayActivityIcon() );
		}
	}

	/**
	 * Sets the tray icon to normal.
	 */
	public void setNormalState()
	{
		if ( trayIcon != null )
		{
			setTrayIcon( statusIcons.getNormalIcon() );
		}
	}

	/**
	 * Sets the tray icon to normal with activity.
	 */
	public void setNormalActivityState()
	{
		if ( trayIcon != null )
		{
			setTrayIcon( statusIcons.getNormalActivityIcon() );
		}
	}

	/**
	 * Sets the tooltip on the system tray icon.
	 *
	 * @param toolTip The tooltip to set.
	 */
	public void setToolTip( final String toolTip )
	{
		if ( trayIcon != null )
			trayIcon.setToolTip( toolTip );
	}

	/**
	 * Handles clicks on the quit menu item.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed( final ActionEvent e )
	{
		if ( e.getSource() == quitMI )
		{
			mediator.quit();
		}
	}

	/**
	 * Handles left mouse click events.
	 *
	 * <p>Makes sure the main chat window is shown or hidden,
	 * and updates the tray icon.</p>
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void mouseClicked( final MouseEvent e )
	{
		if ( e.getSource() == trayIcon && e.getButton() == MouseEvent.BUTTON1 )
		{
			if ( trayIcon.getImage() == statusIcons.getNormalActivityIcon() )
				trayIcon.setImage( statusIcons.getNormalIcon() );

			else if ( trayIcon.getImage() == statusIcons.getAwayActivityIcon() )
				trayIcon.setImage( statusIcons.getAwayIcon() );

			mediator.showOrHideWindow();
		}
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
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void mousePressed( final MouseEvent e )
	{

	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void mouseReleased( final MouseEvent e )
	{

	}

	/**
	 * Sets the system tray icon if it's different from the icon already in use.
	 *
	 * @param icon The tray icon to use.
	 */
	public void setTrayIcon( final Image icon )
	{
		if ( trayIcon.getImage() != icon )
			trayIcon.setImage( icon );
	}

	/**
	 * Listens for changes to the available system tray icons.
	 * After the initial icon has been added there will never be any
	 * more changes made by the application. So if this event happens,
	 * it means that an icon was removed by the jvm, and that is
	 * usually caused by the system tray being removed.
	 *
	 * <p>To handle this, the system tray support is deactivated and
	 * the main window is unhidden, so it's possible for the user
	 * to continue without having to kill the application.</p>
	 *
	 * @param e A trayicon change event.
	 */
	@Override
	public void propertyChange( final PropertyChangeEvent e )
	{
		final TrayIcon[] icons = (TrayIcon[]) e.getNewValue();

		if ( icons.length == 0 )
		{
			LOG.log( Level.SEVERE, "System Tray removed. Deactivating System Tray support." );
			systemTraySupported = false;
			mediator.minimizeWindowIfHidden();
		}
	}

	/**
	 * Shows a balloon popup message by the system tray icon. The message
	 * will disappear by itself after a few seconds, or if the user clicks
	 * on it.
	 *
	 * @param title The title of the message.
	 * @param message The message to show in the popup.
	 */
	public void showBalloonMessage( final String title, final String message )
	{
		if ( trayIcon != null )
			trayIcon.displayMessage( title, message, MessageType.NONE );
	}
}
