
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.swing.ImageIcon;

import org.junit.Test;

/**
 * Test of {@link SmileyDocumentFilter}.
 *
 * @author Christian Ihle
 */
public class SmileyDocumentFilterTest
{
	/** The image loader. */
	private final ImageLoader imageLoader;

	/** The smiley filter being tested. */
	private final SmileyDocumentFilter filter;

	/**
	 * Constructor.
	 */
	public SmileyDocumentFilterTest()
	{
		imageLoader = new ImageLoader();
		filter = new SmileyDocumentFilter( true, imageLoader );
	}

	/**
	 * Tests that a smiley is detected when the smiley has no text before or after.
	 */
	@Test
	public void testSmileyHasWhitespace1()
	{
		Smiley smiley = new Smiley( 0, new ImageIcon( "" ), ":)" );
		assertTrue( filter.smileyHasWhitespace( smiley, ":)" ) );
	}

	/**
	 * Tests that a smiley is detected when the smiley has no text before.
	 */
	@Test
	public void testSmileyHasWhitespace2()
	{
		Smiley smiley = new Smiley( 1, new ImageIcon( "" ), ":)" );
		assertTrue( filter.smileyHasWhitespace( smiley, " :)" ) );
	}

	/**
	 * Tests that a smiley is detected when the smiley has no text after.
	 */
	@Test
	public void testSmileyHasWhitespace3()
	{
		Smiley smiley = new Smiley( 0, new ImageIcon( "" ), ":)" );
		assertTrue( filter.smileyHasWhitespace( smiley, ":) " ) );
	}

	/**
	 * Tests that a smiley is detected when the smiley has whitespace before and after.
	 */
	@Test
	public void testSmileyHasWhitespace4()
	{
		Smiley smiley = new Smiley( 1, new ImageIcon( "" ), ":)" );
		assertTrue( filter.smileyHasWhitespace( smiley, " :) " ) );
	}

	/**
	 * Tests that a smiley is not detected when the smiley has non-whitespace text around.
	 */
	@Test
	public void testSmileyHasNoWhitespace()
	{
		Smiley smiley = new Smiley( 0, new ImageIcon( "" ), ":)" );
		assertFalse( filter.smileyHasWhitespace( smiley, ":):)" ) );
	}

	/**
	 * Tests that the correct smiley is found when there are several,
	 * but only one with whitespace.
	 */
	@Test
	public void testFindSmiley()
	{
		Smiley smiley = filter.findSmiley( "Test :):) :) :):) Test", 0 );

		assertNotNull( smiley );
		assertEquals( 10, smiley.getStartPosition() );
		assertEquals( 12, smiley.getStopPosition() );
		assertEquals( ":)", smiley.getCode() );
		assertNotNull( smiley.getIcon() );
	}

	/**
	 * Tests that all the correct smileys are found when there are several valid.
	 */
	@Test
	public void testFindAllSmileys()
	{
		final String text = ":$ Test :p :S :) 8) :) ;);) ;) Test";

		Smiley smiley1 = filter.findSmiley( text, 0 );
		assertNotNull( smiley1 );
		assertEquals( 0, smiley1.getStartPosition() );
		assertEquals( 2, smiley1.getStopPosition() );
		assertEquals( ":$", smiley1.getCode() );

		Smiley smiley2 = filter.findSmiley( text, smiley1.getStopPosition() );
		assertNotNull( smiley2 );
		assertEquals( 8, smiley2.getStartPosition() );
		assertEquals( 10, smiley2.getStopPosition() );
		assertEquals( ":p", smiley2.getCode() );

		Smiley smiley3 = filter.findSmiley( text, smiley2.getStopPosition() );
		assertNotNull( smiley3 );
		assertEquals( 11, smiley3.getStartPosition() );
		assertEquals( 13, smiley3.getStopPosition() );
		assertEquals( ":S", smiley3.getCode() );

		Smiley smiley4 = filter.findSmiley( text, smiley3.getStopPosition() );
		assertNotNull( smiley4 );
		assertEquals( 14, smiley4.getStartPosition() );
		assertEquals( 16, smiley4.getStopPosition() );
		assertEquals( ":)", smiley4.getCode() );

		Smiley smiley5 = filter.findSmiley( text, smiley4.getStopPosition() );
		assertNotNull( smiley5 );
		assertEquals( 17, smiley5.getStartPosition() );
		assertEquals( 19, smiley5.getStopPosition() );
		assertEquals( "8)", smiley5.getCode() );

		Smiley smiley6 = filter.findSmiley( text, smiley5.getStopPosition() );
		assertNotNull( smiley6 );
		assertEquals( 20, smiley6.getStartPosition() );
		assertEquals( 22, smiley6.getStopPosition() );
		assertEquals( ":)", smiley6.getCode() );

		Smiley smiley7 = filter.findSmiley( text, smiley6.getStopPosition() );
		assertNotNull( smiley7 );
		assertEquals( 28, smiley7.getStartPosition() );
		assertEquals( 30, smiley7.getStopPosition() );
		assertEquals( ";)", smiley7.getCode() );

		assertNull( filter.findSmiley( text, smiley7.getStopPosition() ) );
	}

	/**
	 * Tests that all the different smileys are found.
	 */
	@Test
	public void testAllSmileys()
	{
		final Smiley smile = filter.findSmiley( ":)", 0 );
		assertNotNull( smile );
		assertEquals( ":)", smile.getCode() );
		assertNotNull( smile.getIcon() );

		final Smiley sad = filter.findSmiley( ":(", 0 );
		assertNotNull( sad );
		assertEquals( ":(", sad.getCode() );
		assertNotNull( sad.getIcon() );

		final Smiley tongue = filter.findSmiley( ":p", 0 );
		assertNotNull( tongue );
		assertEquals( ":p", tongue.getCode() );
		assertNotNull( tongue.getIcon() );

		final Smiley teeth = filter.findSmiley( ":D", 0 );
		assertNotNull( teeth );
		assertEquals( ":D", teeth.getCode() );
		assertNotNull( teeth.getIcon() );

		final Smiley wink = filter.findSmiley( ";)", 0 );
		assertNotNull( wink );
		assertEquals( ";)", wink.getCode() );
		assertNotNull( wink.getIcon() );

		final Smiley omg = filter.findSmiley( ":O", 0 );
		assertNotNull( omg );
		assertEquals( ":O", omg.getCode() );
		assertNotNull( omg.getIcon() );

		final Smiley angry = filter.findSmiley( ":@", 0 );
		assertNotNull( angry );
		assertEquals( ":@", angry.getCode() );
		assertNotNull( angry.getIcon() );

		final Smiley confused = filter.findSmiley( ":S", 0 );
		assertNotNull( confused );
		assertEquals( ":S", confused.getCode() );
		assertNotNull( confused.getIcon() );

		final Smiley cry = filter.findSmiley( ";(", 0 );
		assertNotNull( cry );
		assertEquals( ";(", cry.getCode() );
		assertNotNull( cry.getIcon() );

		final Smiley embarrassed = filter.findSmiley( ":$", 0 );
		assertNotNull( embarrassed );
		assertEquals( ":$", embarrassed.getCode() );
		assertNotNull( embarrassed.getIcon() );

		final Smiley shade = filter.findSmiley( "8)", 0 );
		assertNotNull( shade );
		assertEquals( "8)", shade.getCode() );
		assertNotNull( shade.getIcon() );
	}

	/**
	 * Test that nothing is returned for unregistered smileys.
	 */
	@Test
	public void testUnknownSmileys()
	{
		assertNull( filter.findSmiley( ":/", 0 ) );
		assertNull( filter.findSmiley( "#)", 0 ) );
		assertNull( filter.findSmiley( ":", 0 ) );
		assertNull( filter.findSmiley( ")", 0 ) );
	}
}
