
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.util.Validate;

/**
 * This is the main menubar for the application.
 *
 * @author Christian Ihle
 */
public class MenuBar extends JMenuBar implements ActionListener
{
	/** Standard serial version UID. */
	private static final long serialVersionUID = 1L;

	private final JMenu fileMenu, toolsMenu, helpMenu;
	private final JMenuItem minimizeMI, quitMI;
	private final JMenuItem clearMI, awayMI, topicMI, settingsMI;
	private final JMenuItem aboutMI, commandsMI, faqMI, licenseMI, tipsMI;
	private final ImageLoader imageLoader;
	private Mediator mediator;
	private TextViewerDialog faqViewer, licenseViewer, tipsViewer;

	/**
	 * Constructor. Creates the menubar.
	 *
	 * @param imageLoader The image loader.
	 */
	public MenuBar( final ImageLoader imageLoader )
	{
		Validate.notNull( imageLoader, "Image loader can not be null" );
		this.imageLoader = imageLoader;

		fileMenu = new JMenu( "File" );
		fileMenu.setMnemonic( 'F' );
		minimizeMI = new JMenuItem( "Minimize" );
		minimizeMI.setMnemonic( 'M' );
		minimizeMI.addActionListener( this );
		quitMI = new JMenuItem( "Quit" );
		quitMI.setMnemonic( 'Q' );
		quitMI.addActionListener( this );

		fileMenu.add( minimizeMI );
		fileMenu.addSeparator();
		fileMenu.add( quitMI );

		toolsMenu = new JMenu( "Tools" );
		toolsMenu.setMnemonic( 'T' );
		clearMI = new JMenuItem( "Clear chat" );
		clearMI.setMnemonic( 'C' );
		clearMI.addActionListener( this );
		awayMI = new JMenuItem( "Set away" );
		awayMI.setMnemonic( 'A' );
		awayMI.addActionListener( this );
		awayMI.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F2, 0 ) );
		topicMI = new JMenuItem( "Change topic" );
		topicMI.setMnemonic( 'O' );
		topicMI.addActionListener( this );
		topicMI.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F3, 0 ) );
		settingsMI = new JMenuItem( "Settings" );
		settingsMI.setMnemonic( 'S' );
		settingsMI.addActionListener( this );
		settingsMI.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F4, 0 ) );

		toolsMenu.add( clearMI );
		toolsMenu.add( awayMI );
		toolsMenu.add( topicMI );
		toolsMenu.addSeparator();
		toolsMenu.add( settingsMI );

		helpMenu = new JMenu( "Help" );
		helpMenu.setMnemonic( 'H' );
		faqMI = new JMenuItem( "FAQ" );
		faqMI.setMnemonic( 'F' );
		faqMI.addActionListener( this );
		faqMI.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ) );
		licenseMI = new JMenuItem( "License" );
		licenseMI.setMnemonic( 'L' );
		licenseMI.addActionListener( this );
		tipsMI = new JMenuItem( "Tips & tricks" );
		tipsMI.setMnemonic( 'T' );
		tipsMI.addActionListener( this );
		commandsMI = new JMenuItem( "Commands" );
		commandsMI.setMnemonic( 'C' );
		commandsMI.addActionListener( this );
		aboutMI = new JMenuItem( "About" );
		aboutMI.setMnemonic( 'A' );
		aboutMI.addActionListener( this );

		helpMenu.add( faqMI );
		helpMenu.add( tipsMI );
		helpMenu.add( licenseMI );
		helpMenu.addSeparator();
		helpMenu.add( commandsMI );
		helpMenu.addSeparator();
		helpMenu.add( aboutMI );

		add( fileMenu );
		add( toolsMenu );
		add( helpMenu );
	}

	/**
	 * Sets the mediator to use.
	 *
	 * @param mediator The mediator to set.
	 */
	public void setMediator( final Mediator mediator )
	{
		this.mediator = mediator;
	}

	/**
	 * If away, the settings and topic menu items are disabled.
	 *
	 * @param away If away or not.
	 */
	public void setAwayState( final boolean away )
	{
		settingsMI.setEnabled( !away );
		topicMI.setEnabled( !away );
	}

	/**
	 * Checks if any of the menus are visible.
	 *
	 * @return True if at least one menu is visible.
	 */
	public boolean isPopupMenuVisible()
	{
		return fileMenu.isPopupMenuVisible() || toolsMenu.isPopupMenuVisible() || helpMenu.isPopupMenuVisible();
	}

	/**
	 * ActionListener for the menu items.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed( final ActionEvent e )
	{
		// File/Quit
		if ( e.getSource() == quitMI )
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

		// Tools/Settings
		else if ( e.getSource() == settingsMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					mediator.showSettings();
				}
			} );
		}

		// File/Minimize
		else if ( e.getSource() == minimizeMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					mediator.minimize();
				}
			} );
		}

		// Tools/Set away
		else if ( e.getSource() == awayMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					mediator.setAway();
				}
			} );
		}

		// Tools/Change topic
		else if ( e.getSource() == topicMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					mediator.setTopic();
				}
			} );
		}

		// Tools/Clear chat
		else if ( e.getSource() == clearMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					mediator.clearChat();
				}
			} );
		}

		// Help/FAQ
		else if ( e.getSource() == faqMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					if ( faqViewer == null )
					{
						faqViewer = new TextViewerDialog( Constants.FILE_FAQ,
								"Frequently Asked Questions", true, imageLoader );
					}

					faqViewer.setVisible( true );
				}
			} );
		}

		// Help/Tips & tricks
		else if ( e.getSource() == tipsMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					if ( tipsViewer == null )
					{
						tipsViewer = new TextViewerDialog( Constants.FILE_TIPS,
								"Tips & tricks", false, imageLoader );
					}

					tipsViewer.setVisible( true );
				}
			} );
		}

		// Help/License
		else if ( e.getSource() == licenseMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					if ( licenseViewer == null )
					{
						licenseViewer = new TextViewerDialog( Constants.FILE_LICENSE,
								Constants.APP_LICENSE_NAME, false, imageLoader );
					}

					licenseViewer.setVisible( true );
				}
			} );
		}

		// Help/Commands
		else if ( e.getSource() == commandsMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					mediator.showCommands();
				}
			} );
		}

		// Help/About
		else if ( e.getSource() == aboutMI )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					MessageDialog aboutD = new MessageDialog( null, true, imageLoader );

					aboutD.setTitle( UITools.createTitle( "About" ) );
					aboutD.setTopText( Constants.APP_NAME + " v" + Constants.APP_VERSION );
					aboutD.setContent( "<html>Copyright " + Constants.APP_COPYRIGHT_YEARS + " by " + Constants.AUTHOR_NAME + "."
							+ "<br>" + Constants.AUTHOR_MAIL
							+ "<br>" + Constants.APP_WEB
							+ "<br>"
							+ "<br>Source available under the " + Constants.APP_LICENSE_NAME + "."
							+ "<br>See the license for details.</html>" );

					aboutD.setVisible( true );
				}
			} );
		}
	}
}
