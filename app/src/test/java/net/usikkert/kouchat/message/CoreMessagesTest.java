
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

import org.junit.Before;
import org.junit.Test;

/**
 * Test of {@link CoreMessages}.
 *
 * @author Christian Ihle
 */
public class CoreMessagesTest {

    private CoreMessages coreMessages;

    @Before
    public void setUp() {
        coreMessages = new CoreMessages();
    }

    @Test
    public void getMessageShouldReturnMessageFromCoreProperties() {
        final String message = coreMessages.getMessage("core.settings.errorPopup.saveFailed", 20);

        assertEquals("Settings could not be saved:\n20", message);
    }

    @Test
    public void hasMessageShouldOnlyReturnTrueForMessagesInCoreProperties() {
        assertTrue(coreMessages.hasMessage("core.settings.errorPopup.saveFailed"));
        assertFalse(coreMessages.hasMessage("console.quit.message"));
        assertFalse(coreMessages.hasMessage("swing.button.ok"));
    }
}
