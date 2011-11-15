
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;

import net.usikkert.kouchat.misc.User;
import net.usikkert.kouchat.util.Validate;

/**
 * This class renders the rows in the user list.
 *
 * @author Christian Ihle
 */
public class UserListCellRenderer extends JLabel implements ListCellRenderer
{
	/** Standard serial version UID. */
	private static final long serialVersionUID = 1L;

	/** The logger to use for this class. */
	private static final Logger LOG = Logger.getLogger( UserListCellRenderer.class.getName() );

	/** Max size of the horizontal insets in the list element border. */
	private static final int MAX_HORI_SIZE = 4;

	/** Max size of the vertical insets in the list element border. */
	private static final int MAX_VERT_SIZE = 3;

	/** The envelope icon object. */
	private final ImageIcon envelope;

	/** The dot icon object. */
	private final ImageIcon dot;

	/** The border to use for selected list elements. */
	private final Border selectedBorder;

	/** The border to use for unselected list elements. */
	private final Border normalBorder;

	/**
	 * Default constructor.
	 *
	 * @param imageLoader The image loader.
	 */
	public UserListCellRenderer( final ImageLoader imageLoader )
	{
		Validate.notNull( imageLoader, "Image loader can not be null" );

		envelope = imageLoader.getEnvelopeIcon();
		dot = imageLoader.getDotIcon();

		Border noFocusBorder = UIManager.getBorder( "List.cellNoFocusBorder" );
		Border highlightBorder = UIManager.getBorder( "List.focusCellHighlightBorder" );

		Insets highlightBorderInsets = highlightBorder.getBorderInsets( this );
		int vertical = Math.max( 0, MAX_VERT_SIZE - highlightBorderInsets.top );
		int horizontal = Math.max( 0, MAX_HORI_SIZE - highlightBorderInsets.left );

		// If noFocusBorder does not exist, the normalBorder will be 1px smaller
		int padding = ( noFocusBorder == null ? 1 : 0 );

		normalBorder = BorderFactory.createCompoundBorder(
				noFocusBorder,
				BorderFactory.createEmptyBorder( vertical + padding, horizontal + padding,
												 vertical + padding, horizontal + padding ) );

		selectedBorder = BorderFactory.createCompoundBorder(
				highlightBorder,
				BorderFactory.createEmptyBorder( vertical, horizontal, vertical, horizontal ) );

		setOpaque( true );
	}

	/**
	 * Displays an icon and the user's nick name.
	 *
	 * If the user is away, the nick name is shown in gray.
	 * If the user is "me", the nick name is shown in bold.
	 * If the user has a new message, the icon changes to an envelope.
	 * If the user is writing, the nick name will have a star next to it.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public Component getListCellRendererComponent( final JList list, final Object value,
			final int index, final boolean isSelected, final boolean cellHasFocus )
	{
		if ( isSelected )
		{
			setBackground( list.getSelectionBackground() );
			setForeground( list.getSelectionForeground() );
			setBorder( selectedBorder );
		}

		else
		{
			setBackground( list.getBackground() );
			setForeground( list.getForeground() );
			setBorder( normalBorder );
		}

		User user = (User) value;

		if ( user != null )
		{
			if ( user.isMe() )
				setFont( list.getFont().deriveFont( Font.BOLD ) );
			else
				setFont( list.getFont().deriveFont( Font.PLAIN ) );

			if ( user.isAway() )
				setForeground( Color.GRAY );

			if ( user.isNewPrivMsg() )
				setIcon( envelope );
			else
				setIcon( dot );

			if ( user.isWriting() )
			{
				setText( user.getNick() + " *" );
				setToolTipText( user.getNick() + " is writing" );
			}

			else
			{
				setText( user.getNick() );

				if ( user.isAway() )
					setToolTipText( user.getNick() + " is away" );
				else
					setToolTipText( user.getNick() );
			}
		}

		else
			LOG.log( Level.WARNING, "Got a null list element." );

		setEnabled( list.isEnabled() );
		setComponentOrientation( list.getComponentOrientation() );

		return this;
	}

	/**
	 * Copied from {@link DefaultListCellRenderer#isOpaque()}
	 * to fix the gray background with some look and feels like GTK+ and Nimbus.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public boolean isOpaque()
	{
		Color background = getBackground();
		Component parent = getParent();

		if ( parent != null )
		{
			parent = parent.getParent();
		}

		// Parent should now be the JList.
		boolean colorMatch = background != null
						  && parent != null
						  && background.equals( parent.getBackground() )
						  && parent.isOpaque();

		return !colorMatch && super.isOpaque();
	}
}
