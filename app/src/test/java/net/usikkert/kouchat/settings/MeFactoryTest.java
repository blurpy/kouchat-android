
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

package net.usikkert.kouchat.settings;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import net.usikkert.kouchat.misc.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

/**
 * Test of {@link MeFactory}.
 *
 * @author Christian Ihle
 */
@SuppressWarnings("HardCodedStringLiteral")
public class MeFactoryTest {

    @Rule
    public RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    private MeFactory meFactory;

    @Before
    public void setUp() {
        meFactory = new MeFactory();
    }

    @Test
    public void createMeShouldSetUserAsMe() {
        final User me = meFactory.createMe();

        assertTrue(me.isMe());
    }

    @Test
    public void createMeShouldSetOperatingSystemFromSystemProperty() {
        System.setProperty("os.name", "Wintendo");

        final User me = meFactory.createMe();

        assertEquals("Wintendo", me.getOperatingSystem());
    }

    @Test
    public void createMeShouldSetNickNameWithCapitalFirstLetterFromUserNameInSystemProperty() {
        System.setProperty("user.name", "superman");

        final User me = meFactory.createMe();

        assertEquals("Superman", me.getNick());
    }

    @Test
    public void createMeShouldSetNickNameToFirstTenLettersOfUserName() {
        System.setProperty("user.name", "theAmazingSpiderman");

        final User me = meFactory.createMe();

        assertEquals("TheAmazing", me.getNick());
    }

    @Test
    public void createMeShouldSetNickNameToFirstWordInUserName() {
        System.setProperty("user.name", "super kou");

        final User me = meFactory.createMe();

        assertEquals("Super", me.getNick());
    }

    @Test
    public void createMeShouldSetNickNameToFirstTenLettersOfFirstWordInUserName() {
        System.setProperty("user.name", "ultrasuperduper kou");

        final User me = meFactory.createMe();

        assertEquals("Ultrasuper", me.getNick());
    }

    @Test
    public void createMeShouldSetNickNameToCodeIfUserNameIsMissing() {
        System.clearProperty("user.name");

        final User me = meFactory.createMe();

        assertEquals(String.valueOf(me.getCode()), me.getNick());
    }

    @Test
    public void createMeShouldSetNickNameToCodeIfUserNameIsInvalid() {
        System.setProperty("user.name", "kou$");

        final User me = meFactory.createMe();

        assertEquals(String.valueOf(me.getCode()), me.getNick());
    }

    @Test
    public void createMeShouldUseUniqueCodes() {
        final List<Integer> uniqueCodes = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            final User me = meFactory.createMe();
            final int code = me.getCode();

            assertFalse(uniqueCodes.contains(code));
            assertTrue(code >= 10000000);
            assertTrue(code <= 20000000);

            uniqueCodes.add(code);
        }

        assertEquals(100, uniqueCodes.size());
    }

    @Test
    public void createMeShouldSetLogonTimeToNow() {
        final User me = meFactory.createMe();

        final long timeSinceLogon = System.currentTimeMillis() - me.getLogonTime();

        assertTrue(timeSinceLogon >= 0);
        assertTrue(timeSinceLogon < 1000);
    }

    @Test
    public void createMeShouldSetLastIdleToNow() {
        final User me = meFactory.createMe();

        final long timeSinceLastIdle = System.currentTimeMillis() - me.getLastIdle();

        assertTrue(timeSinceLastIdle >= 0);
        assertTrue(timeSinceLastIdle < 1000);
    }
}
