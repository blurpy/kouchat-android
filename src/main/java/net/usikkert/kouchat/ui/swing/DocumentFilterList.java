
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

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import net.usikkert.kouchat.util.Validate;

/**
 * This is a document filter list that can be used when more than one
 * document filter is needed.
 *
 * <p>This filter does not make any changes to the text. The text is added
 * to the document, and then a list of sub-filters are notified so they can
 * do their magic.</p>
 *
 * @author Christian Ihle
 */
public class DocumentFilterList extends DocumentFilter
{
	/** A list of sub-filters that are notified when text is added. */
	private final List<DocumentFilter> filters;

	/**
	 * Constructor.
	 */
	public DocumentFilterList()
	{
		filters = new ArrayList<DocumentFilter>();
	}

	/**
	 * Inserts the text at the end of the Document, and notifies the sub-filters.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void insertString( final FilterBypass fb, final int offset, final String text,
			final AttributeSet attr ) throws BadLocationException
	{
		super.insertString( fb, offset, text, attr );

		for ( DocumentFilter filter : filters )
		{
			filter.insertString( fb, offset, text, attr );
		}
	}

	/**
	 * Adds the document filter for notification when text is added.
	 *
	 * @param filter The document filter to add.
	 */
	public synchronized void addDocumentFilter( final DocumentFilter filter )
	{
		Validate.notNull( filter, "Document filter can not be null" );
		filters.add( filter );
	}

	/**
	 * Removes the document filter from the notification list.
	 *
	 * @param filter The document filter to remove.
	 */
	public synchronized void removeDocumentFilter( final DocumentFilter filter )
	{
		Validate.notNull( filter, "Document filter can not be null" );
		filters.remove( filter );
	}
}
