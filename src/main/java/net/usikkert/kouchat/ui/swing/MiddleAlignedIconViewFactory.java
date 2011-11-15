
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

import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * This is almost like the normal <code>StyledViewFactory</code> in
 * {@link StyledEditorKit}, except this uses a {@link MiddleAlignedIconView}
 * instead of the usual {@link IconView}.
 *
 * @author Christian Ihle
 */
public class MiddleAlignedIconViewFactory implements ViewFactory
{
	/**
	 * This works the same as original view factory, except the
	 * use of a middle aligned icon view.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public View create( final Element elem )
	{
		String kind = elem.getName();

		if ( kind != null )
		{
			if ( kind.equals( AbstractDocument.ContentElementName ) )
				return new LabelView( elem );
			else if ( kind.equals( AbstractDocument.ParagraphElementName ) )
				return new ParagraphView( elem );
			else if ( kind.equals( AbstractDocument.SectionElementName ) )
				return new BoxView( elem, View.Y_AXIS );
			else if ( kind.equals( StyleConstants.ComponentElementName ) )
				return new ComponentView( elem );
			else if ( kind.equals( StyleConstants.IconElementName ) )
				return new MiddleAlignedIconView( elem ); // Overridden icon view
		}

		// Default is text display
		return new LabelView( elem );
	}
}
