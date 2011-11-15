
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
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.WindowConstants;
import javax.swing.UIManager.LookAndFeelInfo;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.UncaughtExceptionLogger;

/**
 * This is the main chat window.
 *
 * @author Christian Ihle
 */
public class KouChatFrame extends JFrame implements WindowListener, FocusListener
{
	/** Standard serial version UID. */
	private static final long serialVersionUID = 1L;

	/** The panel that contains all the other panels and components. */
	private final MainPanel mainP;

	/** The panel with the buttons and user list. */
	private final SidePanel sideP;

	/** The mediator that connects all the panels. */
	private final Mediator mediator;

	/** The menu bar. */
	private final MenuBar menuBar;

	/** The settings. */
	private final Settings settings;

	/** The application user. */
	private final User me;

	/** The icons to use for the window frame. */
	private final StatusIcons statusIcons;

	/**
	 * Constructor.
	 *
	 * Initializes all components, shows the window, and starts the network.
	 */
	public KouChatFrame()
	{
		System.setProperty( Constants.PROPERTY_CLIENT_UI, "Swing" );
		settings = Settings.getSettings();
		me = settings.getMe();
		setLookAndFeel();
		new SwingPopupErrorHandler();
		ImageLoader imageLoader = new ImageLoader();
		registerUncaughtExceptionListener( imageLoader );
		statusIcons = new StatusIcons( imageLoader );

		ButtonPanel buttonP = new ButtonPanel();
		sideP = new SidePanel( buttonP, imageLoader );
		mainP = new MainPanel( sideP, imageLoader );
		SysTray sysTray = new SysTray( imageLoader );
		SettingsDialog settingsDialog = new SettingsDialog( imageLoader );
		menuBar = new MenuBar( imageLoader );

		ComponentHandler compHandler = new ComponentHandler();
		compHandler.setGui( this );
		compHandler.setButtonPanel( buttonP );
		compHandler.setSidePanel( sideP );
		compHandler.setMainPanel( mainP );
		compHandler.setSysTray( sysTray );
		compHandler.setSettingsDialog( settingsDialog );
		compHandler.setMenuBar( menuBar );

		mediator = new SwingMediator( compHandler, imageLoader );
		buttonP.setMediator( mediator );
		sideP.setMediator( mediator );
		mainP.setMediator( mediator );
		sysTray.setMediator( mediator );
		settingsDialog.setMediator( mediator );
		menuBar.setMediator( mediator );

		// Show tooltips for 10 seconds. Default is very short.
		ToolTipManager.sharedInstance().setDismissDelay( 10000 );

		setJMenuBar( menuBar );
		getContentPane().add( mainP, BorderLayout.CENTER );
		setTitle( Constants.APP_NAME );
		setIconImage( imageLoader.getAppIcon().getImage() );
		setSize( 650, 480 );
		setMinimumSize( new Dimension( 450, 300 ) );
		setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
		setVisible( true );

		getRootPane().addFocusListener( this );
		addWindowListener( this );
		fixTextFieldFocus();
		hideWithEscape();
		mediator.updateTitleAndTray();

		// Try to stop the gui from lagging during startup
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				mediator.start();
				mainP.getMsgTF().requestFocusInWindow();
			}
		} );
	}

	/**
	 * Registers the {@link ExceptionDialog} as an uncaught exception listener.
	 *
	 * @param imageLoader The image loader.
	 */
	private void registerUncaughtExceptionListener( final ImageLoader imageLoader )
	{
		UncaughtExceptionLogger uncaughtExceptionLogger =
			(UncaughtExceptionLogger) Thread.getDefaultUncaughtExceptionHandler();
		uncaughtExceptionLogger.registerUncaughtExceptionListener(
				new ExceptionDialog( null, true, imageLoader ) );
	}

	/**
	 * Sets the correct look and feel.
	 *
	 * <p>The correct look and feel is either the saved look and feel,
	 * or the system look and feel if one exists. If none of those are
	 * available, then no look and feel is set.</p>
	 */
	private void setLookAndFeel()
	{
		LookAndFeelInfo lookAndFeel = UITools.getLookAndFeel( settings.getLookAndFeel() );

		if ( lookAndFeel == null )
		{
			if ( UITools.isSystemLookAndFeelSupported() )
			{
				UITools.setSystemLookAndFeel();
			}
		}

		else
		{
			UITools.setLookAndFeel( settings.getLookAndFeel() );
		}
	}

	/**
	 * Adds a shortcut to hide the window when escape is pressed.
	 */
	private void hideWithEscape()
	{
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false );

		Action escapeAction = new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed( final ActionEvent e )
			{
				mediator.minimize();
			}
		};

		mainP.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ).put( escapeKeyStroke, "ESCAPE" );
		mainP.getActionMap().put( "ESCAPE", escapeAction );
	}

	/**
	 * If this window is focused, the text field will get the keyboard events
	 * if the chat area or the user list was focused when typing was started.
	 */
	private void fixTextFieldFocus()
	{
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( new KeyEventDispatcher()
		{
			@Override
			public boolean dispatchKeyEvent( final KeyEvent e )
			{
				if ( e.getID() == KeyEvent.KEY_TYPED && isFocused() && ( e.getSource() == mainP.getChatTP() || e.getSource() == sideP.getUserList() ) )
				{
					KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent( mainP.getMsgTF(), e );
					mainP.getMsgTF().requestFocusInWindow();

					return true;
				}

				else
					return false;
			}
		} );
	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void focusGained( final FocusEvent e )
	{

	}

	/**
	 * Make sure the menubar gets focus when navigating with the keyboard.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void focusLost( final FocusEvent e )
	{
		if ( menuBar.isPopupMenuVisible() )
			getRootPane().requestFocusInWindow();
	}

	/**
	 * Shut down the right way.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void windowClosing( final WindowEvent e )
	{
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				mediator.quit();
			}
		} );
	}

	/**
	 * Fix focus and repaint issues when the window gets focused.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void windowActivated( final WindowEvent e )
	{
		mainP.getChatSP().repaint();
		sideP.getUserList().repaint();
		mainP.getMsgTF().requestFocusInWindow();

		if ( me.isNewMsg() )
		{
			me.setNewMsg( false );
			mediator.updateTitleAndTray();
		}
	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void windowClosed( final WindowEvent e )
	{

	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void windowDeactivated( final WindowEvent e )
	{

	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void windowDeiconified( final WindowEvent e )
	{

	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void windowIconified( final WindowEvent e )
	{

	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void windowOpened( final WindowEvent e )
	{

	}

	/**
	 * Changes the window icon depending on away status and if a new message has arrived.
	 */
	public void updateWindowIcon()
	{
		if ( me.isNewMsg() )
		{
			if ( me.isAway() )
				setWindowIcon( statusIcons.getAwayActivityIcon() );
			else
				setWindowIcon( statusIcons.getNormalActivityIcon() );
		}

		else
		{
			if ( me.isAway() )
				setWindowIcon( statusIcons.getAwayIcon() );
			else
				setWindowIcon( statusIcons.getNormalIcon() );
		}
	}

	/**
	 * Sets the window icon if it's different from the icon already in use.
	 *
	 * @param icon The window icon to use.
	 */
	public void setWindowIcon( final Image icon )
	{
		if ( getIconImage() != icon )
			setIconImage( icon );
	}
}
