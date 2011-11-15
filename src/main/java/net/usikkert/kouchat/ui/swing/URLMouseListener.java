
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

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * This listener adds support for opening a url in a browser
 * by clicking on a link. The mouse cursor will also change when
 * hovering over a link.
 *
 * @author Christian Ihle
 */
public class URLMouseListener implements MouseListener, MouseMotionListener
{
	private final Cursor handCursor;
	private final JTextPane textPane;
	private final StyledDocument doc;

	/**
	 * Constructor.
	 *
	 * @param textPane The text pane this listener is registered to.
	 */
	public URLMouseListener( final JTextPane textPane )
	{
		this.textPane = textPane;

		doc = textPane.getStyledDocument();
		handCursor = new Cursor( Cursor.HAND_CURSOR );
	}

	/**
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void mouseDragged( final MouseEvent e )
	{

	}

	/**
	 * Updates the mouse cursor when hovering over a link.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void mouseMoved( final MouseEvent e )
	{
		int mousePos = textPane.viewToModel( e.getPoint() );

		AttributeSet attr = doc.getCharacterElement( mousePos ).getAttributes();

		if ( StyleConstants.isUnderline( attr ) )
		{
			if ( textPane.getCursor() != handCursor )
				textPane.setCursor( handCursor );
		}

		else
		{
			if ( textPane.getCursor() == handCursor )
				textPane.setCursor( null );
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
	 * Not implemented.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void mousePressed( final MouseEvent e )
	{

	}

	/**
	 * Opens the clicked link in a browser.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void mouseReleased( final MouseEvent e )
	{
		if ( e.getButton() == MouseEvent.BUTTON1 )
		{
			int clickPos = textPane.viewToModel( e.getPoint() );

			AttributeSet attr = doc.getCharacterElement( clickPos ).getAttributes();

			if ( StyleConstants.isUnderline( attr ) )
			{
				Object obj = attr.getAttribute( URLDocumentFilter.URL_ATTRIBUTE );

				if ( obj != null )
				{
					final String url = obj.toString();

					SwingUtilities.invokeLater( new Runnable()
					{
						@Override
						public void run()
						{
							UITools.browse( url );
						}
					} );
				}
			}
		}
	}
}
