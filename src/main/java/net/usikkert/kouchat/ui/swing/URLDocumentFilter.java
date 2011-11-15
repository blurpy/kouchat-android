
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

import java.util.regex.Pattern;

import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * This document filter is used to highlight urls added to a {@link StyledDocument}.
 * The current form of highlighting is underlining the url.
 *
 * <p>3 different urls are recognized:</p>
 *
 * <ul>
 *   <li>protocol://host</li>
 *   <li>www.host.name</li>
 *   <li>ftp.host.name</li>
 * </ul>
 *
 * @author Christian Ihle
 */
public class URLDocumentFilter extends DocumentFilter
{
	/**
	 * The url is saved as an attribute in the Document, so
	 * this attribute can be used to retrieve the url later.
	 */
	public static final String URL_ATTRIBUTE = "url.attribute";

	/** Regex for: <code>protocol://host</code>. */
	private final Pattern protPattern;

	/** Regex for: <code>www.host.name</code>. */
	private final Pattern wwwPattern;

	/** Regex for: <code>ftp.host.name</code>. */
	private final Pattern ftpPattern;

	/**
	 * If this document filter is the only document filter used.
	 * This must be true if it is, or the text will not be visible.
	 * If this is not the only filter, then this must be false, or
	 * the same text will be shown several times.
	 */
	private final boolean standAlone;

	/**
	 * Constructor. Creates regex patterns to use for url checking.
	 *
	 * @param standAlone If this is the only document filter used.
	 */
	public URLDocumentFilter( final boolean standAlone )
	{
		this.standAlone = standAlone;

		protPattern = Pattern.compile( "\\w{2,}://\\w+\\S+.+" );
		wwwPattern = Pattern.compile( "www\\.\\w+\\S+\\.\\S+.+" );
		ftpPattern = Pattern.compile( "ftp\\.\\w+\\S+\\.\\S+.+" );
	}

	/**
	 * Checks if any parts of the text contains any urls. If a url is found,
	 * it is underlined and saved in an attribute.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void insertString( final FilterBypass fb, final int offset, final String text, final AttributeSet attr )
			throws BadLocationException
	{
		if ( standAlone )
			super.insertString( fb, offset, text, attr );

		// Make a copy now, or else it could change if another message comes
		final MutableAttributeSet urlAttr = (MutableAttributeSet) attr.copyAttributes();

		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				int startPos = findURLPos( text, 0 );

				if ( startPos != -1 )
				{
					StyleConstants.setUnderline( urlAttr, true );
					StyledDocument doc = (StyledDocument) fb.getDocument();

					while ( startPos != -1 )
					{
						int stopPos = text.indexOf( " ", startPos );

						if ( stopPos == -1 )
							stopPos = text.indexOf( "\n", startPos );

						urlAttr.addAttribute( URL_ATTRIBUTE, text.substring( startPos, stopPos ) );
						doc.setCharacterAttributes( offset + startPos, stopPos - startPos, urlAttr, false );
						startPos = findURLPos( text, stopPos );
					}
				}
			}
		} );
	}

	/**
	 * Returns the position of the first matching
	 * url in the text, starting from the specified offset.
	 *
	 * @param text The text to find urls in.
	 * @param offset Where in the text to begin the search.
	 * @return The position of the first character in the url, or -1
	 * if no url was found.
	 */
	private int findURLPos( final String text, final int offset )
	{
		int prot = text.indexOf( "://", offset );
		int www = text.indexOf( " www", offset );
		int ftp = text.indexOf( " ftp", offset );

		int firstMatch = -1;
		boolean retry = true;

		// Needs to loop because the text can get through the first test above,
		// but fail the regex match. If another url exists after the failed regex
		// match, it will not be found.
		while ( retry )
		{
			retry = false;

			if ( prot != -1 && ( prot < firstMatch || firstMatch == -1 ) )
			{
				int protStart = text.lastIndexOf( ' ', prot ) + 1;
				String t = text.substring( protStart, text.length() - 1 );

				if ( protPattern.matcher( t ).matches() )
					firstMatch = protStart;

				else
				{
					prot = text.indexOf( "://", prot + 1 );

					if ( prot != -1 && ( prot < firstMatch || firstMatch == -1 ) )
						retry = true;
				}
			}

			if ( www != -1 && ( www < firstMatch || firstMatch == -1 ) )
			{
				String t = text.substring( www + 1, text.length() - 1 );

				if ( wwwPattern.matcher( t ).matches() )
					firstMatch = www + 1;

				else
				{
					www = text.indexOf( " www", www + 1 );

					if ( www != -1 && ( www < firstMatch || firstMatch == -1 ) )
						retry = true;
				}
			}

			if ( ftp != -1 && ( ftp < firstMatch || firstMatch == -1 ) )
			{
				String t = text.substring( ftp + 1, text.length() - 1 );

				if ( ftpPattern.matcher( t ).matches() )
					firstMatch = ftp + 1;

				else
				{
					ftp = text.indexOf( " ftp", ftp + 1 );

					if ( ftp != -1 && ( ftp < firstMatch || firstMatch == -1 ) )
						retry = true;
				}
			}
		}

		return firstMatch;
	}
}
