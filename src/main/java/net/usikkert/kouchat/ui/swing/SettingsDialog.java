
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
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.UIManager.LookAndFeelInfo;

import net.usikkert.kouchat.Constants;
import net.usikkert.kouchat.misc.ErrorHandler;
import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.util.Validate;

/**
 * This is the dialog window used to change settings.
 *
 * @author Christian Ihle
 */
public class SettingsDialog extends JDialog implements ActionListener
{
	private static final Logger LOG = Logger.getLogger( SettingsDialog.class.getName() );
	private static final long serialVersionUID = 1L;

	private final JButton saveB, cancelB, chooseOwnColorB, chooseSysColorB, testBrowserB, chooseBrowserB;
	private final JTextField nickTF, browserTF;
	private final JLabel nickL, ownColorL, sysColorL, browserL, lookAndFeelL;
	private final JCheckBox soundCB, loggingCB, smileysCB;
	private final JComboBox lookAndFeelCB;
	private final Settings settings;
	private final ErrorHandler errorHandler;
	private Mediator mediator;

	/**
	 * Constructor. Creates the dialog.
	 *
	 * @param imageLoader The image loader.
	 */
	public SettingsDialog( final ImageLoader imageLoader )
	{
		Validate.notNull( imageLoader, "Image loader can not be null" );

		nickL = new JLabel( "Nick:" );
		nickTF = new JTextField( 10 );
		new CopyPastePopup( nickTF );

		JPanel nickP = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		nickP.add( nickL );
		nickP.add( nickTF );
		nickP.setBorder( BorderFactory.createTitledBorder( "Choose nick" ) );

		ownColorL = new JLabel( "Own text color looks like this" );
		ownColorL.setToolTipText( "<html>You and other users will see"
				+ "<br>the messages you write in this color.</html>" );

		sysColorL = new JLabel( "System text color looks like this" );
		sysColorL.setToolTipText( "<html>Information messages from the application"
				+ "<br>will be shown in this color.</html>" );

		chooseOwnColorB = new JButton( "Change" );
		chooseOwnColorB.addActionListener( this );
		chooseSysColorB = new JButton( "Change" );
		chooseSysColorB.addActionListener( this );

		JPanel ownColorP = new JPanel();
		ownColorP.setLayout( new BoxLayout( ownColorP, BoxLayout.LINE_AXIS ) );
		ownColorP.add( ownColorL );
		ownColorP.add( Box.createHorizontalGlue() );
		ownColorP.add( chooseOwnColorB );

		JPanel sysColorP = new JPanel();
		sysColorP.setLayout( new BoxLayout( sysColorP, BoxLayout.LINE_AXIS ) );
		sysColorP.add( sysColorL );
		sysColorP.add( Box.createHorizontalGlue() );
		sysColorP.add( chooseSysColorB );

		lookAndFeelL = new JLabel( "Look and feel" );
		lookAndFeelL.setToolTipText( "<html>Gives a choice of all the different looks that are available."
				+ "<br />Note that " + Constants.APP_NAME + " needs to be restarted for the"
				+ "<br />changes to take effect.</html>" );
		lookAndFeelCB = new JComboBox( UITools.getLookAndFeels() );

		JPanel lookAndFeelP = new JPanel();
		lookAndFeelP.setLayout( new BoxLayout( lookAndFeelP, BoxLayout.LINE_AXIS ) );
		lookAndFeelP.add( lookAndFeelL );
		lookAndFeelP.add( Box.createHorizontalGlue() );
		lookAndFeelP.add( lookAndFeelCB );

		JPanel lookP = new JPanel( new GridLayout( 3, 1, 1, 4 ) );
		lookP.add( ownColorP );
		lookP.add( sysColorP );
		lookP.add( lookAndFeelP );
		lookP.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder( "Choose look" ),
				BorderFactory.createEmptyBorder( 0, 5, 0, 5 ) ) );

		soundCB = new JCheckBox( "Enable sound" );
		soundCB.setToolTipText( "<html>Will give a short sound notification when"
				+ "<br>a new message is received if " + Constants.APP_NAME
				+ "<br>is minimized to the system tray, and"
				+ "<br>when asked to receive a file.</html>" );

		loggingCB = new JCheckBox( "Enable logging" );
		loggingCB.setToolTipText( "<html>Stores the conversation in the main chat to a log file in"
				+ "<br>" + Constants.APP_LOG_FOLDER
				+ "<br>Only text written after this option was enabled will be stored.</html>" );

		smileysCB = new JCheckBox( "Enable smileys" );
		smileysCB.setToolTipText( "<html>Replaces text smileys in the chat with smiley images."
				+ "<br />See the FAQ for a list of available smileys.</html>" );

		JPanel miscP = new JPanel( new GridLayout( 2, 2 ) );
		miscP.add( soundCB );
		miscP.add( loggingCB );
		miscP.add( smileysCB );
		miscP.setBorder( BorderFactory.createTitledBorder( "Misc" ) );

		browserL = new JLabel( "Browser: " );
		browserTF = new JTextField( 22 );
		browserTF.setToolTipText( "<html>When you click on a link in the chat it will open"
				+ "<br>in the browser defined here. If this field"
				+ "<br>is empty the default browser on your system"
				+ "<br>will be used, if possible.</html>" );
		new CopyPastePopup( browserTF );

		JPanel browserTopP = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		browserTopP.add( browserL );
		browserTopP.add( browserTF );

		chooseBrowserB = new JButton( "Choose" );
		chooseBrowserB.addActionListener( this );
		testBrowserB = new JButton( "Test" );
		testBrowserB.addActionListener( this );

		JPanel browserBottomP = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		browserBottomP.add( chooseBrowserB );
		browserBottomP.add( testBrowserB );

		JPanel browserP = new JPanel( new BorderLayout() );
		browserP.add( browserTopP, BorderLayout.NORTH );
		browserP.add( browserBottomP, BorderLayout.SOUTH );
		browserP.setBorder( BorderFactory.createTitledBorder( "Choose browser" ) );

		JPanel centerP = new JPanel( new BorderLayout() );
		centerP.add( lookP, BorderLayout.CENTER );
		centerP.add( miscP, BorderLayout.SOUTH );
		centerP.add( browserP, BorderLayout.NORTH );

		saveB = new JButton( "OK" );
		saveB.addActionListener( this );
		cancelB = new JButton( "Cancel" );
		cancelB.addActionListener( this );
		saveB.setPreferredSize( cancelB.getPreferredSize() );

		JPanel buttonP = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		buttonP.add( saveB );
		buttonP.add( cancelB );

		JPanel panel = new JPanel( new BorderLayout() );
		panel.add( nickP, BorderLayout.NORTH );
		panel.add( centerP, BorderLayout.CENTER );
		panel.add( buttonP, BorderLayout.SOUTH );
		panel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 5, 10 ) );

		getContentPane().add( panel );

		pack();
		setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
		setIconImage( imageLoader.getAppIcon().getImage() );
		setTitle( UITools.createTitle( "Settings" ) );
		setResizable( false );
		setModal( true );
		hideWithEscape();

		// So the save button activates using Enter
		getRootPane().setDefaultButton( saveB );

		settings = Settings.getSettings();
		errorHandler = ErrorHandler.getErrorHandler();
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
				setVisible( false );
			}
		};

		getRootPane().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( escapeKeyStroke, "ESCAPE" );
		getRootPane().getActionMap().put( "ESCAPE", escapeAction );
	}

	/**
	 * Sets the mediator for this window.
	 *
	 * @param mediator The mediator to use.
	 */
	public void setMediator( final Mediator mediator )
	{
		this.mediator = mediator;
	}

	/**
	 * Handles all the buttons in this window.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed( final ActionEvent e )
	{
		if ( e.getSource() == saveB )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					if ( mediator.changeNick( nickTF.getText() ) )
					{
						settings.setSysColor( sysColorL.getForeground().getRGB() );
						settings.setOwnColor( ownColorL.getForeground().getRGB() );
						settings.setSound( soundCB.isSelected() );
						settings.setLogging( loggingCB.isSelected() );
						settings.setBrowser( browserTF.getText() );
						settings.setSmileys( smileysCB.isSelected() );
						LookAndFeelWrapper lnfw = (LookAndFeelWrapper) lookAndFeelCB.getSelectedItem();
						settings.setLookAndFeel( lnfw.getLookAndFeelInfo().getName() );
						settings.saveSettings();
						setVisible( false );
						notifyLookAndFeelChange( lnfw );
					}
				}
			} );
		}

		else if ( e.getSource() == cancelB )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					setVisible( false );
				}
			} );
		}

		else if ( e.getSource() == chooseOwnColorB )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					Color newColor = UITools.showColorChooser( "Choose color for own messages",
							new Color( settings.getOwnColor() ) );

					if ( newColor != null )
					{
						ownColorL.setForeground( newColor );
					}
				}
			} );
		}

		else if ( e.getSource() == chooseSysColorB )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					Color newColor = UITools.showColorChooser( "Choose color for system messages",
							new Color( settings.getSysColor() ) );

					if ( newColor != null )
					{
						sysColorL.setForeground( newColor );
					}
				}
			} );
		}

		else if ( e.getSource() == testBrowserB )
		{
			SwingUtilities.invokeLater( new Runnable()
			{
				@Override
				public void run()
				{
					String browser = browserTF.getText();

					if ( browser.trim().length() > 0 )
					{
						try
						{
							Runtime.getRuntime().exec( browser + " " + Constants.APP_WEB );
						}

						catch ( final IOException e )
						{
							errorHandler.showError( "Could not open the browser '" + browser
									+ "'. Try using the full path." );
						}
					}

					else if ( UITools.isDesktopActionSupported( Desktop.Action.BROWSE ) )
					{
						try
						{
							Desktop.getDesktop().browse( new URI( Constants.APP_WEB ) );
						}

						catch ( final IOException e )
						{
							errorHandler.showError( "Could not open the default browser." );
						}

						catch ( final URISyntaxException e )
						{
							LOG.log( Level.WARNING, e.toString() );
							errorHandler.showError( "That's strange, could not open " + Constants.APP_WEB );
						}
					}

					else
					{
						errorHandler.showError( "Your system does not support a default browser."
								+ " Please choose a browser manually." );
					}
				}
			} );
		}

		else if ( e.getSource() == chooseBrowserB )
		{
			JFileChooser chooser = UITools.createFileChooser( "Open" );
			int returnVal = chooser.showOpenDialog( null );

			if ( returnVal == JFileChooser.APPROVE_OPTION )
			{
				File file = chooser.getSelectedFile().getAbsoluteFile();
				browserTF.setText( file.getAbsolutePath() );
			}
		}
	}

	/**
	 * Notifies the user that the application needs to be restarted before
	 * the new look and feel is used.
	 *
	 * @param lnfw Information about the chosen look and feel.
	 */
	private void notifyLookAndFeelChange( final LookAndFeelWrapper lnfw )
	{
		String newLookAndFeel = lnfw.getLookAndFeelInfo().getName();
		LookAndFeelInfo currentLookAndFeel = UITools.getCurrentLookAndFeel();

		if ( currentLookAndFeel == null || !newLookAndFeel.equals( currentLookAndFeel.getName() ) )
		{
			UITools.showInfoMessage( "The new look and feel will be used the next time "
					+ Constants.APP_NAME + " is started.",
					"Changed look and feel" );
		}
	}

	/**
	 * Loads the current settings, and shows the window.
	 */
	public void showSettings()
	{
		nickTF.setText( settings.getMe().getNick() );
		sysColorL.setForeground( new Color( settings.getSysColor() ) );
		ownColorL.setForeground( new Color( settings.getOwnColor() ) );
		soundCB.setSelected( settings.isSound() );
		loggingCB.setSelected( settings.isLogging() );
		browserTF.setText( settings.getBrowser() );
		smileysCB.setSelected( settings.isSmileys() );
		selectLookAndFeel();

		setVisible( true );
		nickTF.requestFocusInWindow();
	}

	/**
	 * Selects the correct look and feel in the combobox.
	 *
	 * <p>The correct item is either the saved look and feel,
	 * or the current look and feel if none is saved yet.</p>
	 */
	private void selectLookAndFeel()
	{
		LookAndFeelInfo lookAndFeel = UITools.getLookAndFeel( settings.getLookAndFeel() );
		String lnfClass = "";

		if ( lookAndFeel == null )
			lnfClass = UIManager.getLookAndFeel().getClass().getName();
		else
			lnfClass = lookAndFeel.getClassName();

		for ( int i = 0; i < lookAndFeelCB.getItemCount(); i++ )
		{
			LookAndFeelWrapper lafw = (LookAndFeelWrapper) lookAndFeelCB.getItemAt( i );

			if ( lafw.getLookAndFeelInfo().getClassName().equals( lnfClass ) )
			{
				lookAndFeelCB.setSelectedIndex( i );
				break;
			}
		}
	}
}
