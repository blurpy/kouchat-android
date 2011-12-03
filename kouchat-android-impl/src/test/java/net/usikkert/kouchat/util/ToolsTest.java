
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

package net.usikkert.kouchat.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Test of {@link Tools}.
 *
 * @author Christian Ihle
 */
public class ToolsTest
{
	/**
	 * Tests that capitalization of the first letter in a word works as expected.
	 */
	@Test
	public void testCapitalizeFirstLetter()
	{
		assertNull( Tools.capitalizeFirstLetter( null ) );
		assertEquals( "Monkey", Tools.capitalizeFirstLetter( "monkey" ) );
		assertEquals( "Kou", Tools.capitalizeFirstLetter( "kou" ) );
		assertEquals( "Up", Tools.capitalizeFirstLetter( "up" ) );
		assertEquals( "O", Tools.capitalizeFirstLetter( "o" ) );
		assertEquals( "-", Tools.capitalizeFirstLetter( "-" ) );
		assertEquals( "", Tools.capitalizeFirstLetter( "" ) );
		assertEquals( "CAKE", Tools.capitalizeFirstLetter( "CAKE" ) );
		assertEquals( "123", Tools.capitalizeFirstLetter( "123" ) );
	}

	/**
	 * Tests the shortening of words.
	 */
	@Test
	public void testShorten()
	{
		assertNull( Tools.shorten( null, 5 ) );
		assertEquals( "Monkey", Tools.shorten( "Monkey", 12 ) );
		assertEquals( "Monkey", Tools.shorten( "Monkey", 6 ) );
		assertEquals( "Monke", Tools.shorten( "Monkey", 5 ) );
		assertEquals( "M", Tools.shorten( "Monkey", 1 ) );
		assertEquals( "", Tools.shorten( "Monkey", 0 ) );
		assertEquals( "", Tools.shorten( "Monkey", -5 ) );
	}

	/**
	 * Tests getting the file extension from a file name.
	 */
	@Test
	public void testGetFileExtension()
	{
		assertNull( Tools.getFileExtension( null ) );
		assertEquals( "", Tools.getFileExtension( "file" ) );
		assertEquals( ".txt", Tools.getFileExtension( "file.txt" ) );
		assertEquals( ".", Tools.getFileExtension( "file." ) );
		assertEquals( ".txt", Tools.getFileExtension( ".txt" ) );
		assertEquals( ".jpg", Tools.getFileExtension( "image.txt.jpg" ) );
	}

	/**
	 * Tests getting the base name from a file name.
	 */
	@Test
	public void testGetFileBaseName()
	{
		assertNull( Tools.getFileBaseName( null ) );
		assertEquals( "file", Tools.getFileBaseName( "file" ) );
		assertEquals( "file", Tools.getFileBaseName( "file.txt" ) );
		assertEquals( "file", Tools.getFileBaseName( "file." ) );
		assertEquals( "", Tools.getFileBaseName( ".txt" ) );
		assertEquals( "image.txt", Tools.getFileBaseName( "image.txt.jpg" ) );
	}

	/**
	 * Test finding how many percent a fraction is of the total.
	 */
	@Test
	public void testPercent()
	{
		assertEquals( 0.08, Tools.percent( 1, 1250 ), 10 );
		assertEquals( 25, Tools.percent( 50, 200 ), 10 );
		assertEquals( 50, Tools.percent( 5, 10 ),  10 );
		assertEquals( 100, Tools.percent( 10, 10 ), 10 );
		assertEquals( 200, Tools.percent( 60, 30 ), 10 );
	}

	/**
	 * Test finding the fraction from the percent of the total.
	 */
	@Test
	public void testPercentOf()
	{
		assertEquals( 1, Tools.percentOf( 0.08, 1250 ), 10 );
		assertEquals( 50, Tools.percentOf( 25, 200 ),  10 );
		assertEquals( 5, Tools.percentOf( 50, 10 ), 10 );
		assertEquals( 10, Tools.percentOf( 100, 10 ), 10 );
		assertEquals( 60, Tools.percentOf( 200, 30 ), 10 );
	}
}
