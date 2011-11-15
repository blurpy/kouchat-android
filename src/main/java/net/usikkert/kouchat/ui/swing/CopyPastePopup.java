
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

/**
 * Shows a popup menu with copy, cut, paste and clear menu items.
 *
 * @author Christian Ihle
 */
public class CopyPastePopup extends JPopupMenu implements MouseListener, ActionListener
{
	/** Standard serial version UID. */
	private static final long serialVersionUID = 1L;

	/** Menu item to copy selected text. */
	private final JMenuItem copyMI;

	/** Menu item to paste text into the text field. */
	private final JMenuItem pasteMI;

	/** Menu item to cut selected text. */
	private final JMenuItem cutMI;

	/** Menu item to clear all the text from the text field. */
	private final JMenuItem clearMI;

	/** The text field this popup is connected to. */
	private final JTextField textfield;

	/**
	 * Constructor. Creates the menu.
	 *
	 * @param textfield The text field to use the popup on.
	 */
	public CopyPastePopup( final JTextField textfield )
	{
		this.textfield = textfield;

		copyMI = new JMenuItem( new DefaultEditorKit.CopyAction() );
		copyMI.setText( "Copy" );
		copyMI.setMnemonic( 'C' );
		copyMI.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_C, KeyEvent.CTRL_MASK ) );

		cutMI = new JMenuItem( new DefaultEditorKit.CutAction() );
		cutMI.setText( "Cut" );
		cutMI.setMnemonic( 'U' );
		cutMI.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_X, KeyEvent.CTRL_MASK ) );

		pasteMI = new JMenuItem( new DefaultEditorKit.PasteAction() );
		pasteMI.setText( "Paste" );
		pasteMI.setMnemonic( 'P' );
		pasteMI.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_V, KeyEvent.CTRL_MASK ) );

		clearMI = new JMenuItem( "Clear" );
		clearMI.setMnemonic( 'L' );

		add( cutMI );
		add( copyMI );
		add( pasteMI );
		addSeparator();
		add( clearMI );

		textfield.addMouseListener( this );
		clearMI.addActionListener( this );
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
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void mousePressed( final MouseEvent e )
	{

	}

	/**
	 * Shows the popup menu if right mouse button was used.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void mouseReleased( final MouseEvent e )
	{
		if ( isPopupTrigger( e ) && textfield.isEnabled() )
		{
			textfield.requestFocusInWindow();

			if ( textfield.getSelectedText() == null )
			{
				copyMI.setEnabled( false );
				cutMI.setEnabled( false );
			}

			else
			{
				copyMI.setEnabled( true );
				cutMI.setEnabled( true );
			}

			if ( textfield.getText().length() > 0 )
				clearMI.setEnabled( true );
			else
				clearMI.setEnabled( false );

			show( textfield, e.getX(), e.getY() );
		}
	}

	/**
	 * Clears the text in the text field.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed( final ActionEvent e )
	{
		textfield.setText( "" );
	}
}
