
/***************************************************************************
 *   Copyright 2006-2019 by Christian Ihle                                 *
 *   contact@kouchat.net                                                   *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package net.usikkert.kouchat.android.smiley;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;

import net.usikkert.kouchat.junit.ExpectedException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link SmileyLocator}.
 *
 * @author Christian Ihle
 */
public class SmileyLocatorTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SmileyLocator locator;

    @Before
    public void setUp() {
        final HashSet<String> smileyCodes = new HashSet<String>();
        smileyCodes.add(":)");
        smileyCodes.add(":(");
        smileyCodes.add(":D");

        locator = new SmileyLocator(smileyCodes);
    }

    @Test
    public void constructorShouldThrowExceptionIfSmileyCodesIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Smiley codes can not be null");

        new SmileyLocator(null);
    }

    @Test
    public void findSmileysShouldThrowExceptionIfTextIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Text can not be null");

        locator.findSmileys(null);
    }

    @Test
    public void findSmileysShouldHandleEmptyStrings() {
        final List<Smiley> smileys = locator.findSmileys("");

        assertTrue(smileys.isEmpty());
    }

    @Test
    public void findSmileysShouldHandleStringsWithNoSmileys() {
        final List<Smiley> smileys = locator.findSmileys("No smileys here!");

        assertTrue(smileys.isEmpty());
    }

    @Test
    public void findSmileysShouldFindTextWithOnlyASmiley() {
        final List<Smiley> smileys = locator.findSmileys(":)");

        assertEquals(1, smileys.size());

        verifySmiley(smileys, ":)", 0, 2);
    }

    @Test
    public void findSmileysShouldFindTextWithSmileyInTheMiddle() {
        final List<Smiley> smileys = locator.findSmileys("hey :) you");

        assertEquals(1, smileys.size());

        verifySmiley(smileys, ":)", 4, 6);
    }

    @Test
    public void findSmileysShouldFindMultipleSmileysWithOtherCharactersBetween() {
        final List<Smiley> smileys = locator.findSmileys(":) - :D - :(");

        assertEquals(3, smileys.size());

        verifySmiley(smileys, ":)", 0, 2);
        verifySmiley(smileys, ":D", 5, 7);
        verifySmiley(smileys, ":(", 10, 12);
    }

    @Test
    public void findSmileysShouldFindMultipleSmileysWithJustSpaceBetween() {
        final List<Smiley> smileys = locator.findSmileys(":) :D :(");

        assertEquals(3, smileys.size());

        verifySmiley(smileys, ":)", 0, 2);
        verifySmiley(smileys, ":D", 3, 5);
        verifySmiley(smileys, ":(", 6, 8);
    }

    @Test
    public void findSmileysShouldFindMultipleOfTheSameSmileyWithOtherCharactersBetween() {
        final List<Smiley> smileys = locator.findSmileys(":) - :) - :)");

        assertEquals(3, smileys.size());

        verifySmiley(smileys, ":)", 0, 2);
        verifySmiley(smileys, ":)", 5, 7);
        verifySmiley(smileys, ":)", 10, 12);
    }

    @Test
    public void findSmileysShouldFindMultipleOfTheSameSmileyWithJustSpaceBetween() {
        final List<Smiley> smileys = locator.findSmileys(":) :) :)");

        assertEquals(3, smileys.size());

        verifySmiley(smileys, ":)", 0, 2);
        verifySmiley(smileys, ":)", 3, 5);
        verifySmiley(smileys, ":)", 6, 8);
    }

    @Test
    public void findSmileysShouldFindMultipleOfTheSameSmileyWithSeveralSpacesBetween() {
        final List<Smiley> smileys = locator.findSmileys(":)  :)   :)");

        assertEquals(3, smileys.size());

        verifySmiley(smileys, ":)", 0, 2);
        verifySmiley(smileys, ":)", 4, 6);
        verifySmiley(smileys, ":)", 9, 11);
    }

    @Test
    public void findSmileysShouldNotFindSmileysWithoutSpaceBefore() {
        final List<Smiley> smileys = locator.findSmileys("A:)");

        assertTrue(smileys.isEmpty());
    }

    @Test
    public void findSmileysShouldNotFindSmileysWithoutSpaceAfter() {
        final List<Smiley> smileys = locator.findSmileys(":)B");

        assertTrue(smileys.isEmpty());
    }

    @Test
    public void findSmileysShouldNotFindSmileysWithoutSpaceBeforeOrAfter() {
        final List<Smiley> smileys = locator.findSmileys("A:)B");

        assertTrue(smileys.isEmpty());
    }

    @Test
    public void findSmileysShouldNotFindSmileysStuckToEachOther() {
        final List<Smiley> smileys = locator.findSmileys(":):)");

        assertTrue(smileys.isEmpty());
    }

    private void verifySmiley(final List<Smiley> smileys, final String code,
                              final int startPosition, final int endPosition) {
        boolean smileyFound = false;

        for (final Smiley smiley : smileys) {
            if (smiley.getCode().equals(code) &&
                    smiley.getStartPosition() == startPosition &&
                    smiley.getEndPosition() == endPosition) {
                smileyFound = true;
            }
        }

        assertTrue(smileyFound);
    }
}
