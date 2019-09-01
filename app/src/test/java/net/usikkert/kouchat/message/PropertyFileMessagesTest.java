
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

package net.usikkert.kouchat.message;

import static org.junit.Assert.*;

import java.util.MissingResourceException;

import net.usikkert.kouchat.junit.ExpectedException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test of {@link PropertyFileMessages}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class PropertyFileMessagesTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private PropertyFileMessages messages;

    @Before
    public void setUp() {
        messages = new PropertyFileMessages("test-messages");
    }

    @Test
    public void constructorShouldThrowExceptionIfBaseNameIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Base name can not be empty");

        new PropertyFileMessages(null);
    }

    @Test
    public void constructorShouldThrowExceptionIfBaseNameIsEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Base name can not be empty");

        new PropertyFileMessages(" ");
    }

    @Test
    public void constructorShouldThrowExceptionIfBaseNameIsInvalid() {
        expectedException.expect(MissingResourceException.class);
        expectedException.expectMessageContaining("Can't find bundle for base name wrong"); // And some locale

        new PropertyFileMessages("wrong");
    }

    @Test
    public void hasMessageShouldThrowExceptionIfKeyIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Key can not be empty");

        messages.hasMessage(null);
    }

    @Test
    public void hasMessageShouldThrowExceptionIfKeyIsEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Key can not be empty");

        messages.hasMessage(" ");
    }

    @Test
    public void hasMessageShouldBeTrueIfKeyExist() {
        assertTrue(messages.hasMessage("test.string1"));
        assertTrue(messages.hasMessage("test.string2"));
        assertTrue(messages.hasMessage("test.hello"));
    }

    @Test
    public void hasMessageShouldBeFalseIfKeyDontExist() {
        assertFalse(messages.hasMessage("test.string10"));
        assertFalse(messages.hasMessage("test.hi"));
        assertFalse(messages.hasMessage("nothing"));
    }

    @Test
    public void getMessageShouldThrowExceptionIfKeyIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Key can not be empty");

        messages.getMessage(null);
    }

    @Test
    public void getMessageShouldThrowExceptionIfKeyIsEmpty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Key can not be empty");

        messages.getMessage(" ");
    }

    @Test
    public void getMessageShouldThrowExceptionIfKeyDontExist() {
        expectedException.expect(MissingResourceException.class);
        expectedException.expectMessage("Can't find resource for bundle java.util.PropertyResourceBundle, key nothing");

        messages.getMessage("nothing");
    }

    @Test
    public void getMessageShouldReturnTheCorrectValue() {
        assertEquals("This is the first string", messages.getMessage("test.string1"));
        assertEquals("This is the second string", messages.getMessage("test.string2"));
    }

    @Test
    public void getMessageShouldReplaceTheCorrectArguments() {
        assertEquals("Say hello to Nelly from Niles!", messages.getMessage("test.hello", "Nelly", "Niles"));
        assertEquals("Say hello to 3 from false!", messages.getMessage("test.hello", 3L, false));

    }
}
