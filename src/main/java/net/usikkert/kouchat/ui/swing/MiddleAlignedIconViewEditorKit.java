
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

import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;

/**
 * This is almost a normal {@link StyledEditorKit}, with the
 * only difference being the use of a custom view factory
 * to be able to middle align icons with the text.
 *
 * @author Christian Ihle
 * @see MiddleAlignedIconViewFactory
 * @see MiddleAlignedIconView
 */
public class MiddleAlignedIconViewEditorKit extends StyledEditorKit
{
	/** Default version uid. */
	private static final long serialVersionUID = 1L;

	/** The custom view factory to use. */
	private final ViewFactory viewFactory;

	/**
	 * Constructor. Initializes the view factory.
	 */
	public MiddleAlignedIconViewEditorKit()
	{
		viewFactory = new MiddleAlignedIconViewFactory();
	}

	/**
	 * Gets the {@link MiddleAlignedIconViewFactory}.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public ViewFactory getViewFactory()
	{
		return viewFactory;
	}
}
