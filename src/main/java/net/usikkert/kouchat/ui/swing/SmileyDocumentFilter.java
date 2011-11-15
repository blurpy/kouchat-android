
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

import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.usikkert.kouchat.misc.Settings;
import net.usikkert.kouchat.util.Validate;

/**
 * This is a document filter that checks for text smiley codes added to
 * a {@link StyledDocument}, and replaces them with images.
 *
 * @author Christian Ihle
 */
public class SmileyDocumentFilter extends DocumentFilter
{
	/**
	 * If this document filter is the only document filter used.
	 * This must be true if it is, or the text will not be visible.
	 * If this is not the only filter, then this must be false, or
	 * the same text will be shown several times.
	 */
	private final boolean standAlone;

	/** The available smileys. */
	private final SmileyMap smileyMap;

	/** The settings. */
	private final Settings settings;

	/**
	 * Constructor.
	 *
	 * @param standAlone If this is the only document filter used.
	 * @param imageLoader The image loader.
	 */
	public SmileyDocumentFilter( final boolean standAlone, final ImageLoader imageLoader )
	{
		Validate.notNull( imageLoader, "Image loader can not be null" );

		this.standAlone = standAlone;
		smileyMap = new SmileyMap( imageLoader );
		settings = Settings.getSettings();
	}

	/**
	 * Checks if any text smiley codes are in the text, and replaces them
	 * with the corresponding image. But only if smileys are enabled in the settings.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public void insertString( final FilterBypass fb, final int offset, final String text,
			final AttributeSet attr ) throws BadLocationException
	{
		if ( standAlone )
			super.insertString( fb, offset, text, attr );

		if ( !settings.isSmileys() )
			return;

		// Make a copy now, or else it could change if another message comes
		final MutableAttributeSet smileyAttr = (MutableAttributeSet) attr.copyAttributes();

		// Do this in the background so the text wont lag
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{
				Smiley smiley = findSmiley( text, 0 );
				StyledDocument doc = (StyledDocument) fb.getDocument();

				while ( smiley != null )
				{
					if ( !smileyIconRegistered( smileyAttr, smiley ) )
						registerSmileyIcon( smileyAttr, smiley );

					registerSmileyLocation( doc, smiley, offset, smileyAttr );
					smiley = findSmiley( text, smiley.getStopPosition() );
				}
			}
		} );
	}

	/**
	 * Checks if the smiley icon already exists in the attribute set.
	 *
	 * <p>That will be the case if there was an identical smiley added right
	 * before this. If a different smiley is added in between then the first
	 * is overwritten.</p>
	 *
	 * @param smileyAttr The attribute set for the inserted string.
	 * @param smiley The smiley containing the icon to check.
	 * @return If the icon was found.
	 */
	private boolean smileyIconRegistered( final MutableAttributeSet smileyAttr, final Smiley smiley )
	{
		return smileyAttr.containsAttribute( StyleConstants.IconAttribute, smiley.getIcon() );
	}

	/**
	 * Sets the current smiley icon in the attribute set.
	 *
	 * @param smileyAttr The attribute set for the inserted string.
	 * @param smiley The smiley containing the icon to set.
	 */
	private void registerSmileyIcon( final MutableAttributeSet smileyAttr, final Smiley smiley )
	{
		StyleConstants.setIcon( smileyAttr, smiley.getIcon() );
	}

	/**
	 * Adds a new smiley location to the document, using the last registered
	 * smiley icon.
	 *
	 * @param doc The document to add the smiley to.
	 * @param smiley The smiley to add.
	 * @param offset Offset to start position.
	 * @param smileyAttr The attribute set for the inserted string.
	 */
	private void registerSmileyLocation( final StyledDocument doc, final Smiley smiley,
			final int offset, final MutableAttributeSet smileyAttr )
	{
		int stopPos = smiley.getStopPosition();
		int startPos = smiley.getStartPosition();
		doc.setCharacterAttributes( offset + startPos, stopPos - startPos, smileyAttr, false );
	}

	/**
	 * Returns the first matching smiley in the text, starting from the specified offset.
	 *
	 * @param text The text to find smileys in.
	 * @param offset Where in the text to begin the search.
	 * @return The first matching smiley in the text, or <code>null</code> if
	 *         none were found.
	 */
	protected Smiley findSmiley( final String text, final int offset )
	{
		int firstMatch = -1;
		Smiley smiley = null;

		for ( String smileyText : smileyMap.getTextSmileys() )
		{
			int smileyPos = 0;
			int loopOffset = offset;

			// Needs this extra loop because of the required whitespace check,
			// which happens after the first smiley is found.
			// This makes sure :):) :) :):) only finds the smiley in the center.
			do
			{
				smileyPos = text.indexOf( smileyText, loopOffset );

				if ( newSmileyFound( smileyPos, firstMatch ) )
				{
					Smiley tmpSmiley =
						new Smiley( smileyPos, smileyMap.getSmiley( smileyText ), smileyText );

					if ( smileyHasWhitespace( tmpSmiley, text ) )
					{
						smiley = tmpSmiley;
						firstMatch = smileyPos;
					}
				}

				loopOffset = smileyPos + 1;
			}

			while ( smileyPos != -1 );
		}

		return smiley;
	}

	/**
	 * Checks if a new smiley is found.
	 *
	 * <p>A new smiley is found if:</p>
	 * <ul>
	 *   <li>An actual smiley was found, and</li>
	 *   <li>No others smileys have been found before, or</li>
	 *   <li>This smiley is earlier in the text than the previous smiley that was found.</li>
	 * </ul>
	 *
	 * @param smileyPos The position of the smiley in the text.
	 * @param firstMatch The position of the previously found smiley in the text.
	 * @return If a new smiley has been found.
	 */
	private boolean newSmileyFound( final int smileyPos, final int firstMatch )
	{
		return smileyPos != -1 && ( smileyPos < firstMatch || firstMatch == -1 );
	}

	/**
	 * Checks if the smiley is surrounded by some sort of whitespace.
	 *
	 * <p>Whitespace can be whatever defined in {@link Character#isWhitespace(char)}.</p>
	 *
	 * @param smiley The smiley to check.
	 * @param text The text where the smiley is taken from.
	 * @return If the smiley is surrounded by whitespace.
	 */
	protected boolean smileyHasWhitespace( final Smiley smiley, final String text )
	{
		int leftIndex = smiley.getStartPosition() - 1;
		boolean leftOk = false;

		if ( leftIndex <= 0 )
			leftOk = true;
		else
			leftOk = Character.isWhitespace( text.charAt( leftIndex ) );

		if ( !leftOk )
			return false;

		int rightIndex = smiley.getStopPosition();
		boolean rightOk = false;

		if ( rightIndex >= text.length() )
			rightOk = true;
		else
			rightOk = Character.isWhitespace( text.charAt( rightIndex ) );

		return rightOk;
	}
}
