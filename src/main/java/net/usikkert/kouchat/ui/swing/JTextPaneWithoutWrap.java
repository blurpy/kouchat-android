
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

import java.awt.Dimension;

import javax.swing.JTextPane;

/**
 * This is a modified {@link JTextPane} that doesn't wrap text.
 *
 * <p>The normal {@link JTextPane} wraps long lines of text instead
 * of adding a horizontal scrollbar. This modified version does
 * not wrap long lines, and instead shows the horizontal scrollbar.</p>
 *
 * @author Christian Ihle
 */
public class JTextPaneWithoutWrap extends JTextPane
{
	/** Standard serial version UID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Makes sure the size of the textpane fills the whole viewport.
	 *
	 * <p>Since {@link #getScrollableTracksViewportWidth()} is disabled
	 * then the size set here is the textpane's preferred size.
	 * Which is the width of the text in it. If the text in this
	 * textpane is shorter than the viewport, then the part of the
	 * textpane not filled with text is grayed out.</p>
	 *
	 * <p>To fix this, the size is adjusted to the same size
	 * as the viewport.</p>
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void setSize( final Dimension d )
	{
		// Parent is the viewport
		if ( d.width < getParent().getSize().width )
			d.width = getParent().getSize().width;

		super.setSize( d );
	}

	/**
	 * If the scrollpane should be the same size as the viewport.
	 *
	 * <p>If that's the case then long lines will wrap at the end of
	 * the textpane. If the scrollpane has it's own size then
	 * there is no need to wrap long lines.</p>
	 *
	 * @return false, to disable word wrap.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public boolean getScrollableTracksViewportWidth()
	{
		return false;
	}
}
