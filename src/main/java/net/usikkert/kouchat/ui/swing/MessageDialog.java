
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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.usikkert.kouchat.util.Validate;

/**
 * This is a more fancy message dialog with a header.
 *
 * @author Christian Ihle
 */
public class MessageDialog extends JDialog
{
	/** Standard serial version UID. */
	private static final long serialVersionUID = 1L;

	private final JLabel appNameL, contentL;

	/**
	 * Creates a new MessageDialog. To open the dialog, use setVisible().
	 *
	 * @param parent The parent frame.
	 * @param modal If the dialog should block or not.
	 * @param imageLoader The image loader.
	 */
	public MessageDialog( final Frame parent, final boolean modal, final ImageLoader imageLoader )
	{
		super( parent, modal );
		Validate.notNull( imageLoader, "Image loader can not be null" );

		setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		setTitle( UITools.createTitle( "Missing title" ) );
		setResizable( false );
		setIconImage( imageLoader.getAppIcon().getImage() );

		appNameL = new JLabel();
		appNameL.setFont( new Font( "Dialog", 0, 22 ) );
		appNameL.setIcon( imageLoader.getAppIcon() );
		appNameL.setText( " No top text" );

		JPanel northP = new JPanel();
		northP.setBackground( Color.WHITE );
		northP.setBorder( BorderFactory.createMatteBorder( 0, 0, 1,	0, Color.BLACK ) );
		northP.setLayout( new FlowLayout( FlowLayout.LEFT, 12, 12 ) );
		northP.add( appNameL );

		getContentPane().add( northP, BorderLayout.PAGE_START );

		JButton okB = new JButton();
		okB.setText( "OK" );
		okB.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( final ActionEvent e )
			{
				dispose();
			}
		} );

		getRootPane().setDefaultButton( okB );

		JPanel southP = new JPanel();
		southP.setLayout( new FlowLayout( FlowLayout.CENTER, 12, 12 ) );
		southP.add( okB );

		getContentPane().add( southP, BorderLayout.PAGE_END );

		JLabel iconIconL = new JLabel();
		iconIconL.setIcon( UIManager.getDefaults().getIcon( "OptionPane.informationIcon" ) );

		JPanel leftP = new JPanel();
		leftP.setLayout( new FlowLayout( FlowLayout.CENTER, 12, 12 ) );
		leftP.add( iconIconL );

		getContentPane().add( leftP, BorderLayout.LINE_START );

		contentL = new JLabel( "No content" );

		JPanel centerP = new JPanel();
		centerP.setBorder( BorderFactory.createEmptyBorder( 12, 2, 0, 12 ) );
		centerP.setLayout( new BorderLayout() );
		centerP.add( contentL, BorderLayout.CENTER );

		getContentPane().add( centerP, BorderLayout.CENTER );

		// Close with Escape key
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false );

		Action escapeAction = new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed( final ActionEvent e )
			{
				dispose();
			}
		};

		getRootPane().getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( escapeKeyStroke, "ESCAPE" );
		getRootPane().getActionMap().put( "ESCAPE", escapeAction );
	}

	/**
	 * This is the text shown at the top (below the titlebar), to the left of the icon.
	 *
	 * @param text The text to show.
	 */
	public void setTopText( final String text )
	{
		appNameL.setText( " " + text );
	}

	/**
	 * This is the main content.
	 *
	 * @param info The text to add.
	 */
	public void setContent( final String info )
	{
		contentL.setText( info );
	}

	/**
	 * Shows the Message Dialog.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void setVisible( final boolean visible )
	{
		pack();
		setLocationRelativeTo( getParent() );
		super.setVisible( visible );
	}
}
